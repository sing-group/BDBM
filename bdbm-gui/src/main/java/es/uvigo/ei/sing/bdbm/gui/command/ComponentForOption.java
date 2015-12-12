package es.uvigo.ei.sing.bdbm.gui.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ComponentForOption {
	public String[] value();
	public boolean allowsMultiple() default false;
}
