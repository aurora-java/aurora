package aurora.presentation.component.std;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;

public class Chart extends Component {

	public Chart(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";

	public static String INPUT_TYPE = "inputtype";
	public static String DEFAULT_INPUT_TYPE = "input";
	private static final String PROPERTITY_SERIES_NAME = "seriesName";
	private static final String PROPERTITY_THEME = "chartTheme";

	private static final String PROPERTITY_CHART_ALIGNTICKS = "alignTicks";
	private static final String PROPERTITY_CHART_ANIMATION = "animation";
	private static final String PROPERTITY_CHART_BACKGROUNDCOLOR = "backgroundColor";
	private static final String PROPERTITY_CHART_BORDERCOLOR = "borderColor";
	private static final String PROPERTITY_CHART_BORDERRADIUS = "borderRadius";
	private static final String PROPERTITY_CHART_BORDERWIDTH = "borderWidth";
	private static final String PROPERTITY_CHART_CLASSNAME = "className";
	private static final String PROPERTITY_CHART_DEFAULTSERIESTYPE = "defaultSeriesType";
	private static final String PROPERTITY_CHART_IGNORE_HIDDEN_SERIES = "ignoreHiddenSeries";
	private static final String PROPERTITY_CHART_INVERTED = "inverted";
	private static final String PROPERTITY_CHART_MARGIN = "margin";
	private static final String PROPERTITY_CHART_MARGIN_TOP = "marginTop";
	private static final String PROPERTITY_CHART_MARGIN_RIGHT = "marginRight";
	private static final String PROPERTITY_CHART_MARGIN_LEFT = "marginLeft";
	private static final String PROPERTITY_CHART_MARGIN_BOTTOM = "marginBottom";
	private static final String PROPERTITY_CHART_PINCH_TYPE = "pinchType";
	private static final String PROPERTITY_CHART_PLOT_BACKGROUND_COLOR = "plotBackgroundColor";
	private static final String PROPERTITY_CHART_PLOT_BACKGROUND_IMAGE = "plotBackgroundImage";
	private static final String PROPERTITY_CHART_PLOT_BORDER_COLOR = "plotBorderColor";
	private static final String PROPERTITY_CHART_PLOT_BORDER_WIDTH = "plotBorderWidth";
	private static final String PROPERTITY_CHART_PLOT_SHADOW = "plotShadow";
	private static final String PROPERTITY_CHART_REFLOW = "reflow";
	private static final String PROPERTITY_CHART_RENDERTO = "renderTo";
	private static final String PROPERTITY_CHART_SELECTIONMARKERFILL = "selectionMarkerFill";
	private static final String PROPERTITY_CHART_SHADOW = "shadow";
	private static final String PROPERTITY_CHART_SHOW_AXES = "showAxes";
	private static final String PROPERTITY_CHART_SPACING_TOP = "spacingTop";
	private static final String PROPERTITY_CHART_SPACING_RIGHT = "spacingRight";
	private static final String PROPERTITY_CHART_SPACING_BOTTOM = "spacingBottom";
	private static final String PROPERTITY_CHART_SPACING_LEFT = "spacingLeft";
	private static final String PROPERTITY_CHART_STYLE = "chartStyle";
	private static final String PROPERTITY_CHART_TYPE = "type";
	private static final String PROPERTITY_CHART_ZOOMTYPE = "zoomType";

	private static final String PROPERTITY_CHART_RESETZOOMBUTTON = "resetZoomButton";
	private static final String PROPERTITY_CHART_RESETZOOMBUTTON_RELATIVETO = "relativeTo";
	private static final String PROPERTITY_CHART_RESETZOOMBUTTON_THEME = "theme";

	private static final String PROPERTITY_CHART_POLAR = "polar";
	private static final String PROPERTITY_CHART_NAME_FIELD = "namefield";
	private static final String PROPERTITY_CHART_VALUE_FIELD = "valuefield";
	private static final String PROPERTITY_CHART_GROUPBY = "groupBy";

	private static final String PROPERTITY_CREDITS = "credits";
	private static final String PROPERTITY_CREDITS_ENABLED = "enabled";

	private static final String PROPERTITY_POSITION = "position";
	private static final String PROPERTITY_POSITION_ALIGN = "align";
	private static final String PROPERTITY_POSITION_X = "x";
	private static final String PROPERTITY_POSITION_VERTICALALIGN = "verticalAlign";
	private static final String PROPERTITY_POSITION_Y = "y";

	private static final String PROPERTITY_CREDITS_HREF = "href";
	private static final String PROPERTITY_CREDITS_STYLE = "style";
	private static final String PROPERTITY_CREDITS_TEXT = "text";

	private static final String PROPERTITY_LABELS = "chartLabels";
	private static final String PROPERTITY_LABELS_STYLE = "style";
	private static final String PROPERTITY_LABELS_LABEL_HTML = "html";
	private static final String PROPERTITY_LABELS_LABEL_STYLE = "style";
	private static final String PROPERTITY_LABELS_ITEMS = "items";

	private static final String PROPERTITY_LOADING = "loading";
	private static final String PROPERTITY_LOADING_HIDEDURATION = "hideDuration";
	private static final String PROPERTITY_LOADING_LABELSTYLE = "labelStyle";
	private static final String PROPERTITY_LOADING_SHOWDURATION = "showDuration";
	private static final String PROPERTITY_LOADING_STYLE = "style";

	private static final String PROPERTITY_LEGEND = "legend";
	private static final String PROPERTITY_LEGEND_ALIGN = "align";
	private static final String PROPERTITY_LEGEND_BACKGROUNDCOLOR = "backgroundColor";
	private static final String PROPERTITY_LEGEND_BORDERCOLOR = "borderColor";
	private static final String PROPERTITY_LEGEND_BORDERRADIUS = "borderRadius";
	private static final String PROPERTITY_LEGEND_BORDERWIDTH = "borderWidth";
	private static final String PROPERTITY_LEGEND_ENABLED = "enabled";
	private static final String PROPERTITY_LEGEND_FLOATING = "floating";
	private static final String PROPERTITY_LEGEND_ITEMDISTANCE = "itemDistance";
	private static final String PROPERTITY_LEGEND_ITEMHIDDENSTYLE = "itemHiddenStyle";
	private static final String PROPERTITY_LEGEND_ITEMHOVERSTYLE = "itemHoverStyle";
	private static final String PROPERTITY_LEGEND_ITEMMARGINBOTTOM = "itemMarginBottom";
	private static final String PROPERTITY_LEGEND_ITEMMARGINTOP = "itemMarginTop";
	private static final String PROPERTITY_LEGEND_ITEMSTYLE = "itemStyle";
	private static final String PROPERTITY_LEGEND_ITEMWIDTH = "itemWidth";
	private static final String PROPERTITY_LEGEND_LABELFORMAT = "labelFormat";
	private static final String PROPERTITY_LEGEND_LABELFORMATTER = "labelFormatter";
	private static final String PROPERTITY_LEGEND_LAYOUT = "layout";
	private static final String PROPERTITY_LEGEND_LINEHEIGHT = "lineHeight";
	private static final String PROPERTITY_LEGEND_MARGIN = "margin";
	private static final String PROPERTITY_LEGEND_MAXHEIGHT = "maxHeight";
	private static final String PROPERTITY_LEGEND_NAVIGATION = "legendNavigation";
	private static final String PROPERTITY_LEGEND_NAVIGATION_ACTIVECOLOR = "activeColor";
	private static final String PROPERTITY_LEGEND_NAVIGATION_ANIMATION = "animation";
	private static final String PROPERTITY_LEGEND_NAVIGATION_ARROWSIZE = "arrowSize";
	private static final String PROPERTITY_LEGEND_NAVIGATION_INACTIVECOLOR = "inactiveColor";
	private static final String PROPERTITY_LEGEND_NAVIGATION_STYLE = "style";
	private static final String PROPERTITY_LEGEND_PADDING = "padding";
	private static final String PROPERTITY_LEGEND_REVERSED = "reversed";
	private static final String PROPERTITY_LEGEND_RTL = "rtl";
	private static final String PROPERTITY_LEGEND_SHADOW = "shadow";
	private static final String PROPERTITY_LEGEND_STYLE = "style";
	private static final String PROPERTITY_LEGEND_SYMBOLPADDING = "symbolPadding";
	private static final String PROPERTITY_LEGEND_SYMBOLWIDTH = "symbolWidth";

	private static final String PROPERTITY_LEGEND_TITLE = "legendTitle";

	private static final String PROPERTITY_LEGEND_USEHTML = "useHTML";
	private static final String PROPERTITY_LEGEND_VERTICALALIGN = "verticalAlign";
	private static final String PROPERTITY_LEGEND_WIDTH = "width";
	private static final String PROPERTITY_LEGEND_X = "x";
	private static final String PROPERTITY_LEGEND_Y = "y";

	private static final String PROPERTITY_SUBTITLE = "subtitle";
	private static final String PROPERTITY_SUBTITLE_ALIGN = "align";
	private static final String PROPERTITY_SUBTITLE_FLOATING = "floating";
	private static final String PROPERTITY_SUBTITLE_STYLE = "style";
	private static final String PROPERTITY_SUBTITLE_TEXT = "text";
	private static final String PROPERTITY_SUBTITLE_USEHTML = "useHTML";
	private static final String PROPERTITY_SUBTITLE_VERTICALALIGN = "verticalAlign";
	private static final String PROPERTITY_SUBTITLE_X = "x";
	private static final String PROPERTITY_SUBTITLE_Y = "y";

	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_TITLE_ALIGN = "align";
	private static final String PROPERTITY_TITLE_FLOATING = "floating";
	private static final String PROPERTITY_TITLE_MARGIN = "margin";
	private static final String PROPERTITY_TITLE_STYLE = "style";
	private static final String PROPERTITY_TITLE_TEXT = "text";
	private static final String PROPERTITY_TITLE_USEHTML = "useHTML";
	private static final String PROPERTITY_TITLE_VERTICALALIGN = "verticalAlign";
	private static final String PROPERTITY_TITLE_X = "x";
	private static final String PROPERTITY_TITLE_Y = "y";

	private static final String PROPERTITY_TOOLTIP = "tooltip";
	private static final String PROPERTITY_TOOLTIP_ANIMATION = "animation";
	private static final String PROPERTITY_TOOLTIP_BACKGROUNDCOLOR = "backgroundColor";
	private static final String PROPERTITY_TOOLTIP_BORDERCOLOR = "borderColor";
	private static final String PROPERTITY_TOOLTIP_BORDERRADIUS = "borderRadius";
	private static final String PROPERTITY_TOOLTIP_BORDERWIDTH = "borderWidth";

	private static final String PROPERTITY_TOOLTIP_CROSSHAIRS = "crosshairs";
	private static final String PROPERTITY_TOOLTIP_CROSSHAIRS_WIDTH = "width";
	private static final String PROPERTITY_TOOLTIP_CROSSHAIRS_COLOR = "color";
	private static final String PROPERTITY_TOOLTIP_CROSSHAIRS_DASHSTYLE = "dashStyle";

	private static final String PROPERTITY_TOOLTIP_ENABLED = "enabled";
	private static final String PROPERTITY_TOOLTIP_FOLLOWPOINTER = "followPointer";
	private static final String PROPERTITY_TOOLTIP_FOLLOWTOUCHMOVE = "followTouchMove";
	private static final String PROPERTITY_TOOLTIP_FOOTERFORMAT = "footerFormat";
	private static final String PROPERTITY_TOOLTIP_FORMATTER = "formatter";
	private static final String PROPERTITY_TOOLTIP_HEADERFORMAT = "headerFormat";
	private static final String PROPERTITY_TOOLTIP_HIDEDELAY = "hideDelay";
	private static final String PROPERTITY_TOOLTIP_POINTFORMAT = "pointFormat";
	private static final String PROPERTITY_TOOLTIP_POSITIONER = "positioner";
	private static final String PROPERTITY_TOOLTIP_SHADOW = "shadow";
	private static final String PROPERTITY_TOOLTIP_SHARED = "shared";
	private static final String PROPERTITY_TOOLTIP_SNAP = "snap";
	private static final String PROPERTITY_TOOLTIP_STYLE = "style";
	private static final String PROPERTITY_TOOLTIP_USEHTML = "useHTML";
	private static final String PROPERTITY_TOOLTIP_VALUEDECIMALS = "valueDecimals";
	private static final String PROPERTITY_TOOLTIP_VALUEPREFIX = "valuePrefix";
	private static final String PROPERTITY_TOOLTIP_VALUESUFFIX = "valueSuffix";
	private static final String PROPERTITY_TOOLTIP_XDATEFORMAT = "xDateFormat";

	private static final String PROPERTITY_EXPORTING = "exporting";
	private static final String PROPERTITY_EXPORTING_BUTTONS = "buttons";
	private static final String PROPERTITY_EXPORTING_BUTTONS_CONTEXTBUTTON = "contextButton";
	private static final String PROPERTITY_EXPORTING_CHARTOPTIONS = "chartOptions";
	private static final String PROPERTITY_EXPORTING_ENABLED = "enabled";
	private static final String PROPERTITY_EXPORTING_FILENAME = "filename";
	private static final String PROPERTITY_EXPORTING_SCALE = "scale";
	private static final String PROPERTITY_EXPORTING_SOURCEHEIGHT = "sourceHeight";
	private static final String PROPERTITY_EXPORTING_SOURCEWIDTH = "sourceWidth";
	private static final String PROPERTITY_EXPORTING_TYPE = "type";
	private static final String PROPERTITY_EXPORTING_URL = "url";
	private static final String PROPERTITY_EXPORTING_WIDTH = "width";

	private static final String PROPERTITY_NAVIGATION = "navigation";

	private static final String PROPERTITY_BUTTON_OPTIONS = "buttonOptions";
	private static final String PROPERTITY_BUTTON_OPTIONS_ALIGN = "align";
	private static final String PROPERTITY_BUTTON_OPTIONS_ENABLED = "enabled";
	private static final String PROPERTITY_BUTTON_OPTIONS_HEIGHT = "height";
	private static final String PROPERTITY_BUTTON_OPTIONS_MENUITEMS = "menuItems";
	private static final String PROPERTITY_BUTTON_OPTIONS_ONCLICK = "onclick";
	private static final String PROPERTITY_BUTTON_OPTIONS_SYMBOL = "symbol";
	private static final String PROPERTITY_BUTTON_OPTIONS_SYMBOLFILL = "symbolFill";
	private static final String PROPERTITY_BUTTON_OPTIONS_SYMBOLSIZE = "symbolSize";
	private static final String PROPERTITY_BUTTON_OPTIONS_SYMBOLSTROKE = "symbolStroke";
	private static final String PROPERTITY_BUTTON_OPTIONS_SYMBOLSTROKEWIDTH = "symbolStrokeWidth";
	private static final String PROPERTITY_BUTTON_OPTIONS_SYMBOLX = "symbolX";
	private static final String PROPERTITY_BUTTON_OPTIONS_SYMBOLY = "symbolY";
	private static final String PROPERTITY_BUTTON_OPTIONS_TEXT = "text";
	private static final String PROPERTITY_BUTTON_OPTIONS_THEME = "theme";
	private static final String PROPERTITY_BUTTON_OPTIONS_VERTICALALIGN = "verticalAlign";
	private static final String PROPERTITY_BUTTON_OPTIONS_WIDTH = "width";
	private static final String PROPERTITY_BUTTON_OPTIONS_X = "x";
	private static final String PROPERTITY_BUTTON_OPTIONS_Y = "y";

	private static final String PROPERTITY_NAVIGATION_MENUITEMHOVERSTYLE = "menuItemHoverStyle";
	private static final String PROPERTITY_NAVIGATION_MENUITEMSTYLE = "menuItemStyle";
	private static final String PROPERTITY_NAVIGATION_MENUSTYLE = "menuStyle";

	private static final String PROPERTITY_AXIS_X = "xAxis";
	private static final String PROPERTITY_AXIS_Y = "yAxis";
	private static final String PROPERTITY_AXIS_ALLOWDECIMALS = "allowDecimals";
	private static final String PROPERTITY_AXIS_ALTERNATEGRIDCOLOR = "alternateGridColor";
	private static final String PROPERTITY_AXIS_CATEGORIES = "categories";

	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS = "dateTimeLabelFormats";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_MILLISECOND = "millisecond";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_SECOND = "second";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_MINUTE = "minute";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_HOUR = "hour";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_DAY = "day";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_WEEK = "week";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_MONTH = "month";
	private static final String PROPERTITY_AXIS_DATETIMELABELFORMATS_YEAR = "year";

	private static final String PROPERTITY_AXIS_ENDONTICK = "endOnTick";
	private static final String PROPERTITY_AXIS_GRIDLINECOLOR = "gridLineColor";
	private static final String PROPERTITY_AXIS_GRIDLINEDASHSTYLE = "gridLineDashStyle";
	private static final String PROPERTITY_AXIS_GRIDLINEINTERPOLATION = "gridLineInterpolation";
	private static final String PROPERTITY_AXIS_GRIDLINEWIDTH = "gridLineWidth";
	private static final String PROPERTITY_AXIS_ID = "id";
	private static final String PROPERTITY_AXIS_LINECOLOR = "lineColor";
	private static final String PROPERTITY_AXIS_LINEWIDTH = "lineWidth";
	private static final String PROPERTITY_AXIS_LINKEDTO = "linkedTo";
	private static final String PROPERTITY_AXIS_MAX = "max";
	private static final String PROPERTITY_AXIS_MAXPADDING = "maxPadding";
	private static final String PROPERTITY_AXIS_MIN = "min";
	private static final String PROPERTITY_AXIS_MINTICKINTERVAL = "minTickInterval";
	private static final String PROPERTITY_AXIS_MINORGRIDLINECOLOR = "minorGridLineColor";
	private static final String PROPERTITY_AXIS_MINORGRIDLINEDASHSTYLE = "minorGridLineDashStyle";
	private static final String PROPERTITY_AXIS_MINORGRIDLINEWIDTH = "minorGridLineWidth";
	private static final String PROPERTITY_AXIS_MINORTICKCOLOR = "minorTickColor";
	private static final String PROPERTITY_AXIS_MINORTICKINTERVAL = "minorTickInterval";
	private static final String PROPERTITY_AXIS_MINORTICKLENGTH = "minorTickLength";
	private static final String PROPERTITY_AXIS_MINORTICKPOSITION = "minorTickPosition";
	private static final String PROPERTITY_AXIS_MINORTICKWIDTH = "minorTickWidth";
	private static final String PROPERTITY_AXIS_MINPADDING = "minPadding";
	private static final String PROPERTITY_AXIS_MINRANGE = "minRange";
	private static final String PROPERTITY_AXIS_OFFSET = "offset";
	private static final String PROPERTITY_AXIS_OPPOSITE = "opposite";

	private static final String PROPERTITY_AXIS_PLOTBANDS = "plotBands";
	private static final String PROPERTITY_AXIS_PLOTBANDS_COLOR = "color";
	private static final String PROPERTITY_AXIS_PLOTBANDS_FROM = "from";
	private static final String PROPERTITY_AXIS_PLOTBANDS_ID = "id";
	private static final String PROPERTITY_AXIS_PLOTBANDS_INNERRADIUS = "innerRadius";
	private static final String PROPERTITY_AXIS_PLOTBANDS_OUTERRADIUS = "outerRadius";
	private static final String PROPERTITY_AXIS_PLOTBANDS_THICKNESS = "thickness";
	private static final String PROPERTITY_AXIS_PLOTBANDS_TO = "to";
	private static final String PROPERTITY_AXIS_PLOTBANDS_ZINDEX = "zIndex";

	private static final String PROPERTITY_AXIS_PLOT_LABEL = "plotLabel";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_ALIGN = "align";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_TEXT = "text";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_VERTICALALIGN = "verticalAlign";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_RATATION = "rotation";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_STYLE = "style";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_TEXTALIGN = "textAlign";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_X = "x";
	private static final String PROPERTITY_AXIS_PLOT_LABEL_Y = "y";

	private static final String PROPERTITY_AXIS_PLOTLINES = "plotLines";
	private static final String PROPERTITY_AXIS_PLOTLINES_COLOR = "color";
	private static final String PROPERTITY_AXIS_PLOTLINES_ID = "id";
	private static final String PROPERTITY_AXIS_PLOTLINES_VALUE = "value";
	private static final String PROPERTITY_AXIS_PLOTLINES_WIDTH = "width";
	private static final String PROPERTITY_AXIS_PLOTLINES_ZINDEX = "zIndex";

	private static final String PROPERTITY_AXIS_REVERSED = "reversed";
	private static final String PROPERTITY_AXIS_SHOWEMPTY = "showEmpty";
	private static final String PROPERTITY_AXIS_SHOWFIRSTLABEL = "showFirstLabel";
	private static final String PROPERTITY_AXIS_SHOWLASTLABEL = "showLastLabel";

	private static final String PROPERTITY_AXIS_STACKLABELS = "stackLabels";
	private static final String PROPERTITY_AXIS_STACKLABELS_ALIGN = "align";
	private static final String PROPERTITY_AXIS_STACKLABELS_TEXTALIGN = "textAlign";
	private static final String PROPERTITY_AXIS_STACKLABELS_VERTICALALIGH = "verticalAlign";
	private static final String PROPERTITY_AXIS_STACKLABELS_ENABLED = "enabled";
	private static final String PROPERTITY_AXIS_STACKLABELS_FORMATTER = "formatter";
	private static final String PROPERTITY_AXIS_STACKLABELS_STYLE = "style";
	private static final String PROPERTITY_AXIS_STACKLABELS_ROTATION = "rotation";
	private static final String PROPERTITY_AXIS_STACKLABELS_X = "x";
	private static final String PROPERTITY_AXIS_STACKLABELS_Y = "y";

	private static final String PROPERTITY_AXIS_STARTOFWEEK = "startOfWeek";
	private static final String PROPERTITY_AXIS_STARTONTICK = "startOnTick";
	private static final String PROPERTITY_AXIS_TICKCOLOR = "tickColor";
	private static final String PROPERTITY_AXIS_TICKINTERVAL = "tickInterval";
	private static final String PROPERTITY_AXIS_TICKLENGTH = "tickLength";
	private static final String PROPERTITY_AXIS_TICKMARKPLACEMENT = "tickmarkPlacement";
	private static final String PROPERTITY_AXIS_TICKPIXELINTERVAL = "tickPixelInterval";
	private static final String PROPERTITY_AXIS_TICKPOSITION = "tickPosition";
	private static final String PROPERTITY_AXIS_TICKPOSITIER = "tickPositier";
	private static final String PROPERTITY_AXIS_TICKPOSITIONS = "tickPositions";
	private static final String PROPERTITY_AXIS_TICKWIDTH = "tickWidth";

	private static final String PROPERTITY_AXIS_TITLE = "title";
	private static final String PROPERTITY_AXIS_TITLE_ALIGN = "align";
	private static final String PROPERTITY_AXIS_TITLE_MARGIN = "margin";
	private static final String PROPERTITY_AXIS_TITLE_OFFSET = "offset";
	private static final String PROPERTITY_AXIS_TITLE_ROTATION = "rotation";
	private static final String PROPERTITY_AXIS_TITLE_STYLE = "style";
	private static final String PROPERTITY_AXIS_TITLE_TEXT = "text";
	private static final String PROPERTITY_AXIS_TITLE_X = "x";
	private static final String PROPERTITY_AXIS_TITLE_Y = "y";

	private static final String PROPERTITY_AXIS_TYPE = "type";

	private static final String PROPERTITY_AXIS_LABELS = "labels";
	private static final String PROPERTITY_AXIS_LABELS_ALIGN = "align";
	private static final String PROPERTITY_AXIS_LABELS_DISTANCE = "distance";
	private static final String PROPERTITY_AXIS_LABELS_ENABLED = "enabled";
	private static final String PROPERTITY_AXIS_LABELS_FORMAT = "format";
	private static final String PROPERTITY_AXIS_LABELS_FORMATTER = "formatter";
	private static final String PROPERTITY_AXIS_LABELS_MAXSTAGGERLINES = "maxStaggerLines";
	private static final String PROPERTITY_AXIS_LABELS_OVERFLOW = "overflow";
	private static final String PROPERTITY_AXIS_LABELS_ROTATION = "rotation";
	private static final String PROPERTITY_AXIS_LABELS_STAGGERLINES = "staggerLines";
	private static final String PROPERTITY_AXIS_LABELS_STEP = "step";
	private static final String PROPERTITY_AXIS_LABELS_STYLE = "style";
	private static final String PROPERTITY_AXIS_LABELS_USEHTML = "useHTML";
	private static final String PROPERTITY_AXIS_LABELS_X = "x";
	private static final String PROPERTITY_AXIS_LABELS_Y = "y";

	private static final String PROPERTITY_AXIS_NAME = "name";
	private static final String PROPERTITY_AXIS_BINDTARGET = "bindtarget";
	private static final String PROPERTITY_AXIS_DATEFORMAT = "dateFormat";
	private static final String PROPERTITY_AXIS_STARTANGLE = "startAngle";
	private static final String PROPERTITY_AXIS_ENDANGLE = "endAngle";

	private static final String PROPERTITY_PANE = "pane";
	private static final String PROPERTITY_PANE_BACKGROUNDS = "backgrounds";
	private static final String PROPERTITY_PANE_BACKGROUND = "background";
	private static final String PROPERTITY_PANE_BACKGROUND_BACKGROUNDCOLOR = "backgroundColor";
	private static final String PROPERTITY_PANE_BACKGROUND_BORDERWIDTH = "borderWidth";
	private static final String PROPERTITY_PANE_BACKGROUND_OUTERRADIUS = "outerRadius";
	private static final String PROPERTITY_PANE_BACKGROUND_INNERRADIUS = "innerRadius";
	private static final String PROPERTITY_PANE_CENTER = "center";
	private static final String PROPERTITY_PANE_ENDANGLE = "endAngle";
	private static final String PROPERTITY_PANE_STARTANGLE = "startAngle";
	private static final String PROPERTITY_PANE_SIZE = "size";

	private static final String PROPERTITY_PLOTOPTIONS = "plotOptions";

	private static final String PROPERTITY_PLOTOPTIONS_ALLOWPOINTSELECT = "allowPointSelect";
	private static final String PROPERTITY_PLOTOPTIONS_ANIMATION = "animation";
	private static final String PROPERTITY_PLOTOPTIONS_BORDERCOLOR = "borderColor";
	private static final String PROPERTITY_PLOTOPTIONS_BORDERRADIUS = "borderRadius";
	private static final String PROPERTITY_PLOTOPTIONS_BORDERWIDTH = "borderWidth";
	private static final String PROPERTITY_PLOTOPTIONS_CENTER = "center";
	private static final String PROPERTITY_PLOTOPTIONS_COLOR = "color";
	private static final String PROPERTITY_PLOTOPTIONS_COLORBYPOINT = "colorByPoint";
	private static final String PROPERTITY_PLOTOPTIONS_COLORS = "colors";
	private static final String PROPERTITY_PLOTOPTIONS_CONNECTENDS = "connectEnds";
	private static final String PROPERTITY_PLOTOPTIONS_CONNECTNULLS = "connectNulls";
	private static final String PROPERTITY_PLOTOPTIONS_CROPTHRESHOLD = "cropThreshold";
	private static final String PROPERTITY_PLOTOPTIONS_CURSOR = "cursor";
	private static final String PROPERTITY_PLOTOPTIONS_DASHSTYLE = "dashStyle";

	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS = "dataLabels";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_ALIGN = "align";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_COLOR = "color";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_BACKGROUND_COLOR = "backgroundColor";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_BORDER_COLOR = "borderColor";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_BORDER_RADIUS = "borderRadius";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_BORDER_WIDTH = "borderWidth";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_CROP = "crop";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_INSIDE = "inside";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_ENABLED = "enabled";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_FORMATTER = "formatter";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_PADDING = "padding";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_ROTATION = "rotation";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_SHADOW = "shadow";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_STYLE = "style";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_USEHTML = "useHTML";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_X = "x";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_Y = "y";
	private static final String PROPERTITY_PLOTOPTIONS_DATALABELS_ZINDEX = "zIndex";

	private static final String PROPERTITY_PLOTOPTIONS_DIAL = "dial";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_BACKGROUNDCOLOR = "backgroundColor";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_BASELENGTH = "baseLength";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_BASEWIDTH = "baseWidth";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_BORDERCOLOR = "borderColor";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_BORDERWIDTH = "borderWidth";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_RADIUS = "radius";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_REARLENGTH = "rearLength";
	private static final String PROPERTITY_PLOTOPTIONS_DIAL_TOPWIDTH = "topWidth";

	private static final String PROPERTITY_PLOTOPTIONS_DISPLAYNEGATIVE = "displayNegative";
	private static final String PROPERTITY_PLOTOPTIONS_ENABLEMOUSETRACKING = "enableMouseTracking";
	private static final String PROPERTITY_PLOTOPTIONS_ENDANGLE = "endAngle";
	private static final String PROPERTITY_PLOTOPTIONS_FILLCOLOR = "fillColor";
	private static final String PROPERTITY_PLOTOPTIONS_FILLOPACITY = "fillOpacity";
	private static final String PROPERTITY_PLOTOPTIONS_GROUPPADDING = "groupPadding";
	private static final String PROPERTITY_PLOTOPTIONS_GROUPING = "grouping";
	private static final String PROPERTITY_PLOTOPTIONS_HEIGHT = "height";
	private static final String PROPERTITY_PLOTOPTIONS_ID = "id";
	private static final String PROPERTITY_PLOTOPTIONS_IGNOREHIDDENPOINT = "ignoreHiddenPoint";
	private static final String PROPERTITY_PLOTOPTIONS_INNERSIZE = "innerSize";
	private static final String PROPERTITY_PLOTOPTIONS_LINEWIDTH = "lineWidth";
	private static final String PROPERTITY_PLOTOPTIONS_LINECOLOR = "lineColor";
	private static final String PROPERTITY_PLOTOPTIONS_LINKEDTO = "linkedTo";

	private static final String PROPERTITY_PLOTOPTIONS_MARKER = "marker";
	private static final String PROPERTITY_PLOTOPTIONS_MARKER_ENABLED = "enabled";
	private static final String PROPERTITY_PLOTOPTIONS_MARKER_FILLCOLOR = "fillColor";
	private static final String PROPERTITY_PLOTOPTIONS_MARKER_LINECOLOR = "lineColor";
	private static final String PROPERTITY_PLOTOPTIONS_MARKER_LINEWIDTH = "lineWidth";
	private static final String PROPERTITY_PLOTOPTIONS_MARKER_RADIUS = "radius";
	private static final String PROPERTITY_PLOTOPTIONS_MARKER_SYMBOL = "symbol";

	private static final String PROPERTITY_PLOTOPTIONS_MAXSIZE = "maxSize";
	private static final String PROPERTITY_PLOTOPTIONS_MEDIANCOLOR = "medianColor";
	private static final String PROPERTITY_PLOTOPTIONS_MEDIANWIDTH = "medianWidth";
	private static final String PROPERTITY_PLOTOPTIONS_MINPOINTLENGTH = "minPointLength";
	private static final String PROPERTITY_PLOTOPTIONS_MINSIZE = "minSize";
	private static final String PROPERTITY_PLOTOPTIONS_NECKHEIGHT = "neckHeight";
	private static final String PROPERTITY_PLOTOPTIONS_NECKWIDTH = "neckWidth";
	private static final String PROPERTITY_PLOTOPTIONS_NEGATIVECOLOR = "negativeColor";
	private static final String PROPERTITY_PLOTOPTIONS_NEGATIVEFILLCOLOR = "negativeFillColor";

	private static final String PROPERTITY_PLOTOPTIONS_PIVOT = "pivot";
	private static final String PROPERTITY_PLOTOPTIONS_PIVOT_BACKGROUNDCOLOR = "backgroundColor";
	private static final String PROPERTITY_PLOTOPTIONS_PIVOT_BORDERCOLOR = "borderColor";
	private static final String PROPERTITY_PLOTOPTIONS_PIVOT_BORDERWIDTH = "borderWidth";
	private static final String PROPERTITY_PLOTOPTIONS_PIVOT_RADIUS = "radius";

	private static final String PROPERTITY_PLOTOPTIONS_POINT = "point";
	private static final String PROPERTITY_PLOTOPTIONS_POINTINTERVAL = "pointInterval";
	private static final String PROPERTITY_PLOTOPTIONS_POINTPADDING = "pointPadding";
	private static final String PROPERTITY_PLOTOPTIONS_POINTPLACEMENT = "pointPlacement";
	private static final String PROPERTITY_PLOTOPTIONS_POINTRANGE = "pointRange";
	private static final String PROPERTITY_PLOTOPTIONS_POINTSTART = "pointStart";
	private static final String PROPERTITY_PLOTOPTIONS_POINTWIDTH = "pointWidth";
	private static final String PROPERTITY_PLOTOPTIONS_SELECTED = "selected";
	private static final String PROPERTITY_PLOTOPTIONS_SHADOW = "shadow";
	private static final String PROPERTITY_PLOTOPTIONS_SHOWCHECKBOX = "showCheckbox";
	private static final String PROPERTITY_PLOTOPTIONS_SHOWINLEGEND = "showInLegend";
	private static final String PROPERTITY_PLOTOPTIONS_SIZE = "size";
	private static final String PROPERTITY_PLOTOPTIONS_SLICEDOFFSET = "slicedOffset";
	private static final String PROPERTITY_PLOTOPTIONS_STACKING = "stacking";
	private static final String PROPERTITY_PLOTOPTIONS_STARTANGLE = "startAngle";
	private static final String PROPERTITY_PLOTOPTIONS_STEMCOLOR = "stemColor";
	private static final String PROPERTITY_PLOTOPTIONS_STEMDASHSTYLE = "stemDashStyle";
	private static final String PROPERTITY_PLOTOPTIONS_STEMWIDTH = "stemWidth";

	private static final String PROPERTITY_PLOTOPTIONS_STATES = "states";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_HOVER = "hover";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_SELECT = "select";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_ENABLED = "enabled";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_FILLCOLOR = "fillColor";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_LINECOLOR = "lineColor";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_LINEWIDTH = "lineWidth";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_RADIUS = "radius";
	private static final String PROPERTITY_PLOTOPTIONS_STATES_HOVER_BRIGHTNESS = "brightness";

	private static final String PROPERTITY_PLOTOPTIONS_STEP = "step";
	private static final String PROPERTITY_PLOTOPTIONS_STICKYTRACKING = "stickyTracking";
	private static final String PROPERTITY_PLOTOPTIONS_THRESHOLD = "threshold";

	private static final String PROPERTITY_PLOTOPTIONS_TOOLTIP = "plotTooltip";

	private static final String PROPERTITY_PLOTOPTIONS_TRACKBYAREA = "trackByArea";
	private static final String PROPERTITY_PLOTOPTIONS_TURBOTHRESHOLD = "turboThreshold";
	private static final String PROPERTITY_PLOTOPTIONS_VISIBLE = "visible";
	private static final String PROPERTITY_PLOTOPTIONS_WHISKERCOLOR = "whiskerColor";
	private static final String PROPERTITY_PLOTOPTIONS_WHISKERLENGTH = "whiskerLength";
	private static final String PROPERTITY_PLOTOPTIONS_WHISKERWIDTH = "whiskerWidth";
	private static final String PROPERTITY_PLOTOPTIONS_WIDTH = "width";
	private static final String PROPERTITY_PLOTOPTIONS_WRAP = "wrap";
	private static final String PROPERTITY_PLOTOPTIONS_ZINDEX = "zIndex";
	private static final String PROPERTITY_PLOTOPTIONS_ZTHRESHOLD = "zThreshold";

	private static final String PROPERTITY_SERIESLIST = "seriesList";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM = "seriesItem";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATAS = "seriesDatas";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA = "seriesData";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_COLOR = "color";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_DRILLDOWN = "drilldown";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_LEGENDINDEX = "legendIndex";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_NAME = "name";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_SLICED = "sliced";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_X = "x";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_Y = "y";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_INDEX = "index";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_LEGENDINDEX = "legendIndex";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_NAME = "name";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_NEGATIVE = "negative";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_STACK = "stack";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_TYPE = "type";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_XAXIS = "xAxis";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_YAXIS = "yAxis";
	private static final String PROPERTITY_SERIESLIST_SERIESITEM_ZINDEX = "zIndex";

	private static final String PROPERTITY_COLORS = "colors";

	private CompositeMap model;

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		String theme = view.getString(PROPERTITY_THEME.toLowerCase());
		if (theme == null)
			theme = "grid";
		addJavaScript(session, context, "chart/Animate-min.js");
		addJavaScript(session, context, "chart/Chart-min.js");
		if (needMore(view, model)) {
			addJavaScript(session, context, "chart/Chart-more-min.js");
		}
		if(!"default".equals(theme)){
			addJavaScript(session, context, "chart/themes/" + theme + ".js");
		}
		addJavaScript(session, context, "chart/Exporting-min.js");
	}

