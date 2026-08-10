package org.eclipse.rap.chartjs;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonObject.Member;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;

/**
 * Implementation of Chart.js Annotation Plugin v3.0.1
 * @see https://www.chartjs.org/chartjs-plugin-annotation/latest
 */
public class ChartAnnotation {

    /**
     * Base class for annotation configurations
     */
    public static abstract class AnnotationBase {
        protected String id;
        protected String type;
        protected String drawTime = "afterDatasetsDraw";
        protected Boolean display = true;
        protected String xScaleID;
        protected String yScaleID;
        protected Boolean adjustScaleRange = true;
        protected Double xMin;
        protected Double xMax;
        protected Double yMin;
        protected Double yMax;
        protected String borderColor;
        protected Integer borderWidth;
        protected int[] borderDash;
        protected Integer borderDashOffset;
        protected String backgroundColor;
        protected AnnotationLabel label;
        protected JsonObject customConfig = new JsonObject();
        
        // Callbacks - these would be handled in a real implementation with function references
        private String enterCallback;
        private String leaveCallback;
        private String clickCallback;
        
        public AnnotationBase(String type) {
            this.type = type;
        }
        
        /**
         * Converts this annotation to a JSON object
         * @return JSON representation of this annotation
         */
        public JsonObject toJson() {
            JsonObject result = new JsonObject();
            
            // Common properties
            result.add("type", type);
            
            if (id != null) {
                result.add("id", id);
            }
            
            if (drawTime != null) {
                result.add("drawTime", drawTime);
            }
            
            if (display != null) {
                result.add("display", display);
            }
            
            if (xScaleID != null) {
                result.add("xScaleID", xScaleID);
            }
            
            if (yScaleID != null) {
                result.add("yScaleID", yScaleID);
            }
            
            if (adjustScaleRange != null) {
                result.add("adjustScaleRange", adjustScaleRange);
            }
            
            // Box coordinates
            if (xMin != null) {
                result.add("xMin", xMin);
            }
            
            if (xMax != null) {
                result.add("xMax", xMax);
            }
            
            if (yMin != null) {
                result.add("yMin", yMin);
            }
            
            if (yMax != null) {
                result.add("yMax", yMax);
            }
            
            // Style properties
            if (borderColor != null) {
                result.add("borderColor", borderColor);
            }
            
            if (borderWidth != null) {
                result.add("borderWidth", borderWidth);
            }
            
            if (borderDash != null && borderDash.length > 0) {
                JsonArray dashArray = new JsonArray();
                for (int value : borderDash) {
                    dashArray.add(value);
                }
                result.add("borderDash", dashArray);
            }
            
            if (borderDashOffset != null) {
                result.add("borderDashOffset", borderDashOffset);
            }
            
            if (backgroundColor != null) {
                result.add("backgroundColor", backgroundColor);
            }
            
            // Label configuration
            if (label != null) {
                result.add("label", label.toJson());
            }
            
            // Add any custom properties
            for (Member entry : customConfig.asObject()) {
                result.add(entry.getName(), entry.getValue());
            }
            
            return result;
        }
        
        // Common fluent setters
        public AnnotationBase setId(String id) {
            this.id = id;
            return this;
        }
        
        public AnnotationBase setDrawTime(String drawTime) {
            this.drawTime = drawTime;
            return this;
        }
        
        public AnnotationBase setDisplay(Boolean display) {
            this.display = display;
            return this;
        }
        
        public AnnotationBase setXScaleID(String xScaleID) {
            this.xScaleID = xScaleID;
            return this;
        }
        
        public AnnotationBase setYScaleID(String yScaleID) {
            this.yScaleID = yScaleID;
            return this;
        }
        
        public AnnotationBase setAdjustScaleRange(Boolean adjustScaleRange) {
            this.adjustScaleRange = adjustScaleRange;
            return this;
        }
        
        public AnnotationBase setXMin(Double xMin) {
            this.xMin = xMin;
            return this;
        }
        
        public AnnotationBase setXMax(Double xMax) {
            this.xMax = xMax;
            return this;
        }
        
        public AnnotationBase setYMin(Double yMin) {
            this.yMin = yMin;
            return this;
        }
        
        public AnnotationBase setYMax(Double yMax) {
            this.yMax = yMax;
            return this;
        }
        
