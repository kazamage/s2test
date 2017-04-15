package jp.pgw.develop.swallow.creator;

import org.seasar.framework.container.ComponentCustomizer;
import org.seasar.framework.container.creator.ComponentCreatorImpl;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.convention.NamingConvention;

public class TargetCreator extends ComponentCreatorImpl {

	public TargetCreator(final NamingConvention namingConvention) {
		super(namingConvention);
		setNameSuffix("Target");
		setInstanceDef(InstanceDefFactory.PROTOTYPE);
	}

	public ComponentCustomizer getActionCustomizer() {
		return getCustomizer();
	}

	public void setActionCustomizer(final ComponentCustomizer customizer) {
		setCustomizer(customizer);
	}
}
