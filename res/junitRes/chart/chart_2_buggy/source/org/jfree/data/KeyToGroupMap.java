package org.jfree.data;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
/** 
 * A class that maps keys (instances of <code>Comparable</code>) to groups.
 */
public class KeyToGroupMap implements Cloneable, PublicCloneable, Serializable {
  /** 
 * For serialization. 
 */
  private static final long serialVersionUID=-2228169345475318082L;
  /** 
 * The default group. 
 */
  private Comparable defaultGroup;
  /** 
 * The groups. 
 */
  private List groups;
  /** 
 * A mapping between keys and groups. 
 */
  private Map keyToGroupMap;
  /** 
 * Creates a new map with a default group named 'Default Group'.
 */
  public KeyToGroupMap(){
    this("Default Group");
  }
  /** 
 * Creates a new map with the specified default group.
 * @param defaultGroup  the default group (<code>null</code> not permitted).
 */
  public KeyToGroupMap(  Comparable defaultGroup){
    if (defaultGroup == null) {
      throw new IllegalArgumentException("Null 'defaultGroup' argument.");
    }
    this.defaultGroup=defaultGroup;
    this.groups=new ArrayList();
    this.keyToGroupMap=new HashMap();
  }
  /** 
 * Returns the number of groups in the map.
 * @return The number of groups in the map.
 */
  public int getGroupCount(){
    return this.groups.size() + 1;
  }
  /** 
 * Returns a list of the groups (always including the default group) in the map.  The returned list is independent of the map, so altering the list will have no effect.
 * @return The groups (never <code>null</code>).
 */
  public List getGroups(){
    List result=new ArrayList();
    result.add(this.defaultGroup);
    Iterator iterator=this.groups.iterator();
    while (iterator.hasNext()) {
      Comparable group=(Comparable)iterator.next();
      if (!result.contains(group)) {
        result.add(group);
      }
    }
    return result;
  }
  /** 
 * Returns the index for the group.
 * @param group  the group.
 * @return The group index (or -1 if the group is not represented withinthe map).
 */
  public int getGroupIndex(  Comparable group){
    int result=this.groups.indexOf(group);
    if (result < 0) {
      if (this.defaultGroup.equals(group)) {
        result=0;
      }
    }
 else {
      result=result + 1;
    }
    return result;
  }
  /** 
 * Returns the group that a key is mapped to.
 * @param key  the key (<code>null</code> not permitted).
 * @return The group (never <code>null</code>, returns the default group ifthere is no mapping for the specified key).
 */
  public Comparable getGroup(  Comparable key){
    if (key == null) {
      throw new IllegalArgumentException("Null 'key' argument.");
    }
    Comparable result=this.defaultGroup;
    Comparable group=(Comparable)this.keyToGroupMap.get(key);
    if (group != null) {
      result=group;
    }
    return result;
  }
  /** 
 * Maps a key to a group.
 * @param key  the key (<code>null</code> not permitted).
 * @param group  the group (<code>null</code> permitted, clears anyexisting mapping).
 */
  public void mapKeyToGroup(  Comparable key,  Comparable group){
    if (key == null) {
      throw new IllegalArgumentException("Null 'key' argument.");
    }
    Comparable currentGroup=getGroup(key);
    if (!currentGroup.equals(this.defaultGroup)) {
      if (!currentGroup.equals(group)) {
        int count=getKeyCount(currentGroup);
        if (count == 1) {
          this.groups.remove(currentGroup);
        }
      }
    }
    if (group == null) {
      this.keyToGroupMap.remove(key);
    }
 else {
      if (!this.groups.contains(group)) {
        if (!this.defaultGroup.equals(group)) {
          this.groups.add(group);
        }
      }
      this.keyToGroupMap.put(key,group);
    }
  }
  /** 
 * Returns the number of keys mapped to the specified group.  This method won't always return an accurate result for the default group, since explicit mappings are not required for this group.
 * @param group  the group (<code>null</code> not permitted).
 * @return The key count.
 */
  public int getKeyCount(  Comparable group){
    if (group == null) {
      throw new IllegalArgumentException("Null 'group' argument.");
    }
    int result=0;
    Iterator iterator=this.keyToGroupMap.values().iterator();
    while (iterator.hasNext()) {
      Comparable g=(Comparable)iterator.next();
      if (group.equals(g)) {
        result++;
      }
    }
    return result;
  }
  /** 
 * Tests the map for equality against an arbitrary object.
 * @param obj  the object to test against (<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof KeyToGroupMap)) {
      return false;
    }
    KeyToGroupMap that=(KeyToGroupMap)obj;
    if (!ObjectUtilities.equal(this.defaultGroup,that.defaultGroup)) {
      return false;
    }
    if (!this.keyToGroupMap.equals(that.keyToGroupMap)) {
      return false;
    }
    return true;
  }
  /** 
 * Returns a clone of the map.
 * @return A clone.
 * @throws CloneNotSupportedException  if there is a problem cloning themap.
 */
  public Object clone() throws CloneNotSupportedException {
    KeyToGroupMap result=(KeyToGroupMap)super.clone();
    result.defaultGroup=(Comparable)KeyToGroupMap.clone(this.defaultGroup);
    result.groups=(List)KeyToGroupMap.clone(this.groups);
    result.keyToGroupMap=(Map)KeyToGroupMap.clone(this.keyToGroupMap);
    return result;
  }
  /** 
 * Attempts to clone the specified object using reflection.
 * @param object  the object (<code>null</code> permitted).
 * @return The cloned object, or the original object if cloning failed.
 */
  private static Object clone(  Object object){
    if (object == null) {
      return null;
    }
    Class c=object.getClass();
    Object result=null;
    try {
      Method m=c.getMethod("clone",(Class[])null);
      if (Modifier.isPublic(m.getModifiers())) {
        try {
          result=m.invoke(object,(Object[])null);
        }
 catch (        Exception e) {
          e.printStackTrace();
        }
      }
    }
 catch (    NoSuchMethodException e) {
      result=object;
    }
    return result;
  }
  /** 
 * Returns a clone of the list.
 * @param list  the list.
 * @return A clone of the list.
 * @throws CloneNotSupportedException if the list could not be cloned.
 */
  private static Collection clone(  Collection list) throws CloneNotSupportedException {
    Collection result=null;
    if (list != null) {
      try {
        List clone=(List)list.getClass().newInstance();
        Iterator iterator=list.iterator();
        while (iterator.hasNext()) {
          clone.add(KeyToGroupMap.clone(iterator.next()));
        }
        result=clone;
      }
 catch (      Exception e) {
        throw new CloneNotSupportedException("Exception.");
      }
    }
    return result;
  }
}
