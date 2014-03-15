package tests.script.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;

public class TestClasses {
	public static class NestedObjectWithGetter implements ObjectGetter {
		public String publicProperty = "public property";
		protected String protectedProperty = "protected property";
		private String privateProperty = "private property";
		
		public NestedObjectWithGetter publicNestedObject;
		public NestedObjectWithGetter publicNestedObjectWithGetter;
		private NestedObjectWithGetter privateNestedObjectWithGetter;
		
		public String publicStringProperty = "publicStringProperty";
		public String duplicatedPublicStringProperty = "publicStringProperty";
		
		public NestedObjectWithGetter(int nestLevel) {
			if (nestLevel > 0) {
				publicNestedObject = new NestedObjectWithGetter(nestLevel - 1);
				publicNestedObjectWithGetter = new NestedObjectWithGetter(nestLevel - 1);
				privateNestedObjectWithGetter = new NestedObjectWithGetter(nestLevel - 1);
			}
		}
		
		public NestedObjectWithGetter getPublicNestedObjectWithGetter() {
			return publicNestedObjectWithGetter;
		}
		
		public NestedObjectWithGetter getPrivateNestedObjectWithGetter() {
			return privateNestedObjectWithGetter;
		}
		
		public void setPrivateNestedObjectWithGetter(NestedObjectWithGetter object) {
			privateNestedObjectWithGetter = object;
		}
		
		public String getPublicStringProperty() {
			duplicatedPublicStringProperty = null;
			return publicStringProperty;
		}
		
		public void setPublicStringProperty(String value) {
			publicStringProperty = value;
			duplicatedPublicStringProperty = value;
		}
		
		public String getConcat() {
			return "";
		}
		
		public String getConcat(String arg1) {
			return arg1;
		}
		
		public String getConcat(String arg1, String arg2) {
			return arg1 + arg2;
		}

		@Override
		public Object get(Object arg) {
			if (arg instanceof String) {
				if (((String)arg).equals("foo")) {
					return "bar";
				}
			}
			
			if (arg instanceof Integer) {
				if (((Integer)arg).equals(0)) {
					return 1;
				}
			}
			
			return ObjectGetter.UNDEFINED_VALUE;
		}
	}
	
	public static class CollectionsContainer {
		public Map<String, String> foobarMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("foo", "bar");
			}
		};
		
		public List<String> foobarList = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("foobar");
			}
		};
	}
}
