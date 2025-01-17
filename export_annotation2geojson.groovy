def annotations = getAnnotationObjects()
def path = "/home/minhtran/Projects/SLC_project_QuPath/SLC_annotations/K6867_annotations.geojson"

// 'FEATURE_COLLECTION' is standard GeoJSON format for multiple objects
exportObjectsToGeoJson(annotations, path, "FEATURE_COLLECTION")
