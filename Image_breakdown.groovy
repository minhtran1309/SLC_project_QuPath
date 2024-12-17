import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.gui.scripting.QPEx
import java.awt.image.BufferedImage;
import qupath.lib.EstimateStainVectors;
import qupath.lib.common.ColorTools;
import qupath.lib.analysis.algorithms.EstimateStainVectors
import qupath.lib.color.ColorDeconvolutionHelper;
import qupath.lib.color.ColorDeconvolutionStains;
import qupath.lib.color.StainVector;
import qupath.lib.common.GeneralTools;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.commands.interfaces.PathCommand;
//import qupath.lib.gui.plots.ScatterPlot;
//import qupath.lib.images.ImageData;
import qupath.lib.objects.PathObject;
import qupath.lib.plugins.parameters.ParameterList;
import qupath.lib.regions.RegionRequest;
import qupath.lib.roi.RectangleROI;
import qupath.lib.gui.scripting.QPEx
import qupath.lib.roi.interfaces.ROI;

ImageData<BufferedImage> imageData = QPEx.getCurrentImageData();
if (imageData == null || !imageData.isBrightfield() || imageData.getServer() == null || !imageData.getServer().isRGB()) {
    DisplayHelpers.showErrorMessage("Estimate stain vectors", "No brightfield, RGB image selected!");
    return;
}
ColorDeconvolutionStains stains = imageData.getColorDeconvolutionStains();
if (stains == null || !stains.getStain(3).isResidual(


)) {
    DisplayHelpers.showErrorMessage("Estimate stain vectors", "Sorry, stain editing is only possible for brightfield, RGB images with 2 stains");
    return;
}
    
///////Select objects as a list///////   
cores_name = ['PathAnnotationObject']; 

//selectObjects = selectAnnotations();
cores_list = getAnnotationObjects();
print(String.format("Selected objetcs: %s", cores_list.asList()))

//////////////////////////////////////////////////////////////////////////////////////////////
for (int i = 0; i < cores_list.size(); i++){
	
    PathObject pathObject = cores_list[I];
    ROI roi = pathObject == null ? null : pathObject.getROI();
    if (roi == null)
        roi = new RectangleROI(0, 0, imageData.getServer().getWidth(), imageData.getServer().getHeight());

    int MAX_PIXELS = 4000*4000;		
    double downsample = Math.max(1, Math.sqrt((roi.getBoundsWidth() * roi.getBoundsHeight()) / MAX_PIXELS));
    RegionRequest request = RegionRequest.createInstance(imageData.getServerPath(), downsample, roi);
    BufferedImage img = imageData.getServer().readBufferedImage(request);
    		
    // Apply small amount of smoothing to reduce compression artefacts
    img = EstimateStainVectors.smoothImage(img);
    // Check modes for background
    int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
    int[] rgbMode = EstimateStainVectors.getModeRGB(rgb);
    int rMax = rgbMode[0];
    int gMax = rgbMode[1];
    int bMax = rgbMode[2];
    double minStain = 0.05;
    double maxStain = 1.0;
    double ignorePercentage = 1.0;


    ColorDeconvolutionStains stain_vec = EstimateStainVectors.estimateStains(img, stains, false)
        
    def hema_vec = stain_vec.getStain(1).toString()[1..-1].replace('ematoxylin: ','');
    def DAB_vec = stain_vec.getStain(2).toString()[1..-1].replace('AB: ','');
    def resi_vec = stain_vec.getStain(3).toString()[1..-1].replace('esidual: ','');
    def background_rgb = rgbMode.toString().replace(',','').replace('[','').replace(']','');   
    
/* print('hema_vec= ' + hema_vec)
print('DAB_vec= ' + DAB_vec) 
print('resi_vec= ' + resi_vec)
print('background_rgb= ' + background_rgb) */

  
setColorDeconvolutionStains('{"Name" : "H-DAB modified by script", "Stain 1" : "Hema_vec", "Values 1" : "'+hema_vec+'", "Stain 2" : "DAB_vec", "Values 2" : "'+DAB_vec+'", "Background" : " '+background_rgb+' "}');

}

print('Done')