	private static String[] CHART_MORE_TYPE = new String[] { "gauge",
			"arearange", "columnrange", "areasplinerange", "boxplot",
			"errorbar", "waterfall", "bubble" };
	private boolean containsMore(String type){
		for(int i = 0;i<CHART_MORE_TYPE.length;i++){
			if(CHART_MORE_TYPE[i].equals(type)){
				return true;
			}
		}
		return false;
	}
	private boolean needMore(CompositeMap view, CompositeMap model) {
		Iterator childs = view.getChildIterator();
		if (null != childs) {
			while (childs.hasNext()) {
				CompositeMap child = (CompositeMap) childs.next();
				if ("chart".equals(child.getName())) {
					if (child.getBoolean(PROPERTITY_CHART_POLAR, false)
							|| containsMore(TextParser.parse(
									child.getString(PROPERTITY_CHART_TYPE),
									model)))
						return true;
				} else {
					if (needMore(child, model))
						return true;
				}
			}
		}
		return false;
	}

	protected void addEvent(String id, String eventName, String handler) {
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		model = context.getModel();

		String bindTarget = view
				.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		if (null != bindTarget && !"".equals(bindTarget)) {
			bindTarget = uncertain.composite.TextParser
					.parse(bindTarget, model);
			map.put(BINDING, new String("$('" + id + "').bind('" + bindTarget
					+ "');\n"));
		}

		String value = view.getString(PROPERTITY_SERIES_NAME.toLowerCase());
		if (value == null)
			value = "name";
		addConfig(PROPERTITY_SERIES_NAME, value);
		map.put("contextPath", model.getObject("/request/@context_path")
				.toString());

		processChartConfig(context, bindTarget);

		JSONObject config = getConfig();

		config.remove(ComponentConfig.PROPERTITY_HEIGHT);
		config.remove(ComponentConfig.PROPERTITY_WIDTH);
		config.remove("listeners");
		map.put(CONFIG, getConfigString());
	}

