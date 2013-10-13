/* You can call the sort function in the file like this:
 *
 *     Util.sort(alist, afield);
 *
 * where alist can be an ArrayList<Object> and afield is a Field object
 * as the signature of the sort method indicates.
 */

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Util {

    public static void sort (List<Object> list, Field keyField) {
        String keyName = keyField.getName();
        Comparator<Object> compare = null;
		
        if (keyField.getType().equals(String.class)) {
            compare = new StringCompare(keyName, keyField);
        }
        else if (keyField.getType().equals(int.class)) {
            compare = new IntCompare(keyName, keyField);
        }
        else if (keyField.getType().equals(double.class)) {
            compare = new DoubleCompare(keyName, keyField);
        }
		
        Collections.sort(list, compare);
    }
		
    static class IntCompare implements Comparator<Object> {
        public String keyName;
        public Field field;
		
        public IntCompare(String keyName, Field field) {
            this.keyName = keyName;
            this.field = field;
        }
		
        public int compare (Object o1, Object o2) {
            try {
                return ((Integer)field.get(o1)).compareTo((Integer)field.get(o2));
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return 0; 
        }
    }
	
    static class DoubleCompare implements Comparator<Object> {
        public String keyName;
        public Field field;
		
        public DoubleCompare(String keyName, Field field) {
            this.keyName = keyName;
            this.field = field;
        }
		
        public int compare (Object o1, Object o2) {
            try {
                return ((Double)field.get(o1)).compareTo((Double)field.get(o2));
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return 0; 
        }
    }
	
    static class StringCompare implements Comparator<Object> {
        public String keyName;
        public Field field;
		
        public StringCompare(String keyName, Field field) {
            this.keyName = keyName;
            this.field = field;
        }
		
        public int compare (Object o1, Object o2) {
            try {
                return ((String)field.get(o1)).compareTo((String)field.get(o2));
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return 0; 
        }
    }

}
