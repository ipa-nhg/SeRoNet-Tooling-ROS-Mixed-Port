package de.seronet_projekt.ros.componentGateway;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import rosInterfacesPool.RosInterface;
import rosInterfacesPool.impl.RosPublisherImpl;
import rosInterfacesPool.RosInterfacesPool;
import rosInterfacesPool.RosInterfacesPoolPackage;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;


import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.seronet_projekt.ros.componentGateway.generator.ComponentGatewayGenerator;
import de.seronet_projekt.ros.componentGateway.generator.CustomOutputProvider;
import ros.ActionClient;
import ros.ActionServer;
import ros.Artifact;
import ros.Node;
import ros.Package;
import ros.PackageSet;
import ros.Publisher;
import ros.ServiceClient;
import ros.ServiceServer;
import ros.Subscriber;
import ros.TopicSpec;
import componentInterface.ComponentInterface;
import componentInterface.RosPublisher;

import org.eclipse.swt.widgets.Shell;

public class GenerationHandler extends AbstractHandler implements IHandler {
	 
	  @Inject
	  private Provider<EclipseResourceFileSystemAccess2> fileAccessProvider;
	 
	  @Inject
	  IResourceDescriptions resourceDescriptions;
	 
	  @Inject
	  IResourceSetProvider resourceSetProvider;

	static Map<String, OutputConfiguration> getOutputConfigurationsAsMap(IOutputConfigurationProvider provider) {
		Map<String, OutputConfiguration> outputs = new HashMap<String, OutputConfiguration>();
		for(OutputConfiguration c: provider.getOutputConfigurations()) {
			outputs.put(c.getName(), c);
		}
		return outputs;
	}

	  @SuppressWarnings("unlikely-arg-type")
	@Override
	  public Object execute(ExecutionEvent event) throws ExecutionException {
	    ISelection selection = HandlerUtil.getCurrentSelection(event);
	    if (selection instanceof IStructuredSelection) {
	      IStructuredSelection structuredSelection = (IStructuredSelection) selection;
	      Object firstElement = structuredSelection.getFirstElement();
	      if (firstElement instanceof IFile) {
	        IFile file = (IFile) firstElement;
	        IProject project = file.getProject();
	        final EclipseResourceFileSystemAccess2 fsa = fileAccessProvider.get();
	        fsa.setProject(project);
	        fsa.setOutputConfigurations(getOutputConfigurationsAsMap(new CustomOutputProvider()));
            fsa.setMonitor(new NullProgressMonitor()); 
            
	        URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
	        ResourceSet rs = resourceSetProvider.get(project);
	        Resource r = rs.getResource(uri, true);
			Display display = Display.getDefault();
		    Shell shell = display.getActiveShell();
		    ComponentGatewayGenerator generator = new ComponentGatewayGenerator();
			generator.doGenerate(r, fsa, new GeneratorContext());

		    MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES| SWT.NO);
		    dialog.setText("ROS Pool Interface");
		    dialog.setMessage("Would you like to relay to the SeRoNet network one of the ROS interfaces automatically?");
		    if (dialog.open() == SWT.YES) {
		    	String ComponentName =file.getName().substring(0, file.getName().indexOf("."));
		    	String RelativePath = file.getProject().getFullPath().toString()+"/src-gen/SeRoNetComponent/"+ComponentName+".gateway.rosinterfacespool";
				ResourceSet rs_pool = new ResourceSetImpl();
				Resource resource = rs_pool.getResource(URI.createPlatformResourceURI(RelativePath,true),true);			
				RosInterfacesPool RosInterfacesPool_model = (RosInterfacesPool) resource.getContents().get(0);
				EList<RosInterface> RosInterfaces= (EList<RosInterface>) RosInterfacesPool_model.getInterfaces();
				ElementListSelectionDialog dialogSelect = new ElementListSelectionDialog(shell, new LabelProvider());
				String[] ListofInterfaces = new String[RosInterfaces.size()];
				for (int i=0; i<RosInterfaces.size(); i++) {
					ListofInterfaces[i]=RosInterfaces.get(i).getName();
				}
				dialogSelect.setElements(ListofInterfaces);
				dialogSelect.setTitle("Select a ROS interface to relay");
				dialogSelect.open();
				Object result = dialogSelect.getFirstResult();
				for (RosInterface ResultInterface: RosInterfaces) {
					if(ResultInterface.getName() == result) {
						RosInterface SelectedInterface = ResultInterface;
						String SRComponentName = "ComponentRos"+ComponentName; 
						String RelativePathToSRComponent = "src-gen/SeRoNetComponent/"+SRComponentName+".component";
						IFile SeRoNetComponentFile = project.getFile(RelativePathToSRComponent);
						/**if (SelectedInterface.getClass().equals("rosInterfacesPool.impl.RosSubscriberImpl")) {
							String SeRoNetPort = "OutputPort";
							ComponentInterface CI =(ComponentInterface)r.getContents().get(0);
							for (RosPublisher RosPub:CI.getRospublisher()){
								if(RosPub.getName() == ((rosInterfacesPool.RosSubscriber) SelectedInterface).getTopicName()) {
									TopicSpec type = RosPub.getPublisher().getMessage();
									System.out.println(type.eCrossReferences());
								}
							}
						}
						System.out.println("TYPE: "+SelectedInterface.getClass());*/
						byte[] bytes = ("ComponentDefinition "+SRComponentName+ "\n{\n"
								+ "	Activity "+ComponentName+"{\n"
								+"		MixedPortROSLink "+SelectedInterface.getName()+
								"\n		DefaultTrigger PeriodicTimer 10.0 Hz\n"+
						"	}\n"+
						"\n	MixedPortROS "+SelectedInterface.getName()+
						
						"\n}").getBytes();
						InputStream source = new ByteArrayInputStream(bytes);
						try {
							if (!SeRoNetComponentFile.exists()) {
								SeRoNetComponentFile.create(source, IResource.NONE, null);
							} else{
								@SuppressWarnings("resource")
								OutputStream outputStream = new FileOutputStream(new File(project.getLocation().toString()+"/"+RelativePathToSRComponent));
								outputStream.write(bytes);
							}

						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
		    }
	 
	      }
	    }


	    return null;
	  }
	 
	  @Override
	  public boolean isEnabled() {
	    return true;
	  }
	}