	private void putStringCfg(CompositeMap view, String key, Map map) {
		String value = view.getString(key.toLowerCase());
		if (null != value) {
			if ("null".equals(value))
				map.put(key, null);
			else if (value.matches("Aurora\\..*|\\$A\\..*|window[\\.\\[].*"))
				map.put(key, new JSONFunction(value));
			else
				map.put(key, value);
		}
	}

	private void putStringCfg(CompositeMap view, String key, Map map,
			CompositeMap model) {
		String value = view.getString(key.toLowerCase());
		if (null != value) {
			value = TextParser.parse(value, model);
			if ("null".equals(value))
				map.put(key, null);
			else if (value.matches("Aurora\\..*|\\$A\\..*|window[\\.\\[].*"))
				map.put(key, new JSONFunction(value));
			else
				map.put(key, value);
		}
	}

	private void putNumberCfg(CompositeMap view, String key, Map map) {
		String valuestr = view.getString(key.toLowerCase());
		if (null != valuestr) {
			if ("auto".equals(valuestr) || valuestr.indexOf("%") != -1) {
				map.put(key, valuestr);
				return;
			}
			try {
				if (valuestr.indexOf(".") != -1) {
					Double value = view.getDouble(key.toLowerCase());
					if (null != value)
						map.put(key, value);
				} else {
					Long value = view.getLong(key.toLowerCase());
					if (null != value)
						map.put(key, value);
				}
			} catch (NumberFormatException e) {
				map.put(key, new JSONFunction(valuestr));
			}
		}
	}

