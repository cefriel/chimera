package st4rt.convertor.empire.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.METHOD}) @Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RdfContainingEntity {
		
		public String relationPrefix() default "has";
		public Class<?> entity();	
		public EntityId entityId() default EntityId.AUTO_GENERATED;
		
		public enum EntityId {
			AUTO_GENERATED,
			ASSIGNED;
		}

}
		