        public AnnotationBase setBorderColor(String borderColor) {
            this.borderColor = borderColor;
            return this;
        }
        
        public AnnotationBase setBorderWidth(Integer borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }
        
        public AnnotationBase setBorderDash(int[] borderDash) {
            this.borderDash = borderDash;
            return this;
        }
        
        public AnnotationBase setBorderDashOffset(Integer borderDashOffset) {
            this.borderDashOffset = borderDashOffset;
            return this;
        }
        
        public AnnotationBase setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }
        
        public AnnotationBase setLabel(AnnotationLabel label) {
            this.label = label;
            return this;
        }
        
        public AnnotationLabel createLabel() {
            if (this.label == null) {
                this.label = new AnnotationLabel();
            }
            return this.label;
        }
        
        // Callback setters (in a real implementation, these would handle function references)
        public AnnotationBase setEnterCallback(String callbackCode) {
            this.enterCallback = callbackCode;
            this.customConfig.add("enter", callbackCode);
            return this;
        }
        
        public AnnotationBase setLeaveCallback(String callbackCode) {
            this.leaveCallback = callbackCode;
            this.customConfig.add("leave", callbackCode);
            return this;
        }
        
        public AnnotationBase setClickCallback(String callbackCode) {
            this.clickCallback = callbackCode;
            this.customConfig.add("click", callbackCode);
            return this;
        }
        
