package de.seronet_projekt.ros.componentGateway;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;
import com.google.inject.Provider;

import componentInterface.ComponentInterface;
import componentInterface.RosActionClient;
import componentInterface.RosActionServer;
import componentInterface.RosPublisher;
import componentInterface.RosServiceClient;
import componentInterface.RosServiceServer;
import componentInterface.RosSubscriber;
import de.seronet_projekt.ros.componentGateway.generator.ComponentGatewayGenerator;
import de.seronet_projekt.ros.componentGateway.generator.CustomOutputProvider;

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
				ComponentInterface ComponentInterface_model = (ComponentInterface) r.getContents().get(0);
				String ComponentName = ComponentInterface_model.getName();
				List<EObject> RosInterfaces = getInterfaces(ComponentInterface_model);
				ElementListSelectionDialog dialogSelect = new ElementListSelectionDialog(shell, new LabelProvider());
				
				String[] ListofInterfaces = new String[RosInterfaces.size()];
				for (int i=0; i<RosInterfaces.size(); i++) {
					ListofInterfaces[i]=(getViewMenuInterfaceName(RosInterfaces.get(i)));
				}

				dialogSelect.setElements(ListofInterfaces);
				dialogSelect.setTitle("Select the ROS interfaces to relay");
				dialogSelect.setMultipleSelection(true);
				dialogSelect.open();
				Object result = dialogSelect.getFirstResult();
				Object[] results = dialogSelect.getResult();
				String SRComponentName = "ComponentRos"+ComponentName; 
				String RelativePathToSRComponent = "src-gen/SeRoNetComponent/"+SRComponentName+".component";
				IFile SeRoNetComponentFile = project.getFile(RelativePathToSRComponent);
				String RosObjectsPath = null;
				String tmp_component="";
				List<String> RequestHandlers = new ArrayList<>();
				List<String> ActivityBody = new ArrayList<>();
				List<String> SeRoNetPorts = new ArrayList<>();
				List<String> ROSMixedPorts = new ArrayList<>();
				


				for (Object result_: results) {
					for (EObject ResultInterface: RosInterfaces) {
						String MixedPortInterfaceName = null;
						String ServiceName = null;

						if((getViewMenuInterfaceName(ResultInterface)).equals(result_.toString())) {
							EObject SelectedInterface = ResultInterface;

							if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosPublisherImpl")) {
								MixedPortInterfaceName = checkname(((RosPublisher)SelectedInterface).getName())+"_sub";
								RosObjectsPath = ((RosPublisher)SelectedInterface).getPublisher().getMessage().getPackage().eContainer().eResource().getURI().toString();
								ServiceName = ((RosPublisher)SelectedInterface).getPublisher().getMessage().getName()+"Service";
								
								ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
								SeRoNetPorts.add("OutputPort "+checkname(((RosPublisher) SelectedInterface).getName())+"Out implements "
								+ ObjectsConversion(RosObjectsPath,ServiceName) + " realizedBy "+ComponentName+"Activity {}");
								ActivityBody.add("MixedPortROSLink "+MixedPortInterfaceName);

							}
							if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosSubscriberImpl")) {
								MixedPortInterfaceName = checkname(((RosSubscriber) SelectedInterface).getName())+"_pub";
								RosObjectsPath = ((RosSubscriber)SelectedInterface).getSubscriber().getMessage().getPackage().eContainer().eResource().getURI().toString();
								ServiceName = ((RosSubscriber)SelectedInterface).getSubscriber().getMessage().getName()+"Service";
								
								
								ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
								SeRoNetPorts.add("InputPort "+checkname(((RosSubscriber) SelectedInterface).getName())+"In implements "
								+ObjectsConversion(RosObjectsPath,ServiceName)+" {}");
								ActivityBody.add("MixedPortROSLink "+MixedPortInterfaceName);
								ActivityBody.add("InputPortLink "+checkname(((RosSubscriber) SelectedInterface).getName())+"In {}");

							}
							if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosServiceClientImpl")) {
								MixedPortInterfaceName = checkname(((RosServiceClient)SelectedInterface).getName())+"_srvser";
								RosObjectsPath = ((RosServiceClient)SelectedInterface).getSrvclient().getService().getPackage().eContainer().eResource().getURI().toString();
								ServiceName = ((RosServiceClient)SelectedInterface).getSrvclient().getService().getName()+"QueryService";
								
								ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
								SeRoNetPorts.add("RequestPort "+checkname(((RosServiceClient) SelectedInterface).getName())+"ServiceReq implements "
								+ObjectsConversion(RosObjectsPath,ServiceName)+" {}");
								ActivityBody.add("MixedPortROSLink "+MixedPortInterfaceName);
								ActivityBody.add("RequestPortLink "+checkname(((RosServiceClient) SelectedInterface).getName())+"ServiceReq");

							}
							if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosServiceServerImpl")) {
								MixedPortInterfaceName = checkname(((RosServiceServer)SelectedInterface).getName())+"_srvcli";
								RosObjectsPath = ((RosServiceServer)SelectedInterface).getSrvserver().getService().getPackage().eContainer().eResource().getURI().toString();
								ServiceName = ((RosServiceServer)SelectedInterface).getSrvserver().getService().getName()+"QueryService";
								
								ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
								SeRoNetPorts.add("AnswerPort "+checkname(((RosServiceServer) SelectedInterface).getName())+"ServiceAnsw implements "
								+ObjectsConversion(RosObjectsPath,ServiceName)+" {}");
								RequestHandlers.add("	RequestHandler "+checkname(((RosServiceServer)SelectedInterface).getName())+"AnswHandler triggeredFrom "+checkname(((RosServiceServer) SelectedInterface).getName())+ "ServiceAnsw {\n"
										+ "		MixedPortROSLink " +MixedPortInterfaceName+"\n	}");
							}
						}}}
						tmp_component="ComponentDefinition "+SRComponentName+" {\n\n";
						for (String RosMixedPort:ROSMixedPorts) {
							tmp_component+="	"+RosMixedPort+"\n";
						} 
						tmp_component+="\n";
						for (String SeRoNetPort:SeRoNetPorts) {
							tmp_component+="	"+SeRoNetPort+"\n";
						}
						tmp_component+="\n";
						for (String RequestHandler:RequestHandlers) {
							tmp_component+=RequestHandler+"\n";
						}
						tmp_component+="\n";
						if (!ActivityBody.isEmpty()){
							tmp_component+="	Activity "+ComponentName+"Activity {\n";
							for(String Activitybody:ActivityBody) {
								tmp_component+="		"+Activitybody+"\n";
							}
							tmp_component+="	}\n";
						}
						tmp_component+="\n";
						tmp_component+="}";

						byte[] bytes = (tmp_component).getBytes();
						
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

	    return null;
	  }
	
	private String capitalize(String line) {
		   return Character.toUpperCase(line.charAt(0)) + line.substring(1);
		}
	
	
    private String ObjectsConversion (String RosObjectsPath, String ServiceName) {
    	return "ROS"+capitalize(RosObjectsPath.substring(RosObjectsPath.lastIndexOf("/")+1,RosObjectsPath.lastIndexOf(".ros")))+"."+ServiceName;
    }
	private List<EObject> getInterfaces(ComponentInterface componentInterface_model) {
		  List<EObject> ROSInterfaces = new ArrayList<EObject>();
		  for (RosPublisher RosPub: componentInterface_model.getRospublisher()) {
			  ROSInterfaces.add(RosPub);
		  }
		  for (RosSubscriber RosSub: componentInterface_model.getRossubscriber()) {
			  ROSInterfaces.add(RosSub);
		  }
		  for (RosServiceClient RosSrvc: componentInterface_model.getRosserviceclient()) {
			  ROSInterfaces.add(RosSrvc);
		  }
		  for (RosServiceServer RosSrvs: componentInterface_model.getRosserviceserver()) {
			  ROSInterfaces.add(RosSrvs);
		  }
		  for (RosActionClient RosActc: componentInterface_model.getRosactionclient()) {
			  ROSInterfaces.add(RosActc);
		  }
		  for (RosActionServer RosActs: componentInterface_model.getRosactionserver()) {
			  ROSInterfaces.add(RosActs);
		  }
		  return ROSInterfaces;
	}
	
	private String getInterfaceName(EObject RosInterface) {
		String name = RosInterface.toString().substring(RosInterface.toString().indexOf("name:")+6,RosInterface.toString().indexOf(","));
		return name;
	}
	private String getViewMenuInterfaceName(EObject RosInterface) {
		String name = "["+RosInterface.toString().substring(RosInterface.toString().indexOf("impl.Ros")+8,RosInterface.toString().indexOf("Impl@"))+"]  "+
				getInterfaceName(RosInterface);
		return name;
	}
	
	private String checkname(String name){
		if (name.contains("/"))
			return name.replace("/","_");
		else
			return name;
	}	
	
	@Override
	  public boolean isEnabled() {
	    return true;
	  }
	}