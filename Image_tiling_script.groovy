
import qupath.lib.gui.scripting.QPEx
import qupath.lib.color.ColorDeconvolutionHelper;
import qupath.lib.color.ColorDeconvolutionStains;
import qupath.lib.common.ColorTools;
import qupath.lib.analysis.algorithms.EstimateStainVectors;
import qupath.lib.color.ColorDeconvolutionHelper;
import qupath.lib.color.ColorDeconvolutionStains;
import qupath.lib.regions.ImagePlane;
import qupath.lib.roi.RectangleROI;
import qupath.lib.images.servers.ImageChannel;

def name_the_annot(RectangleROI annot) {
  def annot_w = annot.x2 - annot.x
  def annot_h = annot.y2 - annot.y
  String file_name = String.format("annot_block_x%.0f_y%.0f_w%.0f_h%.0f.json", annot.x,annot.y, annot_w, annot_h)
  return file_name
}


def add_annotation_boxes(int image_w, int image_h, ImagePlane image_plane, int current_x_coord = 0, int current_y_coord = 0, float roi_rate = 0.1) {
  int bound = Math.round(1/roi_rate)
  ArrayList<PathObjects> added_objects = new ArrayList<PathObjects>()
  int roi_w = (int)image_w*roi_rate
  int roi_h = (int)image_h*roi_rate
  for(int index_w = 0; index_w < bound; index_w++) {
  for(int index_h = 0; index_h < bound; index_h++) {
    new_x_coord = index_w*roi_h
    new_y_coord = index_h*roi_w
    def current_roi = ROIs.createRectangleROI(current_y_coord+new_y_coord, current_x_coord+new_x_coord, roi_w, roi_h, image_plane)
    def current_annot = PathObjects.createAnnotationObject(current_roi)
    addObject(current_annot)
    added_objects.add(current_annot)
  }
  // def logger = String.format("Current roi top left is: %d %d ",current_x_coord, current_y_coord)
  // print(logger)
  }
  return added_objects
}

// Metadata breakdown
// Confirm the correct layers and stain

def viewer = getCurrentViewer()
def image_data = viewer.getImageData()
def server = image_data.getServer()
ImageData<BufferedImage> imageData = QPEx.getCurrentImageData();
if (imageData == null || !imageData.isBrightfield() || imageData.getServer() == null || !imageData.getServer().isRGB()) {
    DisplayHelpers.showErrorMessage("Estimate stain vectors", "No brightfield, RGB image selected!");
    return;
}
ColorDeconvolutionStains stains = imageData.getColorDeconvolutionStains();
print(stains.toString())
for (stain_vec in stains) {
    print( stain_vec.getStain(1).toString())
    print( stain_vec.getStain(2).toString())
    print( stain_vec.getStain(3).toString())
}


def channel2index = new HashMap()
for (int c = 0; c < server.nChannels(); c++) {
  def channelName = server.getChannel(c.intValue())
  print(String.format('Channel Name: %s, index: %d',channelName.getName(),c.intValue()))
  channel2index.put(channelName.getName(), new Integer(c.intValue()))
}

// Add tiles to image
int z = 0
int t = 0
int image_width = server.getWidth() 
int image_height = server.getHeight() 
def plane = ImagePlane.getPlane(z, t)
print(image_width)
print(image_height)

//ArrayList<RectangleROI> added_annotations =  add_annotation_boxes(image_width, image_height, plane, 0, 0, 0.125)
//print(added_annotations)
//print('Added the tiles')
//for (annot in added_annotations) {
//  print(annot)
//  print(annot.getROI())
//  def annot_box_fn = name_the_annot(annot.getROI())
//  print(annot_box_fn)
////  removeObject(annot, false) 
//  
// }
existing_annotation = getAnnotationObjects()
for (annot in existing_annotation) {
    def name = annot.getName() // Get the name
//    print(name)
//    print(annot.getClass())
    if (name  != null) {
//        print(name)
        if (name=='Tissue_area') {
//            print('oh yea')
            annot.setClassification('Tissue')
        } else {
//            continue
            annot.setClassification('Cell')
        }
    }
    else {
        println("Value is not a Sam.")
        annot.setName('Manual_annotation')

    }
}

//print('Removed the tiles')

print('Done')
