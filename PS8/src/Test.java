import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Test {

	
	public static void main(String[] args) {
		try {
			Class classToCheck = Class.forName("Course");
			Field field = classToCheck.getField("cid");
			System.out.println(field);
		}
		catch (ClassNotFoundException e) {
			System.out.println("a");
			System.out.println(e.getMessage());
		}
		catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