        // Custom properties
        public AnnotationBase setCustomProperty(String property, String value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationBase setCustomProperty(String property, int value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationBase setCustomProperty(String property, double value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationBase setCustomProperty(String property, boolean value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationBase setCustomProperty(String property, JsonValue value) {
            customConfig.add(property, value);
            return this;
        }
    }
    
    /**
     * Line annotation
     */
    public static class LineAnnotation extends AnnotationBase {
        private String mode; // 'horizontal', 'vertical'
        private Double value;
        private Double endValue;
        private Boolean scaleID; // deprecated, use xScaleID/yScaleID instead
        
        public LineAnnotation() {
            super("line");
            this.borderColor = "rgba(0,0,0,0.8)";
            this.borderWidth = 2;
        }
        
        @Override
        public JsonObject toJson() {
            JsonObject result = super.toJson();
            
            if (mode != null) {
                result.add("mode", mode);
            }
            
            if (value != null) {
                result.add("value", value);
            }
            
            if (endValue != null) {
                result.add("endValue", endValue);
            }
            
            if (scaleID != null) {
                result.add("scaleID", scaleID);
            }
            
            return result;
        }
        
        // Line-specific setters
        public LineAnnotation setMode(String mode) {
            this.mode = mode;
            return this;
        }
        
        public LineAnnotation setValue(Double value) {
            this.value = value;
            return this;
        }
        
        public LineAnnotation setEndValue(Double endValue) {
            this.endValue = endValue;
            return this;
        }
        
        public LineAnnotation setScaleID(Boolean scaleID) {
            this.scaleID = scaleID;
            return this;
        }
        
        // Override parent methods to return the correct type
        @Override
        public LineAnnotation setId(String id) {
            super.setId(id);
            return this;
        }
        
        @Override
        public LineAnnotation setDrawTime(String drawTime) {
            super.setDrawTime(drawTime);
            return this;
        }
        
        @Override
        public LineAnnotation setDisplay(Boolean display) {
            super.setDisplay(display);
            return this;
        }
        
        @Override
        public LineAnnotation setXScaleID(String xScaleID) {
            super.setXScaleID(xScaleID);
            return this;
        }
        
        @Override
        public LineAnnotation setYScaleID(String yScaleID) {
            super.setYScaleID(yScaleID);
            return this;
        }
        
        @Override
        public LineAnnotation setAdjustScaleRange(Boolean adjustScaleRange) {
            super.setAdjustScaleRange(adjustScaleRange);
            return this;
        }
        
        @Override
        public LineAnnotation setXMin(Double xMin) {
            super.setXMin(xMin);
            return this;
        }
        
        @Override
        public LineAnnotation setXMax(Double xMax) {
            super.setXMax(xMax);
            return this;
        }
        
        @Override
        public LineAnnotation setYMin(Double yMin) {
            super.setYMin(yMin);
            return this;
        }
        
        @Override
        public LineAnnotation setYMax(Double yMax) {
            super.setYMax(yMax);
            return this;
        }
        
        @Override
        public LineAnnotation setBorderColor(String borderColor) {
            super.setBorderColor(borderColor);
            return this;
        }
        
        @Override
        public LineAnnotation setBorderWidth(Integer borderWidth) {
            super.setBorderWidth(borderWidth);
            return this;
        }
        
        @Override
        public LineAnnotation setBorderDash(int[] borderDash) {
            super.setBorderDash(borderDash);
            return this;
        }
        
        @Override
        public LineAnnotation setBorderDashOffset(Integer borderDashOffset) {
            super.setBorderDashOffset(borderDashOffset);
            return this;
        }
        
        @Override
        public LineAnnotation setBackgroundColor(String backgroundColor) {
            super.setBackgroundColor(backgroundColor);
            return this;
        }
        
        @Override
        public LineAnnotation setLabel(AnnotationLabel label) {
            super.setLabel(label);
            return this;
        }
        
        @Override
        public LineAnnotation setEnterCallback(String callbackCode) {
            super.setEnterCallback(callbackCode);
            return this;
        }
        
        @Override
        public LineAnnotation setLeaveCallback(String callbackCode) {
            super.setLeaveCallback(callbackCode);
            return this;
        }
        
        @Override
        public LineAnnotation setClickCallback(String callbackCode) {
            super.setClickCallback(callbackCode);
            return this;
        }
        
        @Override
        public LineAnnotation setCustomProperty(String property, String value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public LineAnnotation setCustomProperty(String property, int value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public LineAnnotation setCustomProperty(String property, double value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public LineAnnotation setCustomProperty(String property, boolean value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public LineAnnotation setCustomProperty(String property, JsonValue value) {
            super.setCustomProperty(property, value);
            return this;
        }
    }
    
    /**
     * Box annotation
     */
    public static class BoxAnnotation extends AnnotationBase {
        private Boolean borderRadius;
        
        public BoxAnnotation() {
            super("box");
            this.backgroundColor = "rgba(0,0,0,0.1)";
            this.borderWidth = 1;
            this.borderColor = "rgba(0,0,0,0.8)";
        }
        
        @Override
        public JsonObject toJson() {
            JsonObject result = super.toJson();
            
            if (borderRadius != null) {
                result.add("borderRadius", borderRadius);
            }
            
            return result;
        }
        
        // Box-specific setters
        public BoxAnnotation setBorderRadius(Boolean borderRadius) {
            this.borderRadius = borderRadius;
            return this;
        }
        
        // Override parent methods to return the correct type
        @Override
        public BoxAnnotation setId(String id) {
            super.setId(id);
            return this;
        }
        
        @Override
        public BoxAnnotation setDrawTime(String drawTime) {
            super.setDrawTime(drawTime);
            return this;
        }
        
        @Override
        public BoxAnnotation setDisplay(Boolean display) {
            super.setDisplay(display);
            return this;
        }
        
        @Override
        public BoxAnnotation setXScaleID(String xScaleID) {
            super.setXScaleID(xScaleID);
            return this;
        }
        
        @Override
        public BoxAnnotation setYScaleID(String yScaleID) {
            super.setYScaleID(yScaleID);
            return this;
        }
        
        @Override
        public BoxAnnotation setAdjustScaleRange(Boolean adjustScaleRange) {
            super.setAdjustScaleRange(adjustScaleRange);
            return this;
        }
        
        @Override
        public BoxAnnotation setXMin(Double xMin) {
            super.setXMin(xMin);
            return this;
        }
        
        @Override
        public BoxAnnotation setXMax(Double xMax) {
            super.setXMax(xMax);
            return this;
        }
        
        @Override
        public BoxAnnotation setYMin(Double yMin) {
            super.setYMin(yMin);
            return this;
        }
        
        @Override
        public BoxAnnotation setYMax(Double yMax) {
            super.setYMax(yMax);
            return this;
        }
        
        @Override
        public BoxAnnotation setBorderColor(String borderColor) {
            super.setBorderColor(borderColor);
            return this;
        }
        
        @Override
        public BoxAnnotation setBorderWidth(Integer borderWidth) {
            super.setBorderWidth(borderWidth);
            return this;
        }
        
        @Override
        public BoxAnnotation setBorderDash(int[] borderDash) {
            super.setBorderDash(borderDash);
            return this;
        }
        
        @Override
        public BoxAnnotation setBorderDashOffset(Integer borderDashOffset) {
            super.setBorderDashOffset(borderDashOffset);
            return this;
        }
        
        @Override
        public BoxAnnotation setBackgroundColor(String backgroundColor) {
            super.setBackgroundColor(backgroundColor);
            return this;
        }
        
        @Override
        public BoxAnnotation setLabel(AnnotationLabel label) {
            super.setLabel(label);
            return this;
        }
        
        @Override
        public BoxAnnotation setEnterCallback(String callbackCode) {
            super.setEnterCallback(callbackCode);
            return this;
        }
        
        @Override
        public BoxAnnotation setLeaveCallback(String callbackCode) {
            super.setLeaveCallback(callbackCode);
            return this;
        }
        
        @Override
        public BoxAnnotation setClickCallback(String callbackCode) {
            super.setClickCallback(callbackCode);
            return this;
        }
        
        @Override
        public BoxAnnotation setCustomProperty(String property, String value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public BoxAnnotation setCustomProperty(String property, int value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public BoxAnnotation setCustomProperty(String property, double value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public BoxAnnotation setCustomProperty(String property, boolean value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public BoxAnnotation setCustomProperty(String property, JsonValue value) {
            super.setCustomProperty(property, value);
            return this;
        }
    }
    
    /**
     * Point annotation
     */
    public static class PointAnnotation extends AnnotationBase {
        private Double radius;
        private Double x;
        private Double y;
        
        public PointAnnotation() {
            super("point");
            this.radius = 10.0;
            this.backgroundColor = "rgba(0,0,0,0.8)";
        }
        
        @Override
        public JsonObject toJson() {
            JsonObject result = super.toJson();
            
            if (radius != null) {
                result.add("radius", radius);
            }
            
            if (x != null) {
                result.add("x", x);
            }
            
            if (y != null) {
                result.add("y", y);
            }
            
            return result;
        }
        
        // Point-specific setters
        public PointAnnotation setRadius(Double radius) {
            this.radius = radius;
            return this;
        }
        
        public PointAnnotation setX(Double x) {
            this.x = x;
            return this;
        }
        
        public PointAnnotation setY(Double y) {
            this.y = y;
            return this;
        }
        
        // Override parent methods to return the correct type
        @Override
        public PointAnnotation setId(String id) {
            super.setId(id);
            return this;
        }
        
        @Override
        public PointAnnotation setDrawTime(String drawTime) {
            super.setDrawTime(drawTime);
            return this;
        }
        
        @Override
        public PointAnnotation setDisplay(Boolean display) {
            super.setDisplay(display);
            return this;
        }
        
        @Override
        public PointAnnotation setXScaleID(String xScaleID) {
            super.setXScaleID(xScaleID);
            return this;
        }
        
        @Override
        public PointAnnotation setYScaleID(String yScaleID) {
            super.setYScaleID(yScaleID);
            return this;
        }
        
        @Override
        public PointAnnotation setAdjustScaleRange(Boolean adjustScaleRange) {
            super.setAdjustScaleRange(adjustScaleRange);
            return this;
        }
        
        @Override
        public PointAnnotation setBorderColor(String borderColor) {
            super.setBorderColor(borderColor);
            return this;
        }
        
        @Override
        public PointAnnotation setBorderWidth(Integer borderWidth) {
            super.setBorderWidth(borderWidth);
            return this;
        }
        
        @Override
        public PointAnnotation setBorderDash(int[] borderDash) {
            super.setBorderDash(borderDash);
            return this;
        }
        
        @Override
        public PointAnnotation setBorderDashOffset(Integer borderDashOffset) {
            super.setBorderDashOffset(borderDashOffset);
            return this;
        }
        
        @Override
        public PointAnnotation setBackgroundColor(String backgroundColor) {
            super.setBackgroundColor(backgroundColor);
            return this;
        }
        
        @Override
        public PointAnnotation setLabel(AnnotationLabel label) {
            super.setLabel(label);
            return this;
        }
        
        @Override
        public PointAnnotation setEnterCallback(String callbackCode) {
            super.setEnterCallback(callbackCode);
            return this;
        }
        
        @Override
        public PointAnnotation setLeaveCallback(String callbackCode) {
            super.setLeaveCallback(callbackCode);
            return this;
        }
        
        @Override
        public PointAnnotation setClickCallback(String callbackCode) {
            super.setClickCallback(callbackCode);
            return this;
        }
        
        @Override
        public PointAnnotation setCustomProperty(String property, String value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PointAnnotation setCustomProperty(String property, int value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PointAnnotation setCustomProperty(String property, double value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PointAnnotation setCustomProperty(String property, boolean value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PointAnnotation setCustomProperty(String property, JsonValue value) {
            super.setCustomProperty(property, value);
            return this;
        }
    }
    
    /**
     * Ellipse annotation
     */
    public static class EllipseAnnotation extends AnnotationBase {
        private Double radiusX;
        private Double radiusY;
        private Double rotation;
        
        public EllipseAnnotation() {
            super("ellipse");
            this.backgroundColor = "rgba(0,0,0,0.1)";
            this.borderColor = "rgba(0,0,0,0.8)";
            this.borderWidth = 1;
        }
        
        @Override
        public JsonObject toJson() {
            JsonObject result = super.toJson();
            
            if (radiusX != null) {
                result.add("radiusX", radiusX);
            }
            
            if (radiusY != null) {
                result.add("radiusY", radiusY);
            }
            
            if (rotation != null) {
                result.add("rotation", rotation);
            }
            
            return result;
        }
        
        // Ellipse-specific setters
        public EllipseAnnotation setRadiusX(Double radiusX) {
            this.radiusX = radiusX;
            return this;
        }
        
        public EllipseAnnotation setRadiusY(Double radiusY) {
            this.radiusY = radiusY;
            return this;
        }
        
        public EllipseAnnotation setRotation(Double rotation) {
            this.rotation = rotation;
            return this;
        }
        
        // Override parent methods to return the correct type
        @Override
        public EllipseAnnotation setId(String id) {
            super.setId(id);
            return this;
        }
        
        @Override
        public EllipseAnnotation setDrawTime(String drawTime) {
            super.setDrawTime(drawTime);
            return this;
        }
        
        @Override
        public EllipseAnnotation setDisplay(Boolean display) {
            super.setDisplay(display);
            return this;
        }
        
        @Override
        public EllipseAnnotation setXScaleID(String xScaleID) {
            super.setXScaleID(xScaleID);
            return this;
        }
        
        @Override
        public EllipseAnnotation setYScaleID(String yScaleID) {
            super.setYScaleID(yScaleID);
            return this;
        }
        
        @Override
        public EllipseAnnotation setAdjustScaleRange(Boolean adjustScaleRange) {
            super.setAdjustScaleRange(adjustScaleRange);
            return this;
        }
        
        @Override
        public EllipseAnnotation setXMin(Double xMin) {
            super.setXMin(xMin);
            return this;
        }
        
        @Override
        public EllipseAnnotation setXMax(Double xMax) {
            super.setXMax(xMax);
            return this;
        }
        
        @Override
        public EllipseAnnotation setYMin(Double yMin) {
            super.setYMin(yMin);
            return this;
        }
        
        @Override
        public EllipseAnnotation setYMax(Double yMax) {
            super.setYMax(yMax);
            return this;
        }
        
        @Override
        public EllipseAnnotation setBorderColor(String borderColor) {
            super.setBorderColor(borderColor);
            return this;
        }
        
        @Override
        public EllipseAnnotation setBorderWidth(Integer borderWidth) {
            super.setBorderWidth(borderWidth);
            return this;
        }
        
        @Override
        public EllipseAnnotation setBorderDash(int[] borderDash) {
            super.setBorderDash(borderDash);
            return this;
        }
        
        @Override
        public EllipseAnnotation setBorderDashOffset(Integer borderDashOffset) {
            super.setBorderDashOffset(borderDashOffset);
            return this;
        }
        
        @Override
        public EllipseAnnotation setBackgroundColor(String backgroundColor) {
            super.setBackgroundColor(backgroundColor);
            return this;
        }
        
        @Override
        public EllipseAnnotation setLabel(AnnotationLabel label) {
            super.setLabel(label);
            return this;
        }
        
        @Override
        public EllipseAnnotation setEnterCallback(String callbackCode) {
            super.setEnterCallback(callbackCode);
            return this;
        }
        
        @Override
        public EllipseAnnotation setLeaveCallback(String callbackCode) {
            super.setLeaveCallback(callbackCode);
            return this;
        }
        
        @Override
        public EllipseAnnotation setClickCallback(String callbackCode) {
            super.setClickCallback(callbackCode);
            return this;
        }
        
        @Override
        public EllipseAnnotation setCustomProperty(String property, String value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public EllipseAnnotation setCustomProperty(String property, int value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public EllipseAnnotation setCustomProperty(String property, double value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public EllipseAnnotation setCustomProperty(String property, boolean value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public EllipseAnnotation setCustomProperty(String property, JsonValue value) {
            super.setCustomProperty(property, value);
            return this;
        }
    }
    
    /**
     * Polygon annotation
     */
    public static class PolygonAnnotation extends AnnotationBase {
        private JsonArray points;
        
        public PolygonAnnotation() {
            super("polygon");
            this.backgroundColor = "rgba(0,0,0,0.1)";
            this.borderColor = "rgba(0,0,0,0.8)";
            this.borderWidth = 1;
            this.points = new JsonArray();
        }
        
        @Override
        public JsonObject toJson() {
            JsonObject result = super.toJson();
            
            if (points != null && points.size() > 0) {
                result.add("points", points);
            }
            
            return result;
        }
        
        // Polygon-specific setters
        public PolygonAnnotation addPoint(Double x, Double y) {
            JsonObject point = new JsonObject();
            point.add("x", x);
            point.add("y", y);
            points.add(point);
            return this;
        }
        
        // Override parent methods to return the correct type
        @Override
        public PolygonAnnotation setId(String id) {
            super.setId(id);
            return this;
        }
        
        @Override
        public PolygonAnnotation setDrawTime(String drawTime) {
            super.setDrawTime(drawTime);
            return this;
        }
        
        @Override
        public PolygonAnnotation setDisplay(Boolean display) {
            super.setDisplay(display);
            return this;
        }
        
        @Override
        public PolygonAnnotation setXScaleID(String xScaleID) {
            super.setXScaleID(xScaleID);
            return this;
        }
        
        @Override
        public PolygonAnnotation setYScaleID(String yScaleID) {
            super.setYScaleID(yScaleID);
            return this;
        }
        
        @Override
        public PolygonAnnotation setAdjustScaleRange(Boolean adjustScaleRange) {
            super.setAdjustScaleRange(adjustScaleRange);
            return this;
        }
        
        @Override
        public PolygonAnnotation setBorderColor(String borderColor) {
            super.setBorderColor(borderColor);
            return this;
        }
        
        @Override
        public PolygonAnnotation setBorderWidth(Integer borderWidth) {
            super.setBorderWidth(borderWidth);
            return this;
        }
        
        @Override
        public PolygonAnnotation setBorderDash(int[] borderDash) {
            super.setBorderDash(borderDash);
            return this;
        }
        
        @Override
        public PolygonAnnotation setBorderDashOffset(Integer borderDashOffset) {
            super.setBorderDashOffset(borderDashOffset);
            return this;
        }
        
        @Override
        public PolygonAnnotation setBackgroundColor(String backgroundColor) {
            super.setBackgroundColor(backgroundColor);
            return this;
        }
        
        @Override
        public PolygonAnnotation setLabel(AnnotationLabel label) {
            super.setLabel(label);
            return this;
        }
        
        @Override
        public PolygonAnnotation setEnterCallback(String callbackCode) {
            super.setEnterCallback(callbackCode);
            return this;
        }
        
        @Override
        public PolygonAnnotation setLeaveCallback(String callbackCode) {
            super.setLeaveCallback(callbackCode);
            return this;
        }
        
        @Override
        public PolygonAnnotation setClickCallback(String callbackCode) {
            super.setClickCallback(callbackCode);
            return this;
        }
        
        @Override
        public PolygonAnnotation setCustomProperty(String property, String value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PolygonAnnotation setCustomProperty(String property, int value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PolygonAnnotation setCustomProperty(String property, double value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PolygonAnnotation setCustomProperty(String property, boolean value) {
            super.setCustomProperty(property, value);
            return this;
        }
        
        @Override
        public PolygonAnnotation setCustomProperty(String property, JsonValue value) {
            super.setCustomProperty(property, value);
            return this;
        }
    }
    
    /**
     * Represents label configuration for annotations
     */
    public static class AnnotationLabel {
        private Boolean display;
        private String content;
        private String position;
        private Integer rotation;
        private JsonObject font;
        private String color;
        private String backgroundColor;
        private Integer borderRadius;
        private Integer borderWidth;
        private String borderColor;
        private JsonObject padding;
        private String textAlign;
        private JsonObject customConfig = new JsonObject();
        
        public AnnotationLabel() {
            this.display = true;
            this.position = "center";
            this.backgroundColor = "rgba(0,0,0,0.8)";
            this.color = "#fff";
            this.borderRadius = 3;
            
            // Default padding
            this.padding = new JsonObject();
            this.padding.add("top", 6);
            this.padding.add("right", 6);
            this.padding.add("bottom", 6);
            this.padding.add("left", 6);
            
            // Default font
            this.font = new JsonObject();
            this.font.add("size", 12);
            this.font.add("style", "normal");
            this.font.add("family", "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif");
        }
        
        public JsonObject toJson() {
            JsonObject result = new JsonObject();
            
            if (display != null) {
                result.add("display", display);
            }
            
            if (content != null) {
                result.add("content", content);
            }
            
            if (position != null) {
                result.add("position", position);
            }
            
            if (rotation != null) {
                result.add("rotation", rotation);
            }
            
            if (font != null) {
                result.add("font", font);
            }
            
            if (color != null) {
                result.add("color", color);
            }
            
            if (backgroundColor != null) {
                result.add("backgroundColor", backgroundColor);
            }
            
            if (borderRadius != null) {
                result.add("borderRadius", borderRadius);
            }
            
            if (borderWidth != null) {
                result.add("borderWidth", borderWidth);
            }
            
            if (borderColor != null) {
                result.add("borderColor", borderColor);
            }
            
            if (padding != null) {
                result.add("padding", padding);
            }
            
            if (textAlign != null) {
                result.add("textAlign", textAlign);
            }
            
            // Add any custom properties
            for (Member entry : customConfig.asObject()) {
                result.add(entry.getName(), entry.getValue());
            }
            
            return result;
        }
        
        // Fluent setters
        public AnnotationLabel setDisplay(Boolean display) {
            this.display = display;
            return this;
        }
        
        public AnnotationLabel setContent(String content) {
            this.content = content;
            return this;
        }
        
        public AnnotationLabel setPosition(String position) {
            this.position = position;
            return this;
        }
        
        public AnnotationLabel setRotation(Integer rotation) {
            this.rotation = rotation;
            return this;
        }
        
        // Font configuration
        public AnnotationLabel setFontSize(Integer size) {
            this.font.add("size", size);
            return this;
        }
        
        public AnnotationLabel setFontStyle(String style) {
            this.font.add("style", style);
            return this;
        }
        
        public AnnotationLabel setFontFamily(String family) {
            this.font.add("family", family);
            return this;
        }
        
        public AnnotationLabel setFontColor(String color) {
            this.color = color;
            return this;
        }
        
        // Style configuration
        public AnnotationLabel setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }
        
        public AnnotationLabel setBorderRadius(Integer borderRadius) {
            this.borderRadius = borderRadius;
            return this;
        }
        
        public AnnotationLabel setBorderWidth(Integer borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }
        
        public AnnotationLabel setBorderColor(String borderColor) {
            this.borderColor = borderColor;
            return this;
        }
        
        // Padding configuration
        public AnnotationLabel setPadding(Integer padding) {
            this.padding = new JsonObject();
            this.padding.add("top", padding);
            this.padding.add("right", padding);
            this.padding.add("bottom", padding);
            this.padding.add("left", padding);
            return this;
        }
        
        public AnnotationLabel setPadding(Integer top, Integer right, Integer bottom, Integer left) {
            this.padding = new JsonObject();
            this.padding.add("top", top);
            this.padding.add("right", right);
            this.padding.add("bottom", bottom);
            this.padding.add("left", left);
            return this;
        }
        
        public AnnotationLabel setTextAlign(String textAlign) {
            this.textAlign = textAlign;
            return this;
        }
        
        // Custom properties
        public AnnotationLabel setCustomProperty(String property, String value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationLabel setCustomProperty(String property, int value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationLabel setCustomProperty(String property, double value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationLabel setCustomProperty(String property, boolean value) {
            customConfig.add(property, value);
            return this;
        }
        
        public AnnotationLabel setCustomProperty(String property, JsonValue value) {
            customConfig.add(property, value);
            return this;
        }
    }
}
