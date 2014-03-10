package org.fit.cssbox.scriptbox.script.javascript;

public class ScriptAnnotationShutter implements Shutter {

	@Override
	public boolean allowClassAccess(Class<?> type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowFieldAccess(Class<?> type, Object instance,
			String fieldName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowMethodAccess(Class<?> type, Object instance,
			String methodName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowStaticFieldAccess(Class<?> type, String fieldName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowStaticMethodAccess(Class<?> type, String methodName) {
		// TODO Auto-generated method stub
		return false;
	}

}
