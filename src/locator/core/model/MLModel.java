package locator.core.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import jdk7.wrapper.JCompiler;
import locator.aux.extractor.FeatureGenerator;
import locator.common.config.Constant;
import locator.common.config.Identifier;
import locator.common.java.JavaFile;
import locator.common.java.Subject;
import locator.common.util.ExecuteCommand;
import locator.common.util.LevelLogger;
import locator.common.util.Pair;
import locator.common.util.Utils;
import locator.core.LineInfo;
import locator.core.model.predict.PredicateFilter;
import locator.core.run.Runner;
import locator.inst.visitor.MultiLinePredicateInstrumentVisitor;
import locator.inst.visitor.PredicateInstrumentVisitor;

public abstract class MLModel extends Model {

	protected MLModel(String modelName) {
		super(modelName, "predicates_backup.txt");
		__name__ = "@MLModel";
	}

	public boolean trainModel(Subject subject) {
		if (!Constant.RE_TRAIN_MODEL) {
			if (modelExist(subject)) {
				return true;
			}
		}
		if (!prepare(subject)) {
			return false;
		}
		// train model
		try {
			LevelLogger.info(">>>>>> Begin Trainning ...");
			ExecuteCommand.executeTrain(subject, this);
			LevelLogger.info(">>>>>> End Trainning !");
		} catch (Exception e) {
			LevelLogger.error("Failed to train model", e);
			return false;
		}
		return true;
	}

