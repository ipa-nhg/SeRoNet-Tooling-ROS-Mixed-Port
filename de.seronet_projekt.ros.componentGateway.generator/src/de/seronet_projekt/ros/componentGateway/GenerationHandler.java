package de.seronet_projekt.ros.componentGateway;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import componentInterface.RosParameter;
import de.seronet_projekt.ros.componentGateway.generator.ComponentGatewayGenerator;
import de.seronet_projekt.ros.componentGateway.generator.CustomOutputProvider;
import ros.ParameterType;
import ros.ParameterValue;

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
					String RelativePathToSRParameterComponent = "src-gen/SeRoNetComponent/"+SRComponentName+".componentParameters";

					IFile SeRoNetComponentFile = project.getFile(RelativePathToSRComponent);
					IFile SeRoNetParameterComponentFile = project.getFile(RelativePathToSRParameterComponent);

					String RosObjectsPath = null;
					String tmp_component="";
					String tmp_parametercomponent="";

					List<String> RequestHandlers = new ArrayList<>();
					List<String> ActivityBody = new ArrayList<>();
					List<String> SeRoNetPorts = new ArrayList<>();
					List<String> ROSMixedPorts = new ArrayList<>();
					
					for (Object result_: results) {
						for (EObject ResultInterface: RosInterfaces) {
							String MixedPortInterfaceName = null;
							String ServiceName = null;
							String SeRoNetPort = null;

							if((getViewMenuInterfaceName(ResultInterface)).equals(result_.toString())) {
								EObject SelectedInterface = ResultInterface;

								if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosPublisherImpl")) {
									MixedPortInterfaceName = checkname(((RosPublisher)SelectedInterface).getName())+"_sub";
									RosObjectsPath = ((RosPublisher)SelectedInterface).getPublisher().getMessage().getPackage().eContainer().eResource().getURI().toString();
									ServiceName = ((RosPublisher)SelectedInterface).getPublisher().getMessage().getName()+"Service";
									SeRoNetPort = SeRoNetPortsName(((RosPublisher) SelectedInterface).getName())+"Out";
								
									ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
									SeRoNetPorts.add("OutputPort "+SeRoNetPort+" implements "
									+ ObjectsConversion(RosObjectsPath,ServiceName) + " realizedBy "+ComponentName+"Activity {}");
									ActivityBody.add("MixedPortROSLink "+MixedPortInterfaceName);

								}
								if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosSubscriberImpl")) {
									MixedPortInterfaceName = checkname(((RosSubscriber) SelectedInterface).getName())+"_pub";
									RosObjectsPath = ((RosSubscriber)SelectedInterface).getSubscriber().getMessage().getPackage().eContainer().eResource().getURI().toString();
									ServiceName = ((RosSubscriber)SelectedInterface).getSubscriber().getMessage().getName()+"Service";
									SeRoNetPort = SeRoNetPortsName(((RosSubscriber) SelectedInterface).getName())+"In";

								
									ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
									SeRoNetPorts.add("InputPort "+SeRoNetPort+" implements "
									+ObjectsConversion(RosObjectsPath,ServiceName)+" {}");
									ActivityBody.add("MixedPortROSLink "+MixedPortInterfaceName);
									ActivityBody.add("InputPortLink "+SeRoNetPort+" {}");

								}
								if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosServiceClientImpl")) {
									MixedPortInterfaceName = checkname(((RosServiceClient)SelectedInterface).getName())+"_srvser";
									RosObjectsPath = ((RosServiceClient)SelectedInterface).getSrvclient().getService().getPackage().eContainer().eResource().getURI().toString();
									ServiceName = ((RosServiceClient)SelectedInterface).getSrvclient().getService().getName()+"QueryService";
									SeRoNetPort = SeRoNetPortsName(((RosServiceClient) SelectedInterface).getName())+"ServiceReq";

								
									ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
									SeRoNetPorts.add("RequestPort "+SeRoNetPort+" implements "
									+ObjectsConversion(RosObjectsPath,ServiceName)+" {}");
									ActivityBody.add("MixedPortROSLink "+MixedPortInterfaceName);
									ActivityBody.add("RequestPortLink "+SeRoNetPort);

								}
								if (SelectedInterface.getClass().toString().contains("componentInterface.impl.RosServiceServerImpl")) {
									MixedPortInterfaceName = checkname(((RosServiceServer)SelectedInterface).getName())+"_srvcli";
									RosObjectsPath = ((RosServiceServer)SelectedInterface).getSrvserver().getService().getPackage().eContainer().eResource().getURI().toString();
									ServiceName = ((RosServiceServer)SelectedInterface).getSrvserver().getService().getName()+"QueryService";
									SeRoNetPort = SeRoNetPortsName(((RosServiceServer) SelectedInterface).getName())+"ServiceAnsw";

								
									ROSMixedPorts.add("MixedPortROS "+MixedPortInterfaceName);
									SeRoNetPorts.add("AnswerPort "+SeRoNetPort+" implements "
									+ObjectsConversion(RosObjectsPath,ServiceName)+" {}");
									RequestHandlers.add("	RequestHandler "+SeRoNetPort.replace("ServiceAnsw", "")+" triggeredFrom "+SeRoNetPort+" {\n"
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
						
						//PARAMETERS
						if (ComponentInterface_model.getRosparameter().size()>0) {
							tmp_parametercomponent="ComponentParameter "+SRComponentName+"Parameters component "+SRComponentName+" {\n";
							tmp_parametercomponent+="    InternalParameter General {\n";

							for (RosParameter rosparams:ComponentInterface_model.getRosparameter()) {
								List<String> UnsuportedTypes = Arrays.asList("ros.impl.ParameterListTypeImpl", "ros.impl.ParameterBase64TypeImpl", "ros.impl.ParameterStructTypeImpl");
								if (!UnsuportedTypes.contains(rosparams.getParameter().getType().getClass().getTypeName())) {
									tmp_parametercomponent+="        "+rosparams.getName().replace("/","")+" : "+getParamType(rosparams.getParameter().getType());
									if (rosparams.getValue()!=null) {
										if (rosparams.getParameter().getType().getClass().getTypeName().equals("ros.impl.ParameterStringTypeImpl")) {
										tmp_parametercomponent+=" = '"+getParamValue(rosparams.getValue())+"'";
									}else {
										tmp_parametercomponent+=" = "+getParamValue(rosparams.getValue());
									}}
									tmp_parametercomponent+="\n";
								}}
							tmp_parametercomponent+="    }";
							tmp_parametercomponent+="}";
							byte[] bytes_param = (tmp_parametercomponent).getBytes();
							
							InputStream source_param = new ByteArrayInputStream(bytes_param);
							try {
								if (!SeRoNetParameterComponentFile.exists()) {
									SeRoNetParameterComponentFile.create(source_param, IResource.NONE, null);
								} else{
									@SuppressWarnings("resource")
									OutputStream outputStream_param = new FileOutputStream(new File(project.getLocation().toString()+"/"+RelativePathToSRParameterComponent));
									outputStream_param.write(bytes_param);
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

		return null;
	}
	
	private String capitalize(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
	
	private String SeRoNetPortsName (String RosName)  {
		return checkname(RosName).replaceFirst("_", "");
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
	private String getParamType(ParameterType rostype) {
		String rostypeClassName = rostype.getClass().getTypeName().replace("ros.impl.Parameter","").replace("TypeImpl", "");
		//List<String> UnsuportedTypes = Arrays.asList("Array", "List", "Base64", "Struct");
		//if (!UnsuportedTypes.contains(rostypeClassName)) {
		if (rostypeClassName.equals("Integer")) {
			return "Int64";
		}
		if (rostypeClassName.equals("Array")) {
			return getParamType((ParameterType) rostype.eContents().get(0))+"[]";
		} else {
		return rostypeClassName;
		}
	}
	
	private String getParamValue(ParameterValue value) {
		String value_string = value.toString();
		String value_return = "";
		if (value_string.contains("value")) {
			return value_string.substring(value_string.indexOf("value")+7, value_string.length()-1);
		} else if(value_string.contains("ParameterSequenceImpl")){
			value_return+="[";
			for(EObject param: value.eContents()){
				if (param.toString().contains("(value:")){
					value_return+=param.toString().substring(param.toString().indexOf("value:")+6,param.toString().indexOf(")"));
					value_return+=",";
			}}
		value_return = value_return.substring(0, value_return.length() - 1);
		value_return+="]";
		return value_return;
		} else {
			return "";
		}
	}
	
	@Override
	  public boolean isEnabled() {
		return true;
	  }
	}
