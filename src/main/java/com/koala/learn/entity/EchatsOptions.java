package com.koala.learn.entity;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import java.time.format.TextStyle;
import java.util.List;

public class EchatsOptions {
    private TitleBean title;
    private TooltipBean tooltip;
    private LegendBean legend;
    private List<XAxisBean> xAxis;
    private List<YAxisBean> yAxis;
    private List<SeriesBean> series;

    public TitleBean getTitle() {
        return title;
    }

    public void setTitle(TitleBean title) {
        this.title = title;
    }

    public TooltipBean getTooltip() {
        return tooltip;
    }

    public void setTooltip(TooltipBean tooltip) {
        this.tooltip = tooltip;
    }

    public LegendBean getLegend() {
        return legend;
    }

    public void setLegend(LegendBean legend) {
        this.legend = legend;
    }

    public List<XAxisBean> getXAxis() {
        return xAxis;
    }

    public void setXAxis(List<XAxisBean> xAxis) {
        this.xAxis = xAxis;
    }

    public List<YAxisBean> getYAxis() {
        return yAxis;
    }

    public void setYAxis(List<YAxisBean> yAxis) {
        this.yAxis = yAxis;
    }

    public List<SeriesBean> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesBean> series) {
        this.series = series;
    }

    public static class TitleBean {
        public TitleBean(String text, String subtext) {
            this.text = text;
            this.subtext = subtext;
            this.textStyle=new TextStyleBeanTitle();
        }

        /**
         * text : 男性女性身高体重分布
         * subtext : 抽样调查来自: Heinz  2003
         */



        private String text;
        private String subtext;

        private TextStyleBeanTitle textStyle;

        public static class TextStyleBeanTitle{
            String color;
            public TextStyleBeanTitle(){
                color="#fcfcfc";
            }
        }


        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSubtext() {
            return subtext;
        }

        public void setSubtext(String subtext) {
            this.subtext = subtext;
        }
    }

    public static class TooltipBean {
        /**
         * trigger : axis
         * showDelay : 0
         * axisPointer : {"show":true,"type":"cross","lineStyle":{"type":"dashed","width":1}}
         */

        private String trigger;
        private int showDelay;
        private AxisPointerBean axisPointer;

        public String getTrigger() {
            return trigger;
        }

        public void setTrigger(String trigger) {
            this.trigger = trigger;
        }

        public int getShowDelay() {
            return showDelay;
        }

        public void setShowDelay(int showDelay) {
            this.showDelay = showDelay;
        }

        public AxisPointerBean getAxisPointer() {
            return axisPointer;
        }

        public void setAxisPointer(AxisPointerBean axisPointer) {
            this.axisPointer = axisPointer;
        }

        public static class AxisPointerBean {
            /**
             * show : true
             * type : cross
             * lineStyle : {"type":"dashed","width":1}
             */

            private boolean show;
            private String type;
            private LineStyleBean lineStyle;

            public boolean isShow() {
                return show;
            }

            public void setShow(boolean show) {
                this.show = show;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public LineStyleBean getLineStyle() {
                return lineStyle;
            }

            public void setLineStyle(LineStyleBean lineStyle) {
                this.lineStyle = lineStyle;
            }

            public static class LineStyleBean {
                /**
                 * type : dashed
                 * width : 1
                 */

                private String type;
                private int width;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }
            }
        }
    }

    public static class LegendBean {
        private List<String> data;

        public LegendBean(List<String> data) {
            this.data = data;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }

    public static class XAxisBean {
        public XAxisBean(String type, boolean scale, AxisLabelBean axisLabel) {
            this.type = type;
            this.scale = scale;
            this.axisLabel = axisLabel;
        }

        /**
         * type : value
         * scale : true
         * axisLabel : {"formatter":"{value} cm"}
         */


        private String type;
        private boolean scale;
        private AxisLabelBean axisLabel;
        private List<String> data;

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isScale() {
            return scale;
        }

        public void setScale(boolean scale) {
            this.scale = scale;
        }

        public AxisLabelBean getAxisLabel() {
            return axisLabel;
        }

        public void setAxisLabel(AxisLabelBean axisLabel) {
            this.axisLabel = axisLabel;
        }

        public static class AxisLabelBean {
            /**
             * formatter : {value} cm
             */

            private String formatter;
            private boolean show;
            private TextStyleBeanX textStyle;

            public AxisLabelBean() {
                show =true;
                textStyle=new TextStyleBeanX();
            }
            public static class TextStyleBeanX{
                private String color;
                public TextStyleBeanX(){
                    color="#ffffff";
                }
            }

            public String getFormatter() {
                return formatter;
            }

            public void setFormatter(String formatter) {
                this.formatter = formatter;
            }
        }
    }

    public static class YAxisBean {

        public YAxisBean(String type, boolean scale, AxisLabelBeanX axisLabel) {
            this.type = type;
            this.scale = scale;
            this.axisLabel = axisLabel;
        }

        /**
         * type : value
         * scale : true
         * axisLabel : {"formatter":"{value} kg"}
         */


        private String type;
        private boolean scale;
        private AxisLabelBeanX axisLabel;
        private List<?> data;

        public List<?> getData() {
            return data;
        }

        public void setData(List<?> data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isScale() {
            return scale;
        }

        public void setScale(boolean scale) {
            this.scale = scale;
        }

        public AxisLabelBeanX getAxisLabel() {
            return axisLabel;
        }

        public void setAxisLabel(AxisLabelBeanX axisLabel) {
            this.axisLabel = axisLabel;
        }

        public static class AxisLabelBeanX {
            /**
             * formatter : {value} kg
             */

            private String formatter;
            private boolean show;
            private TextStyleBeanY textStyle;
            public AxisLabelBeanX() {
                show =true;
                textStyle=new TextStyleBeanY();
            }
            public static class TextStyleBeanY{
                private String color;
                public TextStyleBeanY(){
                    color="#ffffff";
                }
            }

            public String getFormatter() {
                return formatter;
            }

            public void setFormatter(String formatter) {
                this.formatter = formatter;
            }
        }
    }

    public static class SeriesBean {
        /**
         * name : 女性
         * type : scatter
         * itemStyle : {"normal":{"color":"#ff0000"}}
         * data : [[161.2,51.6],[167.5,59],[159.5,49.2],[157,63],[155.8,53.6],[170,59],[159.1,47.6],[166,69.8],[176.2,66.8],[160.2,75.2],[172.5,55.2],[170.9,54.2],[172.9,62.5],[153.4,42],[160,50],[147.2,49.8],[168.2,49.2],[175,73.2],[157,47.8],[167.6,68.8],[159.5,50.6],[175,82.5],[166.8,57.2],[176.5,87.8],[170.2,72.8],[174,54.5],[173,59.8],[179.9,67.3],[170.5,67.8],[160,47],[154.4,46.2],[162,55],[176.5,83],[160,54.4],[152,45.8],[162.1,53.6],[170,73.2],[160.2,52.1],[161.3,67.9],[166.4,56.6],[168.9,62.3],[163.8,58.5],[167.6,54.5],[160,50.2],[161.3,60.3],[167.6,58.3],[165.1,56.2],[160,50.2],[170,72.9],[157.5,59.8],[167.6,61],[160.7,69.1],[163.2,55.9],[152.4,46.5],[157.5,54.3],[168.3,54.8],[180.3,60.7],[165.5,60],[165,62],[164.5,60.3],[156,52.7],[160,74.3],[163,62],[165.7,73.1],[161,80],[162,54.7],[166,53.2],[174,75.7],[172.7,61.1],[167.6,55.7],[151.1,48.7],[164.5,52.3],[163.5,50],[152,59.3],[169,62.5],[164,55.7],[161.2,54.8],[155,45.9],[170,70.6],[176.2,67.2],[170,69.4],[162.5,58.2],[170.3,64.8],[164.1,71.6],[169.5,52.8],[163.2,59.8],[154.5,49],[159.8,50],[173.2,69.2],[170,55.9],[161.4,63.4],[169,58.2],[166.2,58.6],[159.4,45.7],[162.5,52.2],[159,48.6],[162.8,57.8],[159,55.6],[179.8,66.8],[162.9,59.4],[161,53.6],[151.1,73.2],[168.2,53.4],[168.9,69],[173.2,58.4],[171.8,56.2],[178,70.6],[164.3,59.8],[163,72],[168.5,65.2],[166.8,56.6],[172.7,105.2],[163.5,51.8],[169.4,63.4],[167.8,59],[159.5,47.6],[167.6,63],[161.2,55.2],[160,45],[163.2,54],[162.2,50.2],[161.3,60.2],[149.5,44.8],[157.5,58.8],[163.2,56.4],[172.7,62],[155,49.2],[156.5,67.2],[164,53.8],[160.9,54.4],[162.8,58],[167,59.8],[160,54.8],[160,43.2],[168.9,60.5],[158.2,46.4],[156,64.4],[160,48.8],[167.1,62.2],[158,55.5],[167.6,57.8],[156,54.6],[162.1,59.2],[173.4,52.7],[159.8,53.2],[170.5,64.5],[159.2,51.8],[157.5,56],[161.3,63.6],[162.6,63.2],[160,59.5],[168.9,56.8],[165.1,64.1],[162.6,50],[165.1,72.3],[166.4,55],[160,55.9],[152.4,60.4],[170.2,69.1],[162.6,84.5],[170.2,55.9],[158.8,55.5],[172.7,69.5],[167.6,76.4],[162.6,61.4],[167.6,65.9],[156.2,58.6],[175.2,66.8],[172.1,56.6],[162.6,58.6],[160,55.9],[165.1,59.1],[182.9,81.8],[166.4,70.7],[165.1,56.8],[177.8,60],[165.1,58.2],[175.3,72.7],[154.9,54.1],[158.8,49.1],[172.7,75.9],[168.9,55],[161.3,57.3],[167.6,55],[165.1,65.5],[175.3,65.5],[157.5,48.6],[163.8,58.6],[167.6,63.6],[165.1,55.2],[165.1,62.7],[168.9,56.6],[162.6,53.9],[164.5,63.2],[176.5,73.6],[168.9,62],[175.3,63.6],[159.4,53.2],[160,53.4],[170.2,55],[162.6,70.5],[167.6,54.5],[162.6,54.5],[160.7,55.9],[160,59],[157.5,63.6],[162.6,54.5],[152.4,47.3],[170.2,67.7],[165.1,80.9],[172.7,70.5],[165.1,60.9],[170.2,63.6],[170.2,54.5],[170.2,59.1],[161.3,70.5],[167.6,52.7],[167.6,62.7],[165.1,86.3],[162.6,66.4],[152.4,67.3],[168.9,63],[170.2,73.6],[175.2,62.3],[175.2,57.7],[160,55.4],[165.1,104.1],[174,55.5],[170.2,77.3],[160,80.5],[167.6,64.5],[167.6,72.3],[167.6,61.4],[154.9,58.2],[162.6,81.8],[175.3,63.6],[171.4,53.4],[157.5,54.5],[165.1,53.6],[160,60],[174,73.6],[162.6,61.4],[174,55.5],[162.6,63.6],[161.3,60.9],[156.2,60],[149.9,46.8],[169.5,57.3],[160,64.1],[175.3,63.6],[169.5,67.3],[160,75.5],[172.7,68.2],[162.6,61.4],[157.5,76.8],[176.5,71.8],[164.4,55.5],[160.7,48.6],[174,66.4],[163.8,67.3]]
         */

        private String name;
        private String type;
        private ItemStyleBean itemStyle;
        private int symbolSize;
        private List<?> data;

        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }

        public int getSymbolSize() {
            return symbolSize;
        }

        public void setSymbolSize(int symbolSize) {
            this.symbolSize = symbolSize;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ItemStyleBean getItemStyle() {
            return itemStyle;
        }

        public void setItemStyle(ItemStyleBean itemStyle) {
            this.itemStyle = itemStyle;
        }

        public List<?> getData() {
            return data;
        }

        public void setData(List<?> data) {
            this.data = data;
        }

        public static class ItemStyleBean {
            public ItemStyleBean(NormalBean normal) {
                this.normal = normal;
            }

            /**
             * normal : {"color":"#ff0000"}
             */

            private NormalBean normal;

            public NormalBean getNormal() {
                return normal;
            }

            public void setNormal(NormalBean normal) {
                this.normal = normal;
            }

            public static class NormalBean {
                public NormalBean(String color) {
                    this.color = color;
                }

                /**
                 * color : #ff0000
                 */


                private String color;

                public String getColor() {
                    return color;
                }

                public void setColor(String color) {
                    this.color = color;
                }
            }
        }
    }
}