	private void putBooleanCfg(CompositeMap view, String key, Map map) {
		Boolean value = view.getBoolean(key.toLowerCase());
		if (null != value)
			map.put(key, value);
	}

	private void putFunctionCfg(CompositeMap view, String key, Map map) {
		String value = view.getString(key.toLowerCase());
		if (null != value)
			map.put(key, new JSONFunction(value));
	}

	private void putGradientCfg(CompositeMap view, String key, Map map) {
		String value = view.getString(key.toLowerCase());
		if (null != value) {
			String[] values = value.split(",");
			map.put(key,
					new JSONFunction("{x1:" + values[0] + ",y1:" + values[1]
							+ ",x2:" + values[2] + ",y2:" + values[3] + "}"));
		}
	}

	private void putArrayCfg(CompositeMap view, String key, Map map) {
		String value = view.getString(key.toLowerCase());
		if (null != value) {
			map.put(key, new JSONFunction("[" + value + "]"));
		}
	}

	private void putStyleCfg(CompositeMap view, String key, Map map) {
		String value = view.getString(key.toLowerCase());
		if (null != value) {
			JSONObject smap = new JSONObject();
			String[] sts = value.split(";");
			for (int i = 0; i < sts.length; i++) {
				String style = sts[i];
				if (!"".equals(style) && style.indexOf(":") != -1) {
					String[] vs = style.trim().split(":");
					String k = vs[0];
					String v = vs[1];
					v = v.replaceAll("'", "");
					String[] ks = k.split("-");
					k = "";
					for (int j = 0; j < ks.length; j++) {
						ks[j] = ks[j].toLowerCase();
						if (j > 0) {
							String e = ks[j];
							ks[j] = "" + (char) (e.charAt(0) - 32)
									+ e.substring(1);
						}
						k += ks[j];
					}

					Long lv = null;
					Double dv = null;
					try {
						dv = Double.valueOf(v);
						lv = Long.valueOf(v);
					} catch (Exception e) {
					}
					try {
						if (null != lv) {
							smap.put(k, lv);
						} else if (null != dv) {
							smap.put(k, dv);
						} else {
							smap.put(k, v);
						}
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}
			}
			map.put("style", smap);
		}
	}

	private void putGradientColorCfg(CompositeMap parent, String key, Map map) {
		Map cfg = new HashMap();
		CompositeMap view = parent.getChild(key);
		if (null != view) {
			putGradientCfg(view, "linearGradient", cfg);
			putArrayCfg(view, "stops", cfg);
			if (!cfg.isEmpty())
				map.put(key, new JSONObject(cfg));
		} else {
			putStringCfg(parent, key, map);
		}
	}

	private void putColorCfg(CompositeMap parent, String key, Map map) {
		Map cfg = new HashMap();
		CompositeMap view = parent.getChild(key);
		if (null != view) {
			putArrayCfg(view, "linearGradient", cfg);
			putArrayCfg(view, "stops", cfg);
			if (!cfg.isEmpty())
				map.put(key, new JSONObject(cfg));
		} else {
			putStringCfg(parent, key, map);
		}
	}

	private void putEvents(CompositeMap view, Map map) {
		CompositeMap events = view.getChild(ComponentConfig.PROPERTITY_EVENTS);
		if (null != events) {
			List list = events.getChilds();
			if (null != list) {
				JSONObject eo = new JSONObject();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					CompositeMap event = (CompositeMap) it.next();
					EventConfig eventConfig = EventConfig.getInstance(event);
					String eventName = eventConfig.getEventName();
					String handler = eventConfig.getHandler();
					if (!"".equals(eventName) && !"".equals(handler))
						try {
							eo.put(eventName, new JSONFunction(handler));
						} catch (JSONException e) {
							throw new RuntimeException(e);
						}
				}
				map.put(ComponentConfig.PROPERTITY_EVENTS, eo);
			}
		}
	}

	private void processChartConfig(ViewContext context, String bindTarget) {
		CompositeMap view = context.getView();
		Map chart = new HashMap();
		Map map = context.getMap();

		chart.put(PROPERTITY_CHART_RENDERTO,
				(String) map.get(ComponentConfig.PROPERTITY_ID) + "_c");

		createChartOption(view, chart);

		addConfig("chart", new JSONObject(chart));

		processColors(view);
		processCredits(view);
		processExporting(view);
		processLabels(view);
		processLegend(view);
		processLoading(view);
		processNavigation(view);
		processPane(view);
		processPlotOptions(view);
		processSeries(view, bindTarget);
		processSubTitle(view);
		processTitle(view);
		processTooltip(view);
		processXAxis(view);
		processYAxis(view);
	}

	private void createChartOption(CompositeMap parent, Map cfg) {
		putStringCfg(parent, PROPERTITY_CHART_TYPE, cfg, model);
		putBooleanCfg(parent, PROPERTITY_CHART_POLAR, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_ALIGNTICKS, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_ANIMATION, cfg);
		putColorCfg(parent, PROPERTITY_CHART_BACKGROUNDCOLOR, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_BORDERRADIUS, cfg);
		putStringCfg(parent, PROPERTITY_CHART_BORDERCOLOR, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_BORDERWIDTH, cfg);
		putStringCfg(parent, PROPERTITY_CHART_CLASSNAME, cfg);
		putNumberCfg(parent, ComponentConfig.PROPERTITY_HEIGHT, cfg);
		putStringCfg(parent, PROPERTITY_CHART_DEFAULTSERIESTYPE, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_IGNORE_HIDDEN_SERIES, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_INVERTED, cfg);
		putArrayCfg(parent, PROPERTITY_CHART_MARGIN, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_MARGIN_TOP, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_MARGIN_RIGHT, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_MARGIN_LEFT, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_MARGIN_BOTTOM, cfg);
		putStringCfg(parent, PROPERTITY_CHART_PINCH_TYPE, cfg);
		putColorCfg(parent, PROPERTITY_CHART_PLOT_BACKGROUND_COLOR, cfg);
		putStringCfg(parent, PROPERTITY_CHART_PLOT_BACKGROUND_IMAGE, cfg);
		putStringCfg(parent, PROPERTITY_CHART_PLOT_BORDER_COLOR, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_PLOT_BORDER_WIDTH, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_PLOT_SHADOW, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_REFLOW, cfg);
		processResetZoomButton(parent, cfg);
		putStringCfg(parent, PROPERTITY_CHART_SELECTIONMARKERFILL, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_SHADOW, cfg);
		putBooleanCfg(parent, PROPERTITY_CHART_SHOW_AXES, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_SPACING_TOP, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_SPACING_RIGHT, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_SPACING_BOTTOM, cfg);
		putNumberCfg(parent, PROPERTITY_CHART_SPACING_LEFT, cfg);
		putStyleCfg(parent, PROPERTITY_CHART_STYLE, cfg);
		putNumberCfg(parent, ComponentConfig.PROPERTITY_WIDTH, cfg);
		putStringCfg(parent, PROPERTITY_CHART_ZOOMTYPE, cfg);

		putStringCfg(parent, PROPERTITY_CHART_NAME_FIELD, cfg);
		putStringCfg(parent, PROPERTITY_CHART_VALUE_FIELD, cfg);
		putStringCfg(parent, PROPERTITY_CHART_GROUPBY, cfg);
		putEvents(parent, cfg);
	}

