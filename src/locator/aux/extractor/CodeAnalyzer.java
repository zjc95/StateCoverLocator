package locator.aux.extractor;

import java.util.HashSet;
import java.util.Set;

import locator.aux.extractor.core.ast.node.BasicBlock;
import locator.aux.extractor.core.ast.node.Use;
import locator.aux.extractor.core.ast.node.Use.USETYPE;
import locator.aux.extractor.core.process.feature.Analyzer;

public class CodeAnalyzer {

	public static Set<String> getAllVariablesUsed(String base, String relJavaFile, int line) {
		BasicBlock basicBlock = Analyzer.analyze(relJavaFile, base);
		Set<Use> uses = basicBlock.getVariableUses(line);
		Set<String> variabls = new HashSet<>();
		for(Use use : uses) {
			variabls.add(use.getVariableDefine().getName());
		}
		return variabls;
	}
	
	public static Set<String> getAllVariablesReadUse(String base, String relJavaFile, int line) {
		BasicBlock basicBlock = Analyzer.analyze(relJavaFile, base);
		Set<Use> uses = basicBlock.getVariableUses(line);
		Set<String> variabls = new HashSet<>();
		for(Use use : uses) {
			if(use.getUseType() == USETYPE.READ) {
				variabls.add(use.getVariableDefine().getName());
			}
		}
		return variabls;
	}
	
	public static Set<String> getAllVariablesWriteUse(String base, String relJavaFile, int line) {
		BasicBlock basicBlock = Analyzer.analyze(relJavaFile, base);
		Set<Use> uses = basicBlock.getVariableUses(line);
		Set<String> variabls = new HashSet<>();
		for(Use use : uses) {
			if(use.getUseType() == USETYPE.WRITE) {
				variabls.add(use.getVariableDefine().getName());
			}
		}
		return variabls;
	}
	
}