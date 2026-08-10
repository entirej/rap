package org.eclipse.rap.chartjs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.chartjs.ChartAnnotation.AnnotationBase;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonObject.Member;

/**
 * Implementation of Annotations container for Chart.js v3.0.1 Annotation Plugin
 */
public class Annotations {
    private boolean enabled = true;
    private List<AnnotationBase> annotationBases = new ArrayList<>();
    
    public Annotations() {
    }
    
    /**
     * Creates a new line annotation
     * 
     * @param id The unique identifier for this annotation
     * @return The new line annotation
     */
    public ChartAnnotation.LineAnnotation createLineAnnotation(String id) {
        ChartAnnotation.LineAnnotation annotation = new ChartAnnotation.LineAnnotation();
        annotation.setId(id);
        annotationBases.add(annotation);
        return annotation;
    }
    
    /**
     * Creates a new box annotation
     * 
     * @param id The unique identifier for this annotation
     * @return The new box annotation
     */
    public ChartAnnotation.BoxAnnotation createBoxAnnotation(String id) {
        ChartAnnotation.BoxAnnotation annotation = new ChartAnnotation.BoxAnnotation();
        annotation.setId(id);
        annotationBases.add(annotation);
        return annotation;
    }
    
    /**
     * Creates a new point annotation
     * 
     * @param id The unique identifier for this annotation
     * @return The new point annotation
     */
    public ChartAnnotation.PointAnnotation createPointAnnotation(String id) {
        ChartAnnotation.PointAnnotation annotation = new ChartAnnotation.PointAnnotation();
        annotation.setId(id);
        annotationBases.add(annotation);
        return annotation;
    }
    
    /**
     * Creates a new ellipse annotation
     * 
     * @param id The unique identifier for this annotation
     * @return The new ellipse annotation
     */
    public ChartAnnotation.EllipseAnnotation createEllipseAnnotation(String id) {
        ChartAnnotation.EllipseAnnotation annotation = new ChartAnnotation.EllipseAnnotation();
        annotation.setId(id);
        annotationBases.add(annotation);
        return annotation;
    }
    
    /**
     * Creates a new polygon annotation
     * 
     * @param id The unique identifier for this annotation
     * @return The new polygon annotation
     */
    public ChartAnnotation.PolygonAnnotation createPolygonAnnotation(String id) {
        ChartAnnotation.PolygonAnnotation annotation = new ChartAnnotation.PolygonAnnotation();
        annotation.setId(id);
        annotationBases.add(annotation);
        return annotation;
    }
    
    /**
     * Sets whether annotations are enabled
     * 
     * @param enabled True to enable annotations
     * @return This annotations instance
     */
    public Annotations setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    /**
     * Checks if annotations are enabled
     * 
     * @return True if annotations are enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    public void clear()
    {
        annotationBases.clear();
    }
    
    /**
     * Converts this annotations configuration to JSON
     * 
     * @return The JSON representation of this configuration
     */
    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        
        for (AnnotationBase annotationBase : annotationBases)
        {
            result.add(annotationBase.id, annotationBase.toJson()); 
        }
        
        return result;
    }
}