	private void processColors(CompositeMap parent) {
		Map cfg = new HashMap();
		putArrayCfg(parent, PROPERTITY_COLORS, cfg);
		if (!cfg.isEmpty())
			addConfig(PROPERTITY_COLORS, cfg.get(PROPERTITY_COLORS));
	}

	private void processResetZoomButton(CompositeMap cview, Map map) {
		CompositeMap view = cview.getChild(PROPERTITY_CHART_RESETZOOMBUTTON);
		Map cfg = new HashMap();
		if (view != null) {
			createPosition(view, cfg);
			putStringCfg(view, PROPERTITY_CHART_RESETZOOMBUTTON_RELATIVETO, cfg);
			putStringCfg(view, PROPERTITY_CHART_RESETZOOMBUTTON_THEME, cfg);
		}
		if (!cfg.isEmpty())
			map.put(PROPERTITY_CHART_RESETZOOMBUTTON, new JSONObject(cfg));
	}

	private void processCredits(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_CREDITS);
		if (null != view) {
			Map cfg = new HashMap();
			putBooleanCfg(view, PROPERTITY_CREDITS_ENABLED, cfg);
			createPosition(view, cfg);
			putStringCfg(view, PROPERTITY_CREDITS_HREF, cfg);
			putStyleCfg(view, PROPERTITY_CREDITS_STYLE, cfg);
			putStringCfg(view, PROPERTITY_CREDITS_TEXT, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_CREDITS, new JSONObject(cfg));
		}
	}

	private void createPosition(CompositeMap parent, Map map) {
		CompositeMap view = parent.getChild(PROPERTITY_POSITION);
		if (null != view) {
			Map cfg = new HashMap();
			putStringCfg(view, PROPERTITY_POSITION_ALIGN, cfg);
			putNumberCfg(view, PROPERTITY_POSITION_X, cfg);
			putStringCfg(view, PROPERTITY_POSITION_VERTICALALIGN, cfg);
			putNumberCfg(view, PROPERTITY_POSITION_Y, cfg);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_POSITION, new JSONObject(cfg));
		}
	}

	private void processExporting(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_EXPORTING);
		if (null != view) {
			Map cfg = new HashMap();
			createExportingButtons(view, cfg);
			CompositeMap chartOptions = view
					.getChild(PROPERTITY_EXPORTING_CHARTOPTIONS);
			if (null != chartOptions) {
				Map cfg2 = new HashMap();
				createChartOption(chartOptions, cfg2);
				if (!cfg.isEmpty())
					cfg.put(PROPERTITY_EXPORTING_CHARTOPTIONS, new JSONObject(
							cfg2));
			}
			putBooleanCfg(view, PROPERTITY_EXPORTING_ENABLED, cfg);
			putStringCfg(view, PROPERTITY_EXPORTING_FILENAME, cfg);
			putNumberCfg(view, PROPERTITY_EXPORTING_SCALE, cfg);
			putNumberCfg(view, PROPERTITY_EXPORTING_SOURCEHEIGHT, cfg);
			putNumberCfg(view, PROPERTITY_EXPORTING_SOURCEWIDTH, cfg);
			putStringCfg(view, PROPERTITY_EXPORTING_TYPE, cfg);
			putStringCfg(view, PROPERTITY_EXPORTING_URL, cfg);
			putNumberCfg(view, PROPERTITY_EXPORTING_WIDTH, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_EXPORTING, new JSONObject(cfg));
		}
	}

	private void createExportingButtons(CompositeMap cview, Map map) {
		CompositeMap view = cview.getChild(PROPERTITY_EXPORTING_BUTTONS);
		if (null != view) {
			Map cfg = new HashMap();
			createButtonOptions(view, cfg,
					PROPERTITY_EXPORTING_BUTTONS_CONTEXTBUTTON);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_EXPORTING_BUTTONS, new JSONObject(cfg));
		}
	}

	private void createButtonOptions(CompositeMap cview, Map map, String name) {
		CompositeMap view = cview.getChild(name);
		if (null != view) {
			Map cfg = new HashMap();
			putStringCfg(view, PROPERTITY_BUTTON_OPTIONS_ALIGN, cfg);
			putBooleanCfg(view, PROPERTITY_BUTTON_OPTIONS_ENABLED, cfg);
			putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_HEIGHT, cfg);
			putStringCfg(view, PROPERTITY_BUTTON_OPTIONS_SYMBOL, cfg, model);
			putStringCfg(view, PROPERTITY_BUTTON_OPTIONS_SYMBOLFILL, cfg);
			putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_SYMBOLSIZE, cfg);
			putStringCfg(view, PROPERTITY_BUTTON_OPTIONS_SYMBOLSTROKE, cfg);
			putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_SYMBOLSTROKEWIDTH, cfg);
			putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_SYMBOLX, cfg);
			putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_SYMBOLY, cfg);
			putStringCfg(view, PROPERTITY_BUTTON_OPTIONS_TEXT, cfg);
			putStringCfg(view, PROPERTITY_BUTTON_OPTIONS_THEME, cfg);
			putStringCfg(view, PROPERTITY_BUTTON_OPTIONS_VERTICALALIGN, cfg);
			putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_WIDTH, cfg);
			putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_Y, cfg);
			if (PROPERTITY_EXPORTING_BUTTONS_CONTEXTBUTTON.equals(name)) {
				createMenuItems(view, cfg);
				putFunctionCfg(view, PROPERTITY_BUTTON_OPTIONS_ONCLICK, cfg);
				putNumberCfg(view, PROPERTITY_BUTTON_OPTIONS_X, cfg);
			}
			if (!cfg.isEmpty())
				map.put(name, new JSONObject(cfg));
		}
	}

	private void createMenuItems(CompositeMap view, Map map) {
		CompositeMap pbs = view.getChild(PROPERTITY_BUTTON_OPTIONS_MENUITEMS);
		if (null != pbs) {
			List list = pbs.getChilds();
			if (null != list) {
				JSONArray array = new JSONArray();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap pb = (CompositeMap) it.next();
					putGradientColorCfg(pb,
							PROPERTITY_PANE_BACKGROUND_BACKGROUNDCOLOR, cfg);
					putNumberCfg(pb, PROPERTITY_PANE_BACKGROUND_BORDERWIDTH,
							cfg);
					putStringCfg(pb, PROPERTITY_PANE_BACKGROUND_OUTERRADIUS,
							cfg);
					putStringCfg(pb, PROPERTITY_PANE_BACKGROUND_INNERRADIUS,
							cfg);
					array.put(cfg);
				}
				if (array.length() > 0)
					map.put(PROPERTITY_BUTTON_OPTIONS_MENUITEMS, array);
			}
		}
	}

	private void processLabels(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_LABELS);
		if (null == view)
			view = parent.getChild(PROPERTITY_AXIS_LABELS);
		if (null != view) {
			Map cfg = new HashMap();
			putStyleCfg(view, PROPERTITY_LABELS_STYLE, cfg);
			List list = view.getChilds();
			if (null != list) {
				JSONArray array = new JSONArray();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					CompositeMap label = (CompositeMap) it.next();
					Map icfg = new HashMap();
					putStringCfg(label, PROPERTITY_LABELS_LABEL_HTML, icfg);
					putStyleCfg(label, PROPERTITY_LABELS_LABEL_STYLE, icfg);
					array.put(icfg);
				}
				if (array.length() > 0)
					cfg.put(PROPERTITY_LABELS_ITEMS, array);
			}
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_AXIS_LABELS, new JSONObject(cfg));
		}
	}

	private void processLegend(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_LEGEND);
		if (null != view) {
			Map cfg = new HashMap();
			putStringCfg(view, PROPERTITY_LEGEND_ALIGN, cfg);
			putStringCfg(view, PROPERTITY_LEGEND_BACKGROUNDCOLOR, cfg);
			putStringCfg(view, PROPERTITY_LEGEND_BORDERCOLOR, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_BORDERRADIUS, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_BORDERWIDTH, cfg);
			putBooleanCfg(view, PROPERTITY_LEGEND_ENABLED, cfg);
			putBooleanCfg(view, PROPERTITY_LEGEND_FLOATING, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_ITEMDISTANCE, cfg);
			putStyleCfg(view, PROPERTITY_LEGEND_ITEMHIDDENSTYLE, cfg);
			putStyleCfg(view, PROPERTITY_LEGEND_ITEMHOVERSTYLE, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_ITEMMARGINBOTTOM, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_ITEMMARGINTOP, cfg);
			putStyleCfg(view, PROPERTITY_LEGEND_ITEMSTYLE, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_ITEMWIDTH, cfg);
			putStringCfg(view, PROPERTITY_LEGEND_LABELFORMAT, cfg);
			putFunctionCfg(view, PROPERTITY_LEGEND_LABELFORMATTER, cfg);
			putStringCfg(view, PROPERTITY_LEGEND_LAYOUT, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_LINEHEIGHT, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_MARGIN, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_MAXHEIGHT, cfg);
			createLegendNavigation(view, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_PADDING, cfg);
			putBooleanCfg(view, PROPERTITY_LEGEND_REVERSED, cfg);
			putBooleanCfg(view, PROPERTITY_LEGEND_RTL, cfg);
			putBooleanCfg(view, PROPERTITY_LEGEND_SHADOW, cfg);
			putStyleCfg(view, PROPERTITY_LEGEND_STYLE, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_SYMBOLPADDING, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_SYMBOLWIDTH, cfg);
			createLegendTitle(view, cfg);
			putBooleanCfg(view, PROPERTITY_LEGEND_USEHTML, cfg);
			putStringCfg(view, PROPERTITY_LEGEND_VERTICALALIGN, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_WIDTH, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_X, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_Y, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_LEGEND, new JSONObject(cfg));
		}
	}

	private void createLegendNavigation(CompositeMap cview, Map map) {
		CompositeMap view = cview.getChild(PROPERTITY_LEGEND_NAVIGATION);
		if (null == view)
			view = cview.getChild(PROPERTITY_NAVIGATION);
		if (null != view) {
			Map cfg = new HashMap();
			putStringCfg(view, PROPERTITY_LEGEND_NAVIGATION_ACTIVECOLOR, cfg);
			putBooleanCfg(view, PROPERTITY_LEGEND_NAVIGATION_ANIMATION, cfg);
			putNumberCfg(view, PROPERTITY_LEGEND_NAVIGATION_ARROWSIZE, cfg);
			putStringCfg(view, PROPERTITY_LEGEND_NAVIGATION_INACTIVECOLOR, cfg);
			putStyleCfg(view, PROPERTITY_LEGEND_NAVIGATION_STYLE, cfg);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_NAVIGATION, new JSONObject(cfg));
		}
	}

	private void createLegendTitle(CompositeMap cview, Map map) {
		CompositeMap view = cview.getChild(PROPERTITY_LEGEND_TITLE);
		if (null == view)
			view = cview.getChild(PROPERTITY_TITLE);
		if (null != view) {
			Map cfg = new HashMap();
			putStyleCfg(view, PROPERTITY_TITLE_STYLE, cfg);
			putStringCfg(view, PROPERTITY_TITLE_TEXT, cfg);

			if (!cfg.isEmpty())
				map.put(PROPERTITY_TITLE, new JSONObject(cfg));
		}
	}

	private void processLoading(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_LOADING);

		if (null != view) {
			Map cfg = new HashMap();
			putNumberCfg(view, PROPERTITY_LOADING_HIDEDURATION, cfg);
			putStyleCfg(view, PROPERTITY_LOADING_LABELSTYLE, cfg);
			putNumberCfg(view, PROPERTITY_LOADING_SHOWDURATION, cfg);
			putStyleCfg(view, PROPERTITY_LOADING_STYLE, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_LOADING, new JSONObject(cfg));
		}
	}

	private void processNavigation(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_NAVIGATION);

		if (null != view) {
			Map cfg = new HashMap();
			createButtonOptions(view, cfg, PROPERTITY_BUTTON_OPTIONS);
			putStyleCfg(view, PROPERTITY_NAVIGATION_MENUITEMHOVERSTYLE, cfg);
			putStyleCfg(view, PROPERTITY_NAVIGATION_MENUITEMSTYLE, cfg);
			putStyleCfg(view, PROPERTITY_NAVIGATION_MENUSTYLE, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_NAVIGATION, new JSONObject(cfg));
		}
	}

	private void processPane(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_PANE);
		if (null != view) {
			Map cfg = new HashMap();
			createBackgrounds(view, cfg);
			putArrayCfg(view, PROPERTITY_PANE_CENTER, cfg);
			putNumberCfg(view, PROPERTITY_PANE_ENDANGLE, cfg);
			putNumberCfg(view, PROPERTITY_PANE_STARTANGLE, cfg);
			putNumberCfg(view, PROPERTITY_PANE_SIZE, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_PANE, new JSONObject(cfg));
		}
	}

	private void createBackgrounds(CompositeMap view, Map map) {
		CompositeMap pbs = view.getChild(PROPERTITY_PANE_BACKGROUNDS);
		if (null != pbs) {
			List list = pbs.getChilds();
			if (null != list) {
				JSONArray array = new JSONArray();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap pb = (CompositeMap) it.next();
					if (PROPERTITY_PANE_BACKGROUND.equals(pb.getName())) {
						putGradientColorCfg(pb,
								PROPERTITY_PANE_BACKGROUND_BACKGROUNDCOLOR, cfg);
						putNumberCfg(pb,
								PROPERTITY_PANE_BACKGROUND_BORDERWIDTH, cfg);
						putStringCfg(pb,
								PROPERTITY_PANE_BACKGROUND_OUTERRADIUS, cfg);
						putStringCfg(pb,
								PROPERTITY_PANE_BACKGROUND_INNERRADIUS, cfg);
						array.put(cfg);
					}
				}
				if (array.length() > 0)
					map.put(PROPERTITY_PANE_BACKGROUND, array);
			}
		}
	}

	private void processPlotOptions(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_PLOTOPTIONS);
		if (null != view) {
			Map cfg = new HashMap();
			List children = view.getChilds();
			if (children != null) {
				Iterator it = children.iterator();
				while (it.hasNext()) {
					CompositeMap option = (CompositeMap) it.next();
					String name = option.getName().toLowerCase();
					if ("chartcolumn".equals(name)) {
						name = "column";
					}
					cfg.put(name, createPlotOption(option));
				}

			}
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_PLOTOPTIONS, new JSONObject(cfg));
		}
	}

	private void processSeries(CompositeMap parent, String bindTarget) {
		CompositeMap view = parent.getChild(PROPERTITY_SERIESLIST);
		if (null != view) {
			List children = view.getChilds();
			if (children != null) {
				JSONArray array = new JSONArray();
				Iterator it = children.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap pb = (CompositeMap) it.next();
					if (PROPERTITY_SERIESLIST_SERIESITEM.equals(pb.getName())) {
						createSeriesData(pb, cfg);
						putNumberCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_INDEX, cfg);
						putNumberCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_LEGENDINDEX,
								cfg);
						putStringCfg(pb, PROPERTITY_SERIESLIST_SERIESITEM_NAME,
								cfg);
						putBooleanCfg(pb, PROPERTITY_SERIESLIST_SERIESITEM_NEGATIVE,
								cfg);
						putStringCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_STACK, cfg);
						putStringCfg(pb, PROPERTITY_SERIESLIST_SERIESITEM_TYPE,
								cfg);
						putStringCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_XAXIS, cfg);
						putStringCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_YAXIS, cfg);
						putNumberCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_ZINDEX, cfg);
						createPlotOption(pb, cfg);
						array.put(cfg);
					}
				}
				if (array.length() > 0)
					addConfig(
							null == bindTarget || "".equals(bindTarget) ? "series"
									: PROPERTITY_SERIESLIST, array);
			}
		}
	}

	private void createSeriesData(CompositeMap parent, Map map) {
		CompositeMap view = parent
				.getChild(PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATAS);
		if (null != view) {
			List children = view.getChilds();
			if (children != null) {
				JSONArray array = new JSONArray();
				Iterator it = children.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap pb = (CompositeMap) it.next();
					if (PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA.equals(pb
							.getName())) {
						putColorCfg(
								pb,
								PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_COLOR,
								cfg);
						createPlotOptionDataLabels(pb, cfg);
						putStringCfg(
								pb,
								PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_DRILLDOWN,
								cfg);
						putEvents(pb, cfg);
						putNumberCfg(
								pb,
								PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_LEGENDINDEX,
								cfg);
						createPlotOptionMarker(pb, cfg, true);
						putStringCfg(
								pb,
								PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_NAME,
								cfg);
						putBooleanCfg(
								pb,
								PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_SLICED,
								cfg);
						putNumberCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_X,
								cfg);
						putNumberCfg(pb,
								PROPERTITY_SERIESLIST_SERIESITEM_SERIESDATA_Y,
								cfg);
						String text = pb.getText();
						if (cfg.isEmpty()) {
							if (null == text || "".equals(text)) {
								array.put(text);
							} else {
								try {
									array.put(new Double(text));
								} catch (NumberFormatException e) {
									String[] strs = text.split(",");
									if (strs.length > 1) {
										StringBuffer sb = new StringBuffer("[");
										for (int i = 0; i < strs.length; i++) {
											try {
												sb.append(new Double(strs[i]));
											} catch (NumberFormatException ee) {
												sb.append("\"" + strs[i] + "\"");
											}
											if (i != strs.length - 1)
												sb.append(",");
										}
										sb.append("]");
										array.put(new JSONFunction(sb
												.toString()));
									} else {
										array.put(text);
									}
								}
							}
						} else {
							array.put(cfg);
						}
					}
				}
				if (array.length() > 0)
					map.put("data", array);
			}
		}
	}

	private JSONObject createPlotOption(CompositeMap view) {
		Map cfg = new HashMap();
		return createPlotOption(view, cfg);
	}

	private JSONObject createPlotOption(CompositeMap view, Map cfg) {
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_ALLOWPOINTSELECT, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_ANIMATION, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_BORDERCOLOR, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_BORDERRADIUS, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_BORDERWIDTH, cfg);
		putArrayCfg(view, PROPERTITY_PLOTOPTIONS_CENTER, cfg);
		putColorCfg(view, PROPERTITY_PLOTOPTIONS_COLOR, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_COLORBYPOINT, cfg);
		putArrayCfg(view, PROPERTITY_PLOTOPTIONS_COLORS, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_CONNECTENDS, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_CONNECTNULLS, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_CROPTHRESHOLD, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_CURSOR, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_DASHSTYLE, cfg);
		createPlotOptionDial(view, cfg);
		createPlotOptionDataLabels(view, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_DISPLAYNEGATIVE, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_ENABLEMOUSETRACKING, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_ENDANGLE, cfg);
		putEvents(view, cfg);
		putColorCfg(view, PROPERTITY_PLOTOPTIONS_FILLCOLOR, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_FILLOPACITY, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_GROUPPADDING, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_GROUPING, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_HEIGHT, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_ID, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_IGNOREHIDDENPOINT, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_INNERSIZE, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_LINECOLOR, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_LINEWIDTH, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_LINKEDTO, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_MAXSIZE, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_MEDIANCOLOR, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_MEDIANWIDTH, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_MINPOINTLENGTH, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_MINSIZE, cfg);
		createPlotOptionMarker(view, cfg, true);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_NECKHEIGHT, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_NECKWIDTH, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_NEGATIVECOLOR, cfg);
		putColorCfg(view, PROPERTITY_PLOTOPTIONS_NEGATIVEFILLCOLOR, cfg);
		createPlotOptionPivot(view, cfg);
		createPlotOptionPoint(view, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_POINTINTERVAL, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_POINTPADDING, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_POINTPLACEMENT, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_POINTRANGE, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_POINTSTART, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_POINTWIDTH, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_SELECTED, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_SHADOW, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_SHOWCHECKBOX, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_SHOWINLEGEND, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_SIZE, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_SLICEDOFFSET, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_STACKING, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_STARTANGLE, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_STEMCOLOR, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_STEMDASHSTYLE, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_STEMWIDTH, cfg);
		createPlotOptionStates(view, cfg, false);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_STEP, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_STICKYTRACKING, cfg);
		createPlotOptionTooltip(view, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_THRESHOLD, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_TRACKBYAREA, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_TURBOTHRESHOLD, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_VISIBLE, cfg);
		putStringCfg(view, PROPERTITY_PLOTOPTIONS_WHISKERCOLOR, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_WHISKERLENGTH, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_WHISKERWIDTH, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_WIDTH, cfg);
		putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_WRAP, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_ZINDEX, cfg);
		putNumberCfg(view, PROPERTITY_PLOTOPTIONS_ZTHRESHOLD, cfg);

		return new JSONObject(cfg);
	}

	private void createPlotOptionDataLabels(CompositeMap parent, Map map) {
		CompositeMap view = parent.getChild(PROPERTITY_PLOTOPTIONS_DATALABELS);
		Map cfg = new HashMap();
		if (view != null) {
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_ALIGN, cfg);
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_COLOR, cfg);
			putStringCfg(view,
					PROPERTITY_PLOTOPTIONS_DATALABELS_BACKGROUND_COLOR, cfg);
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_BORDER_COLOR,
					cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_BORDER_RADIUS,
					cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_BORDER_WIDTH,
					cfg);
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_CROP, cfg);
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_INSIDE, cfg);
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_ENABLED, cfg);
			putFunctionCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_FORMATTER,
					cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_PADDING, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_ROTATION, cfg);
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_SHADOW, cfg);
			putStyleCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_STYLE, cfg);
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_USEHTML, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_X, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_Y, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DATALABELS_ZINDEX, cfg);
		}
		if (!cfg.isEmpty())
			map.put(PROPERTITY_PLOTOPTIONS_DATALABELS, new JSONObject(cfg));
	}

	private void createPlotOptionDial(CompositeMap parent, Map map) {
		CompositeMap view = parent.getChild(PROPERTITY_PLOTOPTIONS_DIAL);
		Map cfg = new HashMap();
		if (view != null) {
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_BACKGROUNDCOLOR, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_BASELENGTH, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_BASEWIDTH, cfg);
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_BORDERCOLOR, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_BORDERWIDTH, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_RADIUS, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_REARLENGTH, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_DIAL_TOPWIDTH, cfg);
		}
		if (!cfg.isEmpty())
			map.put(PROPERTITY_PLOTOPTIONS_DIAL, new JSONObject(cfg));
	}

	private void createPlotOptionMarker(CompositeMap parent, Map map,
			boolean includeStates) {
		CompositeMap view = parent.getChild(PROPERTITY_PLOTOPTIONS_MARKER);
		if (null != view) {
			Map cfg = new HashMap();
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_MARKER_ENABLED, cfg);
			putColorCfg(view, PROPERTITY_PLOTOPTIONS_MARKER_FILLCOLOR, cfg);
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_MARKER_LINECOLOR, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_MARKER_LINEWIDTH, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_MARKER_RADIUS, cfg);
			if (includeStates) {
				createPlotOptionStates(view, cfg, true);
			}
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_MARKER_SYMBOL, cfg, model);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_PLOTOPTIONS_MARKER, new JSONObject(cfg));
		}
	}

	private void createPlotOptionPivot(CompositeMap parent, Map map) {
		CompositeMap view = parent.getChild(PROPERTITY_PLOTOPTIONS_PIVOT);
		if (null != view) {
			Map cfg = new HashMap();
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_PIVOT_BACKGROUNDCOLOR,
					cfg);
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_PIVOT_BORDERCOLOR, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_PIVOT_BORDERWIDTH, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_PIVOT_RADIUS, cfg);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_PLOTOPTIONS_PIVOT, new JSONObject(cfg));
		}
	}

	private void createPlotOptionPoint(CompositeMap parent, Map map) {
		CompositeMap view = parent.getChild(PROPERTITY_PLOTOPTIONS_POINT);
		if (null != view) {
			Map cfg = new HashMap();
			putEvents(view, cfg);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_PLOTOPTIONS_POINT, new JSONObject(cfg));
		}
	}

	private void createPlotOptionStates(CompositeMap parent, Map map,
			boolean underMarker) {
		CompositeMap view = parent.getChild(PROPERTITY_PLOTOPTIONS_STATES);
		if (null != view) {
			Map cfg = new HashMap();
			createPlotOptionStatesHover(view, cfg, !underMarker);
			if (underMarker) {
				createPlotOptionStatesSelect(view, cfg);
			}
			if (!cfg.isEmpty())
				map.put(PROPERTITY_PLOTOPTIONS_STATES, new JSONObject(cfg));
		}
	}

	private void createPlotOptionStatesHover(CompositeMap parent, Map map,
			boolean includeMaker) {
		CompositeMap view = parent
				.getChild(PROPERTITY_PLOTOPTIONS_STATES_HOVER);
		if (null != view) {
			Map cfg = new HashMap();
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_STATES_ENABLED, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_STATES_LINEWIDTH, cfg);
			if (includeMaker) {
				putNumberCfg(view,
						PROPERTITY_PLOTOPTIONS_STATES_HOVER_BRIGHTNESS, cfg);
				createPlotOptionMarker(view, cfg, false);
			} else {
				putStringCfg(view, PROPERTITY_PLOTOPTIONS_STATES_FILLCOLOR, cfg);
				putStringCfg(view, PROPERTITY_PLOTOPTIONS_STATES_LINECOLOR, cfg);
				putNumberCfg(view, PROPERTITY_PLOTOPTIONS_STATES_RADIUS, cfg);
			}
			if (!cfg.isEmpty())
				map.put(PROPERTITY_PLOTOPTIONS_STATES_HOVER,
						new JSONObject(cfg));
		}
	}

	private void createPlotOptionStatesSelect(CompositeMap parent, Map map) {
		CompositeMap view = parent
				.getChild(PROPERTITY_PLOTOPTIONS_STATES_SELECT);
		Map cfg = new HashMap();
		if (null != view) {
			putBooleanCfg(view, PROPERTITY_PLOTOPTIONS_STATES_ENABLED, cfg);
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_STATES_FILLCOLOR, cfg);
			putStringCfg(view, PROPERTITY_PLOTOPTIONS_STATES_LINECOLOR, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_STATES_LINEWIDTH, cfg);
			putNumberCfg(view, PROPERTITY_PLOTOPTIONS_STATES_RADIUS, cfg);
		}
		if (!cfg.isEmpty())
			map.put(PROPERTITY_PLOTOPTIONS_STATES_SELECT, new JSONObject(cfg));
	}

	private void createPlotOptionTooltip(CompositeMap parent, Map map) {
		CompositeMap view;
		if (null == map)
			view = parent.getChild(PROPERTITY_TOOLTIP);
		else
			view = parent.getChild(PROPERTITY_PLOTOPTIONS_TOOLTIP);
		if (null == view)
			view = parent.getChild(PROPERTITY_TOOLTIP);
		if (view != null) {
			Map cfg = new HashMap();
			if (null == map) {
				putBooleanCfg(view, PROPERTITY_TOOLTIP_ANIMATION, cfg);
				putStringCfg(view, PROPERTITY_TOOLTIP_BACKGROUNDCOLOR, cfg);
				putStringCfg(view, PROPERTITY_TOOLTIP_BORDERCOLOR, cfg);
				putNumberCfg(view, PROPERTITY_TOOLTIP_BORDERRADIUS, cfg);
				putNumberCfg(view, PROPERTITY_TOOLTIP_BORDERWIDTH, cfg);
				createToolTipCrosshairs(view, cfg);
				putBooleanCfg(view, PROPERTITY_TOOLTIP_ENABLED, cfg);
				putFunctionCfg(view, PROPERTITY_TOOLTIP_FORMATTER, cfg);
				putFunctionCfg(view, PROPERTITY_TOOLTIP_POSITIONER, cfg);
				putBooleanCfg(view, PROPERTITY_TOOLTIP_SHADOW, cfg);
				putBooleanCfg(view, PROPERTITY_TOOLTIP_SHARED, cfg);
				putNumberCfg(view, PROPERTITY_TOOLTIP_SNAP, cfg);
				putStyleCfg(view, PROPERTITY_TOOLTIP_STYLE, cfg);
				putBooleanCfg(view, PROPERTITY_TOOLTIP_USEHTML, cfg);
			}
			createDateTimeLabelFormats(view, cfg);
			putBooleanCfg(view, PROPERTITY_TOOLTIP_FOLLOWPOINTER, cfg);
			putBooleanCfg(view, PROPERTITY_TOOLTIP_FOLLOWTOUCHMOVE, cfg);
			putStringCfg(view, PROPERTITY_TOOLTIP_FOOTERFORMAT, cfg);
			putStringCfg(view, PROPERTITY_TOOLTIP_HEADERFORMAT, cfg);
			putNumberCfg(view, PROPERTITY_TOOLTIP_HIDEDELAY, cfg);
			putStringCfg(view, PROPERTITY_TOOLTIP_POINTFORMAT, cfg);
			putNumberCfg(view, PROPERTITY_TOOLTIP_VALUEDECIMALS, cfg);
			putStringCfg(view, PROPERTITY_TOOLTIP_VALUEPREFIX, cfg);
			putStringCfg(view, PROPERTITY_TOOLTIP_VALUESUFFIX, cfg);
			putStringCfg(view, PROPERTITY_TOOLTIP_XDATEFORMAT, cfg);
			if (!cfg.isEmpty()) {
				if (null != map) {
					map.put(PROPERTITY_TOOLTIP, new JSONObject(cfg));
				} else
					addConfig(PROPERTITY_TOOLTIP, new JSONObject(cfg));
			}
		}
	}

	private void processSubTitle(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_SUBTITLE);
		if (null != view) {
			Map cfg = new HashMap();
			putStringCfg(view, PROPERTITY_SUBTITLE_ALIGN, cfg);
			putBooleanCfg(view, PROPERTITY_SUBTITLE_FLOATING, cfg);
			putStyleCfg(view, PROPERTITY_SUBTITLE_STYLE, cfg);
			putStringCfg(view, PROPERTITY_SUBTITLE_TEXT, cfg);
			putBooleanCfg(view, PROPERTITY_SUBTITLE_USEHTML, cfg);
			putStringCfg(view, PROPERTITY_SUBTITLE_VERTICALALIGN, cfg);
			putNumberCfg(view, PROPERTITY_SUBTITLE_X, cfg);
			putNumberCfg(view, PROPERTITY_SUBTITLE_Y, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_SUBTITLE, new JSONObject(cfg));
		}
	}

	private void processTitle(CompositeMap parent) {
		CompositeMap view = parent.getChild(PROPERTITY_TITLE);
		if (null != view) {
			Map cfg = new HashMap();
			putBooleanCfg(view, PROPERTITY_TITLE_FLOATING, cfg);
			putStringCfg(view, PROPERTITY_TITLE_ALIGN, cfg);
			putNumberCfg(view, PROPERTITY_TITLE_MARGIN, cfg);
			putStyleCfg(view, PROPERTITY_TITLE_STYLE, cfg);
			putStringCfg(view, PROPERTITY_TITLE_TEXT, cfg);
			putBooleanCfg(view, PROPERTITY_TITLE_USEHTML, cfg);
			putStringCfg(view, PROPERTITY_TITLE_VERTICALALIGN, cfg);
			putNumberCfg(view, PROPERTITY_TITLE_X, cfg);
			putNumberCfg(view, PROPERTITY_TITLE_Y, cfg);
			if (!cfg.isEmpty())
				addConfig(PROPERTITY_TITLE, new JSONObject(cfg));
		}
	}

	private void processTooltip(CompositeMap parent) {
		createPlotOptionTooltip(parent, null);
	}

	private void createToolTipCrosshairs(CompositeMap view, Map map) {
		CompositeMap pbs = view.getChild(PROPERTITY_TOOLTIP_CROSSHAIRS);
		if (null != pbs) {
			List list = pbs.getChilds();
			if (null != list) {
				JSONArray array = new JSONArray();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap pb = (CompositeMap) it.next();
					putStringCfg(pb, PROPERTITY_TOOLTIP_CROSSHAIRS_COLOR, cfg);
					putStringCfg(pb, PROPERTITY_TOOLTIP_CROSSHAIRS_DASHSTYLE,
							cfg);
					putNumberCfg(pb, PROPERTITY_TOOLTIP_CROSSHAIRS_WIDTH, cfg);
					array.put(cfg);
				}
				if (array.length() > 0)
					map.put(PROPERTITY_TOOLTIP_CROSSHAIRS, array);
			}
		} else {
			String crosshairs = view.getString(PROPERTITY_TOOLTIP_CROSSHAIRS);
			if (null != crosshairs) {
				if (crosshairs.indexOf(",") != -1) {
					putArrayCfg(view, PROPERTITY_TOOLTIP_CROSSHAIRS, map);
				} else {
					putBooleanCfg(view, PROPERTITY_TOOLTIP_CROSSHAIRS, map);
				}
			}
		}
	}

	private void processXAxis(CompositeMap parent) {
		processAxis(parent, PROPERTITY_AXIS_X);
	}

	private void processYAxis(CompositeMap parent) {
		processAxis(parent, PROPERTITY_AXIS_Y);
	}

	private void processAxis(CompositeMap parent, String name) {
		CompositeMap axis = parent.getChild(name);
		if (axis != null) {
			List list = axis.getChilds();
			JSONArray array = null;
			if (list != null) {
				array = new JSONArray();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap axi = (CompositeMap) it.next();
					putBooleanCfg(axi, PROPERTITY_AXIS_ALLOWDECIMALS, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_ALTERNATEGRIDCOLOR, cfg);
					putArrayCfg(axi, PROPERTITY_AXIS_CATEGORIES, cfg);
					// createCategories(axi,cfg);
					createDateTimeLabelFormats(axi, cfg);
					putBooleanCfg(axi, PROPERTITY_AXIS_ENDONTICK, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_GRIDLINECOLOR, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_GRIDLINEDASHSTYLE, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_GRIDLINEINTERPOLATION,
							cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_GRIDLINEWIDTH, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_ID, cfg);
					createAxisLabels(axi, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_LINECOLOR, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_LINEWIDTH, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_LINKEDTO, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MAX, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MAXPADDING, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MIN, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MINTICKINTERVAL, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_MINORGRIDLINECOLOR, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_MINORGRIDLINEDASHSTYLE,
							cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MINORGRIDLINEWIDTH, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_MINORTICKCOLOR, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MINORTICKINTERVAL, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MINORTICKLENGTH, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_MINORTICKPOSITION, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MINORTICKWIDTH, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MINRANGE, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_MINPADDING, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_OFFSET, cfg);
					putBooleanCfg(axi, PROPERTITY_AXIS_OPPOSITE, cfg);
					createPlotBands(axi, cfg);
					createPlotLines(axi, cfg);
					putBooleanCfg(axi, PROPERTITY_AXIS_REVERSED, cfg);
					putBooleanCfg(axi, PROPERTITY_AXIS_SHOWEMPTY, cfg);
					putBooleanCfg(axi, PROPERTITY_AXIS_SHOWFIRSTLABEL, cfg);
					putBooleanCfg(axi, PROPERTITY_AXIS_SHOWLASTLABEL, cfg);
					if (PROPERTITY_AXIS_Y.equals(name))
						createStackLabels(axi, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_STARTOFWEEK, cfg);
					putBooleanCfg(axi, PROPERTITY_AXIS_STARTONTICK, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_TICKCOLOR, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_TICKINTERVAL, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_TICKLENGTH, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_TICKMARKPLACEMENT, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_TICKPIXELINTERVAL, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_TICKPOSITION, cfg);
					putFunctionCfg(axi, PROPERTITY_AXIS_TICKPOSITIER, cfg);
					putArrayCfg(axi, PROPERTITY_AXIS_TICKPOSITIONS, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_TICKWIDTH, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_LINECOLOR, cfg);
					processTitle(axi, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_TYPE, cfg);
					putEvents(axi, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_NAME, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_BINDTARGET, cfg);
					putStringCfg(axi, PROPERTITY_AXIS_DATEFORMAT, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_STARTANGLE, cfg);
					putNumberCfg(axi, PROPERTITY_AXIS_ENDANGLE, cfg);
					array.put(cfg);
				}
				if (array != null)
					addConfig(name, array);
			}
		}
	}

	private void createAxisLabels(CompositeMap cview, Map map) {
		CompositeMap view = cview.getChild(PROPERTITY_AXIS_LABELS);
		if (null != view) {
			Map cfg = new HashMap();
			putStringCfg(view, PROPERTITY_AXIS_LABELS_ALIGN, cfg);
			putBooleanCfg(view, PROPERTITY_AXIS_LABELS_ENABLED, cfg);
			putNumberCfg(view, PROPERTITY_AXIS_LABELS_DISTANCE, cfg);
			putStringCfg(view, PROPERTITY_AXIS_LABELS_FORMAT, cfg);
			putFunctionCfg(view, PROPERTITY_AXIS_LABELS_FORMATTER, cfg);
			putNumberCfg(view, PROPERTITY_AXIS_LABELS_MAXSTAGGERLINES, cfg);
			putNumberCfg(view, PROPERTITY_AXIS_LABELS_ROTATION, cfg);
			putStringCfg(view, PROPERTITY_AXIS_LABELS_OVERFLOW, cfg);
			putNumberCfg(view, PROPERTITY_AXIS_LABELS_STAGGERLINES, cfg);
			putNumberCfg(view, PROPERTITY_AXIS_LABELS_STEP, cfg);
			putStyleCfg(view, PROPERTITY_AXIS_LABELS_STYLE, cfg);
			putBooleanCfg(view, PROPERTITY_AXIS_LABELS_USEHTML, cfg);
			putNumberCfg(view, PROPERTITY_AXIS_LABELS_X, cfg);
			putNumberCfg(view, PROPERTITY_AXIS_LABELS_Y, cfg);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_AXIS_LABELS, new JSONObject(cfg));
		}
	}

	// private void createCategories(CompositeMap view, Map map){
	// CompositeMap cats = view.getChild(PROPERTITY_AXIS_CATEGORIES);
	// if(cats!=null){
	// List list = cats.getChilds();
	// JSONArray array = null;
	// if(list!=null) {
	// array = new JSONArray();
	// Iterator it = list.iterator();
	// while(it.hasNext()){
	// CompositeMap cat = (CompositeMap)it.next();
	// String value = cat.getString("value","");
	// array.put(value);
	// }
	// if(array != null)
	// map.put(PROPERTITY_AXIS_CATEGORIES, array);
	// }
	// }
	// }
	private void createDateTimeLabelFormats(CompositeMap view, Map map) {
		CompositeMap formats = view
				.getChild(PROPERTITY_AXIS_DATETIMELABELFORMATS);
		if (null != formats) {
			Map cfg = new HashMap();
			putStringCfg(formats,
					PROPERTITY_AXIS_DATETIMELABELFORMATS_MILLISECOND, cfg);
			putStringCfg(formats, PROPERTITY_AXIS_DATETIMELABELFORMATS_SECOND,
					cfg);
			putStringCfg(formats, PROPERTITY_AXIS_DATETIMELABELFORMATS_MINUTE,
					cfg);
			putStringCfg(formats, PROPERTITY_AXIS_DATETIMELABELFORMATS_HOUR,
					cfg);
			putStringCfg(formats, PROPERTITY_AXIS_DATETIMELABELFORMATS_DAY, cfg);
			putStringCfg(formats, PROPERTITY_AXIS_DATETIMELABELFORMATS_WEEK,
					cfg);
			putStringCfg(formats, PROPERTITY_AXIS_DATETIMELABELFORMATS_MONTH,
					cfg);
			putStringCfg(formats, PROPERTITY_AXIS_DATETIMELABELFORMATS_YEAR,
					cfg);
			if (!cfg.isEmpty())
				map.put(PROPERTITY_AXIS_DATETIMELABELFORMATS, new JSONObject(
						cfg));
		}
	}

	private void createStackLabels(CompositeMap view, Map map) {
		CompositeMap formats = view.getChild(PROPERTITY_AXIS_STACKLABELS);
		Map cfg = new HashMap();
		if (formats != null) {
			putStringCfg(formats, PROPERTITY_AXIS_STACKLABELS_ALIGN, cfg);
			putStringCfg(formats, PROPERTITY_AXIS_STACKLABELS_TEXTALIGN, cfg);
			putStringCfg(formats, PROPERTITY_AXIS_STACKLABELS_VERTICALALIGH,
					cfg);
			putBooleanCfg(formats, PROPERTITY_AXIS_STACKLABELS_ENABLED, cfg);
			putFunctionCfg(formats, PROPERTITY_AXIS_STACKLABELS_FORMATTER, cfg);
			putStyleCfg(formats, PROPERTITY_AXIS_STACKLABELS_STYLE, cfg);
			putNumberCfg(formats, PROPERTITY_AXIS_STACKLABELS_ROTATION, cfg);
			putNumberCfg(formats, PROPERTITY_AXIS_STACKLABELS_X, cfg);
			putNumberCfg(formats, PROPERTITY_AXIS_STACKLABELS_Y, cfg);
		}
		if (!cfg.isEmpty())
			map.put(PROPERTITY_AXIS_STACKLABELS, new JSONObject(cfg));
	}

	private void createPlotBands(CompositeMap view, Map map) {
		CompositeMap pbs = view.getChild(PROPERTITY_AXIS_PLOTBANDS);
		if (pbs != null) {
			List list = pbs.getChilds();
			JSONArray array = null;
			if (list != null) {
				array = new JSONArray();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap pb = (CompositeMap) it.next();
					putStringCfg(pb, PROPERTITY_AXIS_PLOTBANDS_COLOR, cfg);
					putNumberCfg(pb, PROPERTITY_AXIS_PLOTBANDS_FROM, cfg);
					putStringCfg(pb, PROPERTITY_AXIS_PLOTBANDS_ID, cfg);
					processPlotLabel(pb, cfg);
					putStringCfg(pb, PROPERTITY_AXIS_PLOTBANDS_INNERRADIUS, cfg);
					putStringCfg(pb, PROPERTITY_AXIS_PLOTBANDS_OUTERRADIUS, cfg);
					putStringCfg(pb, PROPERTITY_AXIS_PLOTBANDS_THICKNESS, cfg);
					putNumberCfg(pb, PROPERTITY_AXIS_PLOTBANDS_TO, cfg);
					putNumberCfg(pb, PROPERTITY_AXIS_PLOTBANDS_ZINDEX, cfg);
					putEvents(pb, cfg);
					array.put(cfg);
				}
				if (array != null)
					map.put(PROPERTITY_AXIS_PLOTBANDS, array);
			}
		}
	}

	private void createPlotLines(CompositeMap view, Map map) {
		CompositeMap pls = view.getChild(PROPERTITY_AXIS_PLOTLINES);
		if (pls != null) {
			List list = pls.getChilds();
			JSONArray array = null;
			if (list != null) {
				array = new JSONArray();
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Map cfg = new HashMap();
					CompositeMap pl = (CompositeMap) it.next();

					putStringCfg(pl, PROPERTITY_AXIS_PLOTLINES_COLOR, cfg);
					putStringCfg(pl, PROPERTITY_AXIS_PLOTLINES_ID, cfg);
					putNumberCfg(pl, PROPERTITY_AXIS_PLOTLINES_VALUE, cfg);
					putNumberCfg(pl, PROPERTITY_AXIS_PLOTLINES_WIDTH, cfg);
					processPlotLabel(pl, cfg);
					putNumberCfg(pl, PROPERTITY_AXIS_PLOTLINES_ZINDEX, cfg);
					putEvents(pl, cfg);
					array.put(cfg);
				}
				if (array != null)
					map.put(PROPERTITY_AXIS_PLOTLINES, array);
			}
		}
	}

	private void processPlotLabel(CompositeMap view, Map map) {
		CompositeMap label = view.getChild(PROPERTITY_AXIS_PLOT_LABEL);
		Map cfg = new HashMap();
		if (label != null) {
			putStringCfg(label, PROPERTITY_AXIS_PLOT_LABEL_ALIGN, cfg);
			putStringCfg(label, PROPERTITY_AXIS_PLOT_LABEL_TEXT, cfg);
			putStringCfg(label, PROPERTITY_AXIS_PLOT_LABEL_VERTICALALIGN, cfg);
			putNumberCfg(label, PROPERTITY_AXIS_PLOT_LABEL_RATATION, cfg);
			putStyleCfg(label, PROPERTITY_AXIS_PLOT_LABEL_STYLE, cfg);
			putStringCfg(label, PROPERTITY_AXIS_PLOT_LABEL_TEXTALIGN, cfg);
			putNumberCfg(label, PROPERTITY_AXIS_PLOT_LABEL_X, cfg);
			putNumberCfg(label, PROPERTITY_AXIS_PLOT_LABEL_Y, cfg);
		}
		if (!cfg.isEmpty())
			map.put("label", new JSONObject(cfg));
	}

	private void processTitle(CompositeMap view, Map map) {
		CompositeMap title = view.getChild(PROPERTITY_AXIS_TITLE);
		Map cfg = new HashMap();
		if (title != null) {
			putStringCfg(title, PROPERTITY_AXIS_TITLE_ALIGN, cfg);
			putNumberCfg(title, PROPERTITY_AXIS_TITLE_MARGIN, cfg);
			putNumberCfg(title, PROPERTITY_AXIS_TITLE_OFFSET, cfg);
			putStyleCfg(title, PROPERTITY_AXIS_TITLE_STYLE, cfg);
			putNumberCfg(title, PROPERTITY_AXIS_TITLE_ROTATION, cfg);
			putStringCfg(title, PROPERTITY_AXIS_TITLE_TEXT, cfg);
			putNumberCfg(title, PROPERTITY_AXIS_TITLE_X, cfg);
			putNumberCfg(title, PROPERTITY_AXIS_TITLE_Y, cfg);
		}
		if (!cfg.isEmpty())
			map.put(PROPERTITY_AXIS_TITLE, new JSONObject(cfg));
	}

}