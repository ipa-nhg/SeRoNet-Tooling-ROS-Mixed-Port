package de.seronet_projekt.xtend.ROS.generator.ext;

import org.ecore.component.componentDefinition.Activity;
import org.xtend.smartsoft.generator.component.ActivityGeneratorExtension;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ROSActivityGeneratorExtension implements ActivityGeneratorExtension {
	public static ActivityGeneratorExtension getInjectedExtensionImpl() {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				binder().bind(ActivityGeneratorExtension.class).to(ROSActivityGeneratorExtensionImpl.class);
			}
		});
		return injector.getInstance(ActivityGeneratorExtension.class);
	}
	
	private static ActivityGeneratorExtension impl = getInjectedExtensionImpl();
	
	@Override
	public String getExtensionName() {
		return impl.getExtensionName();
	}
	
	@Override
	public CharSequence getHeaderIncludes(Activity activity) {
		return impl.getHeaderIncludes(activity);
	}

	@Override
	public CharSequence getClassMemberPublicDefinition(Activity activity) {
		return impl.getClassMemberPublicDefinition(activity);
	}
	
	@Override
	public CharSequence getSourceImplementation(Activity activity) {
		return impl.getSourceImplementation(activity);
	}
}