	public boolean evaluate(Subject subject) {
		// evaluate model
		try {
			LevelLogger.info(">>>>>> Begin Evaluating ...");
			ExecuteCommand.executeEvaluate(subject, this);
			LevelLogger.info(">>>>>> End Evaluating !");
		} catch (Exception e) {
			LevelLogger.error("Failed to evaluate model", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean prepare(Subject subject) {
		// get train features
		String outPath = _outPath + "/" + subject.getName() + "/" + subject.getNameAndId();

		// create necessary directories
		Utils.pathGuarantee(outPath + "/var", outPath + "/expr", outPath + "/cluster", outPath + "/pred",
				getPredictResultPath(subject));

		String targetVarPath = outPath + "/var/" + subject.getNameAndId() + ".var.csv";
		String targetExprPath = outPath + "/expr/" + subject.getNameAndId() + ".expr.csv";
		String srcPath = subject.getHome() + subject.getSsrc();
		
		FeatureGenerator.generateTrainVarFeatures(srcPath, targetVarPath);
		FeatureGenerator.generateTrainExprFeatures(srcPath, targetExprPath);
		
		return true;
	}

	public boolean instrumentPredicates(Map<String, Map<Integer, List<Pair<String, String>>>> file2Line2Predicates,
			boolean useSober) {
		MultiLinePredicateInstrumentVisitor instrumentVisitor = new MultiLinePredicateInstrumentVisitor(useSober);
		for (Entry<String, Map<Integer, List<Pair<String, String>>>> entry : file2Line2Predicates.entrySet()) {
			String fileName = entry.getKey();
			Map<Integer, List<Pair<String, String>>> allPreds = entry.getValue();
			CompilationUnit unit = JavaFile.genAST(fileName);
			instrumentVisitor.setCondition(allPreds);
			unit.accept(instrumentVisitor);
			JavaFile.writeStringToFile(fileName, unit.toString());
		}
		return true;
	}

	public Map<String, Map<Integer, List<Pair<String, String>>>> getAllPredicates(Subject subject,
			Set<String> allStatements, boolean useSober) {

		Map<String, Map<Integer, List<Pair<String, String>>>> file2Line2Predicates = null;

		if (Constant.BOOL_RECOVER_PREDICATE_FROM_FILE) {
			file2Line2Predicates = Utils.recoverPredicates(subject, _predicates_backup_file);
		}
		if (file2Line2Predicates != null) {
			return file2Line2Predicates;
		}
		// parse all object type
//		ExprFilter.init(subject);

		String srcPath = subject.getHome() + subject.getSsrc();

		List<String> varFeatures = new ArrayList<String>();
		List<String> exprFeatures = new ArrayList<String>();
		Map<String, LineInfo> lineInfoMapping = mapLine2Features(srcPath, allStatements, varFeatures, exprFeatures);

		Map<String, Map<String, List<Pair<String, String>>>> location2varName2Conditions = predict(subject, varFeatures,
				exprFeatures, lineInfoMapping);

		file2Line2Predicates = new HashMap<>();
		// instrument each condition one by one and compute coverage
		// information for each predicate
		JCompiler compiler = JCompiler.getInstance();
		for (Map.Entry<String, Map<String, List<Pair<String, String>>>> entry : location2varName2Conditions
				.entrySet()) {
			final LineInfo info = lineInfoMapping.get(entry.getKey());
			int line = info.getLine();
			Set<String> alreadyVisited = new HashSet<>();
			if (entry.getValue() != null && entry.getValue().size() > 0) {
				String relJavaPath = info.getRelJavaPath();
				String javaFile = srcPath + Constant.PATH_SEPARATOR + relJavaPath;
				// the source file will instrumented iteratively, before which
				// the original source file should be saved
				PredicateInstrumentVisitor newPredicateInstrumentVisitor = new PredicateInstrumentVisitor(null, line);
				List<Pair<String, String>> legalConditions = new ArrayList<>();
				// read original file once
				String source = JavaFile.readFileToString(javaFile);

				for (Entry<String, List<Pair<String, String>>> innerEntry : entry.getValue().entrySet()) {
					int count = 0;
					int allConditionCount = innerEntry.getValue().size();
					int currentConditionCount = 1;
					for (Pair<String, String> condition : innerEntry.getValue()) {
						LevelLogger.info("Validate conditions by compiling : [" + currentConditionCount + "/"
								+ allConditionCount + "].");
						currentConditionCount++;

						// filter duplicate
						if (alreadyVisited.contains(condition.getFirst())) {
							continue;
						}
						alreadyVisited.add(condition.getFirst());
						alreadyVisited.add("!(" + condition.getFirst() + ")");
						// instrument one condition statement into source file
						CompilationUnit compilationUnit = (CompilationUnit) JavaFile.genASTFromSource(source,
								ASTParser.K_COMPILATION_UNIT);

						List<Pair<String, String>> onePredicate = new ArrayList<>();
						onePredicate.add(condition);
						newPredicateInstrumentVisitor.setCondition(onePredicate);

						compilationUnit.accept(newPredicateInstrumentVisitor);

						if (compiler.compile(subject, relJavaPath, compilationUnit.toString())) {
							legalConditions.add(condition);
							if (!useSober) {
								// add opposite conditions as well
								Pair<String, String> otherSide = new Pair<>();
								otherSide.setFirst("!(" + condition.getFirst() + ")");
								otherSide.setSecond(condition.getSecond());
								legalConditions.add(otherSide);
							}
							LevelLogger.info("Passed build : " + condition.toString() + "\t ADD \t");
							count++;
							// only keep partial predicates "top K"
							if (count >= Constant.TOP_K_PREDICATES_FOR_EACH_VAR) {
								break;
							}
						} else {
							LevelLogger.info("Build failed : " + condition.toString());
						}
					}
				}

				if (legalConditions.size() > 0) {
					Map<Integer, List<Pair<String, String>>> line2Predicate = file2Line2Predicates.get(javaFile);
					if (line2Predicate == null) {
						line2Predicate = new HashMap<>();
					}
					List<Pair<String, String>> predicates = line2Predicate.get(line);
					if (predicates == null) {
						predicates = new ArrayList<>();
					}
					predicates.addAll(legalConditions);
					line2Predicate.put(line, predicates);
					file2Line2Predicates.put(javaFile, line2Predicate);
				}
				if (!compiler.compile(subject, relJavaPath, source)) {
					if (!Runner.compileSubject(subject)) {
						LevelLogger.error(
								__name__ + "#getAllPredicates ERROR : compile original source failed : " + javaFile);
					}
				}
			} // end of "conditionsForRightVars != null"
		} // end of "for(String stmt : allStatements)"

		LevelLogger.debug("-------------------FOR DEBUG----------------------");
		Utils.printPredicateInfo(file2Line2Predicates, subject, _predicates_backup_file);
		file2Line2Predicates = Utils.recoverPredicates(subject, _predicates_backup_file);
		return file2Line2Predicates;
	}

	private Map<String, LineInfo> mapLine2Features(String srcPath, Set<String> allStatements, List<String> varFeatures,
			List<String> exprFeatures) {
		Map<String, LineInfo> lineInfoMapping = new HashMap<String, LineInfo>();
		for (String stmt : allStatements) {

			String[] stmtInfo = stmt.split("#");
			if (stmtInfo.length != 2) {
				LevelLogger.error(__name__ + "#getAllPredicates statement parse error : " + stmt);
				System.exit(0);
			}
			Integer methodID = Integer.valueOf(stmtInfo[0]);
			int line = Integer.parseInt(stmtInfo[1]);
			String methodString = Identifier.getMessage(methodID);
			LevelLogger.info("Current statement  : **" + methodString + "#" + line + "**");
			String[] methodInfo = methodString.split("#");
			if (methodInfo.length < 4) {
				LevelLogger.error(__name__ + "#getAllPredicates method info parse error : " + methodString);
				System.exit(0);
			}
			String clazz = methodInfo[0].replace(".", Constant.PATH_SEPARATOR);
			int index = clazz.indexOf("$");
			if (index > 0) {
				clazz = clazz.substring(0, index);
			}
			String relJavaPath = clazz + ".java";

			String fileName = srcPath + "/" + relJavaPath;
			File file = new File(fileName);
			if (!file.exists()) {
				LevelLogger.error("Cannot find file : " + fileName);
				continue;
			}

			// <varName, type>
			LineInfo info = new LineInfo(line, relJavaPath, clazz);

			Set<String> variabelsToPredict = FeatureGenerator.obtainAllUsedVaraiblesForPredict(srcPath, info,
					Constant.BOOL_PREDICT_LEFT_VARIABLE, varFeatures, exprFeatures);

			for (String key : variabelsToPredict) {
				lineInfoMapping.put(key, info);
			}
		}
		return lineInfoMapping;
	}

	/**
	 * 
	 * @param subject
	 *            : current predict subject
	 * @param varFeatures:
	 *            features for variable predict
	 * @param exprFeatures
	 *            : features for expression predict
	 * @param lineInfoMapping
	 *            : record the info for each line of source code, formatted as
	 *            <filename::line::varName, {line, relJavaPath, clazz}>
	 * @return <filename::line::varName, <variable, [<predicate, probability>]>>
	 */
	private Map<String, Map<String, List<Pair<String, String>>>> predict(Subject subject, List<String> varFeatures,
			List<String> exprFeatures, Map<String, LineInfo> lineInfoMapping) {

		dumpFeature2File(subject, varFeatures, exprFeatures);

		try {
			ExecuteCommand.executePredict(subject, this);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Map<String, Map<String, List<Pair<String, String>>>> predictedConditions = new HashMap<>();
		List<String> predResult = JavaFile.readFileToStringList(getPredictResultFile(subject));

		Map<String, Map<String, List<Pair<String, Double>>>> location2varName2Predicates = parsePredicates(predResult);

		Comparator<Pair<String, Double>> comparator = new Comparator<Pair<String, Double>>() {
			@Override
			public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
				return o2.getSecond().compareTo(o1.getSecond());
			}
		};

		for (Entry<String, Map<String, List<Pair<String, Double>>>> entry : location2varName2Predicates.entrySet()) {
			String key = entry.getKey();
			Map<String, List<Pair<String, Double>>> varName2Predicates = entry.getValue();
			for (Entry<String, List<Pair<String, Double>>> inner : varName2Predicates.entrySet()) {
				String varName = inner.getKey();
				String varType = lineInfoMapping.get(key).getLegalVariableType(varName);
				List<Pair<String, Double>> conditions = inner.getValue();
				// sort predicates with descending order of probability
				Collections.sort(conditions, comparator);

				for (Pair<String, Double> condPair : conditions) {
					String cond = condPair.getFirst();
					Double prob = condPair.getSecond();
					String newCond = PredicateFilter.filter(cond, varName, varType);
					if (newCond != null) {
						Map<String, List<Pair<String, String>>> linePreds = predictedConditions.get(key);
						if (linePreds == null) {
							linePreds = new HashMap<>();
						}
						List<Pair<String, String>> preds = linePreds.get(varName);
						if (preds == null) {
							preds = new ArrayList<>();
						}
						Pair<String, String> pair = new Pair<String, String>(newCond, String.valueOf(prob));
						preds.add(pair);
						linePreds.put(varName, preds);
						predictedConditions.put(key, linePreds);

					} else {
						LevelLogger
								.info("Filter illegal predicates : " + varName + "(" + varType + ")" + " -> " + cond);
					}
				}
			}
		}

		// delete result files
		ExecuteCommand.deleteGivenFile(new File(getVarFeatureOutputFile(subject)).getAbsolutePath());
		ExecuteCommand.deleteGivenFile(new File(getExprFeatureOutputFile(subject)).getAbsolutePath());
		ExecuteCommand.deleteGivenFile(new File(getPredictResultFile(subject)).getAbsolutePath());

		return predictedConditions;
	}

	private Map<String, Map<String, List<Pair<String, Double>>>> parsePredicates(List<String> predResult) {
		Map<String, Map<String, List<Pair<String, Double>>>> location2varName2Predicates = new HashMap<>();
		for (int index = 1; index < predResult.size(); index++) {
			String line = predResult.get(index);
			String[] columns = line.split("\t");
			if (columns.length < 4) {
				LevelLogger.error(__name__ + "#parsePredicates Parse predict result failed : " + line);
				continue;
			}
			String key = columns[0];
			String varName = columns[1];
			String cond = columns[2];
			Double prob = Double.parseDouble(columns[3]);
			Map<String, List<Pair<String, Double>>> result = location2varName2Predicates.get(key);
			if (result == null) {
				result = new HashMap<>();
				location2varName2Predicates.put(key, result);
			}
			List<Pair<String, Double>> conditions = result.get(varName);
			if (conditions == null) {
				conditions = new LinkedList<>();
				result.put(varName, conditions);
			}
			conditions.add(new Pair<String, Double>(cond, prob));
		}

		return location2varName2Predicates;
	}

	private boolean dumpFeature2File(Subject subject, List<String> varFeatures, List<String> exprFeatures) {
		File varFile = new File(getVarFeatureOutputFile(subject));
		JavaFile.writeStringToFile(varFile, FeatureGenerator.getVarFeatureHeader());
		Set<String> uniqueFeatures = new HashSet<>();
		for (String string : varFeatures) {
			// filter duplicated features
			if (uniqueFeatures.contains(string)) {
				continue;
			}
			uniqueFeatures.add(string);
			LevelLogger.debug(string);
			JavaFile.writeStringToFile(varFile, "\n" + string, true);
		}

		File expFile = new File(getExprFeatureOutputFile(subject));
		JavaFile.writeStringToFile(expFile, FeatureGenerator.getExprFeatureHeader());
		for (String string : exprFeatures) {
			// filter duplicated features
			if (uniqueFeatures.contains(string)) {
				continue;
			}
			uniqueFeatures.add(string);
			LevelLogger.debug(string);
			JavaFile.writeStringToFile(expFile, "\n" + string, true);
		}
		return true;
	}
}
