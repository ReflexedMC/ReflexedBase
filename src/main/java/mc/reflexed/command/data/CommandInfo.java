package mc.reflexed.command.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String name();
    String description() default "No description provided.";
    String permission() default "";
    String usage() default "";
    String fallback() default "reflexed";
    String noPermission() default "§cYou do not have permission to execute this command.";
    String[] aliases() default {};
}
