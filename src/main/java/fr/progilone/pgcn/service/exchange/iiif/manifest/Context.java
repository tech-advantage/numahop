
package fr.progilone.pgcn.service.exchange.iiif.manifest;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc",
    "iiif",
    "exif",
    "oa",
    "cnt",
    "dc",
    "dcterms",
    "dctypes",
    "doap",
    "foaf",
    "rdf",
    "rdfs",
    "xsd",
    "svcs",
    "as",
    "license",
    "service",
    "seeAlso",
    "within",
    "profile",
    "related",
    "logo",
    "thumbnail",
    "startCanvas",
    "contentLayer",
    "members",
    "collections",
    "manifests",
    "sequences",
    "canvases",
    "resources",
    "images",
    "otherContent",
    "structures",
    "ranges",
    "metadata",
    "description",
    "navDate",
    "rendering",
    "height",
    "width",
    "attribution",
    "viewingDirection",
    "viewingHint",
    "left-to-right",
    "right-to-left",
    "top-to-bottom",
    "bottom-to-top",
    "paged",
    "non-paged",
    "continuous",
    "individuals",
    "top",
    "multi-part",
    "facing-pages",
    "motivation",
    "resource",
    "on",
    "full",
    "selector",
    "stylesheet",
    "style",
    "default",
    "item",
    "chars",
    "encoding",
    "bytes",
    "format",
    "language",
    "value",
    "label",
    "prefix",
    "suffix",
    "exact",
    "first",
    "last",
    "next",
    "prev",
    "total",
    "startIndex"
})
public class Context {

    @JsonProperty("sc")
    private String sc;
    @JsonProperty("iiif")
    private String iiif;
    @JsonProperty("exif")
    private String exif;
    @JsonProperty("oa")
    private String oa;
    @JsonProperty("cnt")
    private String cnt;
    @JsonProperty("dc")
    private String dc;
    @JsonProperty("dcterms")
    private String dcterms;
    @JsonProperty("dctypes")
    private String dctypes;
    @JsonProperty("doap")
    private String doap;
    @JsonProperty("foaf")
    private String foaf;
    @JsonProperty("rdf")
    private String rdf;
    @JsonProperty("rdfs")
    private String rdfs;
    @JsonProperty("xsd")
    private String xsd;
    @JsonProperty("svcs")
    private String svcs;
    @JsonProperty("as")
    private String as;
    @JsonProperty("license")
    @Valid
    private License license;
    @JsonProperty("service")
    @Valid
    private Service service;
    @JsonProperty("seeAlso")
    @Valid
    private SeeAlso seeAlso;
    @JsonProperty("within")
    @Valid
    private Within within;
    @JsonProperty("profile")
    @Valid
    private Profile profile;
    @JsonProperty("related")
    @Valid
    private Related related;
    @JsonProperty("logo")
    @Valid
    private Logo logo;
    @JsonProperty("thumbnail")
    @Valid
    private Thumbnail thumbnail;
    @JsonProperty("startCanvas")
    @Valid
    private StartCanvas startCanvas;
    @JsonProperty("contentLayer")
    @Valid
    private ContentLayer contentLayer;
    @JsonProperty("members")
    @Valid
    private Members members;
    @JsonProperty("collections")
    @Valid
    private Collections collections;
    @JsonProperty("manifests")
    @Valid
    private Manifests manifests;
    @JsonProperty("sequences")
    @Valid
    private Sequences sequences;
    @JsonProperty("canvases")
    @Valid
    private Canvases canvases;
    @JsonProperty("resources")
    @Valid
    private Resources resources;
    @JsonProperty("images")
    @Valid
    private Images images;
    @JsonProperty("otherContent")
    @Valid
    private OtherContent otherContent;
    @JsonProperty("structures")
    @Valid
    private Structures structures;
    @JsonProperty("ranges")
    @Valid
    private Ranges ranges;
    @JsonProperty("metadata")
    @Valid
    private Metadata metadata;
    @JsonProperty("description")
    @Valid
    private Description description;
    @JsonProperty("navDate")
    @Valid
    private NavDate navDate;
    @JsonProperty("rendering")
    @Valid
    private Rendering rendering;
    @JsonProperty("height")
    @Valid
    private Height height;
    @JsonProperty("width")
    @Valid
    private Width width;
    @JsonProperty("attribution")
    @Valid
    private Attribution attribution;
    @JsonProperty("viewingDirection")
    @Valid
    private ViewingDirection viewingDirection;
    @JsonProperty("viewingHint")
    @Valid
    private ViewingHint viewingHint;
    @JsonProperty("left-to-right")
    private String leftToRight;
    @JsonProperty("right-to-left")
    private String rightToLeft;
    @JsonProperty("top-to-bottom")
    private String topToBottom;
    @JsonProperty("bottom-to-top")
    private String bottomToTop;
    @JsonProperty("paged")
    private String paged;
    @JsonProperty("non-paged")
    private String nonPaged;
    @JsonProperty("continuous")
    private String continuous;
    @JsonProperty("individuals")
    private String individuals;
    @JsonProperty("top")
    private String top;
    @JsonProperty("multi-part")
    private String multiPart;
    @JsonProperty("facing-pages")
    private String facingPages;
    @JsonProperty("motivation")
    @Valid
    private Motivation motivation;
    @JsonProperty("resource")
    @Valid
    private Resource resource;
    @JsonProperty("on")
    @Valid
    private On on;
    @JsonProperty("full")
    @Valid
    private Full full;
    @JsonProperty("selector")
    @Valid
    private Selector selector;
    @JsonProperty("stylesheet")
    @Valid
    private Stylesheet stylesheet;
    @JsonProperty("style")
    @Valid
    private Style style;
    @JsonProperty("default")
    @Valid
    private Default _default;
    @JsonProperty("item")
    @Valid
    private Item item;
    @JsonProperty("chars")
    @Valid
    private Chars chars;
    @JsonProperty("encoding")
    @Valid
    private Encoding encoding;
    @JsonProperty("bytes")
    @Valid
    private Bytes bytes;
    @JsonProperty("format")
    @Valid
    private Format format;
    @JsonProperty("language")
    @Valid
    private Language language;
    @JsonProperty("value")
    @Valid
    private Value value;
    @JsonProperty("label")
    @Valid
    private Label label;
    @JsonProperty("prefix")
    private String prefix;
    @JsonProperty("suffix")
    private String suffix;
    @JsonProperty("exact")
    private String exact;
    @JsonProperty("first")
    @Valid
    private First first;
    @JsonProperty("last")
    @Valid
    private Last last;
    @JsonProperty("next")
    @Valid
    private Next next;
    @JsonProperty("prev")
    @Valid
    private Prev prev;
    @JsonProperty("total")
    @Valid
    private Total total;
    @JsonProperty("startIndex")
    @Valid
    private StartIndex startIndex;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Context() {
    }

    /**
     *
     * @param total
     * @param selector
     * @param resources
     * @param last
     * @param navDate
     * @param motivation
     * @param ranges
     * @param viewingDirection
     * @param leftToRight
     * @param related
     * @param canvases
     * @param paged
     * @param contentLayer
     * @param height
     * @param oa
     * @param description
     * @param exact
     * @param value
     * @param on
     * @param rdf
     * @param iiif
     * @param viewingHint
     * @param topToBottom
     * @param metadata
     * @param exif
     * @param dc
     * @param bottomToTop
     * @param continuous
     * @param foaf
     * @param width
     * @param sc
     * @param label
     * @param format
     * @param facingPages
     * @param prev
     * @param structures
     * @param thumbnail
     * @param prefix
     * @param images
     * @param seeAlso
     * @param language
     * @param svcs
     * @param nonPaged
     * @param attribution
     * @param bytes
     * @param next
     * @param dcterms
     * @param rdfs
     * @param manifests
     * @param style
     * @param full
     * @param rightToLeft
     * @param _default
     * @param encoding
     * @param stylesheet
     * @param license
     * @param dctypes
     * @param members
     * @param collections
     * @param top
     * @param individuals
     * @param profile
     * @param logo
     * @param startIndex
     * @param multiPart
     * @param cnt
     * @param resource
     * @param sequences
     * @param as
     * @param suffix
     * @param rendering
     * @param startCanvas
     * @param otherContent
     * @param item
     * @param service
     * @param xsd
     * @param chars
     * @param within
     * @param doap
     * @param first
     */
    public Context(String sc, String iiif, String exif, String oa, String cnt, String dc, String dcterms, String dctypes, String doap, String foaf, String rdf, String rdfs, String xsd, String svcs, String as, License license, Service service, SeeAlso seeAlso, Within within, Profile profile, Related related, Logo logo, Thumbnail thumbnail, StartCanvas startCanvas, ContentLayer contentLayer, Members members, Collections collections, Manifests manifests, Sequences sequences, Canvases canvases, Resources resources, Images images, OtherContent otherContent, Structures structures, Ranges ranges, Metadata metadata, Description description, NavDate navDate, Rendering rendering, Height height, Width width, Attribution attribution, ViewingDirection viewingDirection, ViewingHint viewingHint, String leftToRight, String rightToLeft, String topToBottom, String bottomToTop, String paged, String nonPaged, String continuous, String individuals, String top, String multiPart, String facingPages, Motivation motivation, Resource resource, On on, Full full, Selector selector, Stylesheet stylesheet, Style style, Default _default, Item item, Chars chars, Encoding encoding, Bytes bytes, Format format, Language language, Value value, Label label, String prefix, String suffix, String exact, First first, Last last, Next next, Prev prev, Total total, StartIndex startIndex) {
        super();
        this.sc = sc;
        this.iiif = iiif;
        this.exif = exif;
        this.oa = oa;
        this.cnt = cnt;
        this.dc = dc;
        this.dcterms = dcterms;
        this.dctypes = dctypes;
        this.doap = doap;
        this.foaf = foaf;
        this.rdf = rdf;
        this.rdfs = rdfs;
        this.xsd = xsd;
        this.svcs = svcs;
        this.as = as;
        this.license = license;
        this.service = service;
        this.seeAlso = seeAlso;
        this.within = within;
        this.profile = profile;
        this.related = related;
        this.logo = logo;
        this.thumbnail = thumbnail;
        this.startCanvas = startCanvas;
        this.contentLayer = contentLayer;
        this.members = members;
        this.collections = collections;
        this.manifests = manifests;
        this.sequences = sequences;
        this.canvases = canvases;
        this.resources = resources;
        this.images = images;
        this.otherContent = otherContent;
        this.structures = structures;
        this.ranges = ranges;
        this.metadata = metadata;
        this.description = description;
        this.navDate = navDate;
        this.rendering = rendering;
        this.height = height;
        this.width = width;
        this.attribution = attribution;
        this.viewingDirection = viewingDirection;
        this.viewingHint = viewingHint;
        this.leftToRight = leftToRight;
        this.rightToLeft = rightToLeft;
        this.topToBottom = topToBottom;
        this.bottomToTop = bottomToTop;
        this.paged = paged;
        this.nonPaged = nonPaged;
        this.continuous = continuous;
        this.individuals = individuals;
        this.top = top;
        this.multiPart = multiPart;
        this.facingPages = facingPages;
        this.motivation = motivation;
        this.resource = resource;
        this.on = on;
        this.full = full;
        this.selector = selector;
        this.stylesheet = stylesheet;
        this.style = style;
        this._default = _default;
        this.item = item;
        this.chars = chars;
        this.encoding = encoding;
        this.bytes = bytes;
        this.format = format;
        this.language = language;
        this.value = value;
        this.label = label;
        this.prefix = prefix;
        this.suffix = suffix;
        this.exact = exact;
        this.first = first;
        this.last = last;
        this.next = next;
        this.prev = prev;
        this.total = total;
        this.startIndex = startIndex;
    }

    @JsonProperty("sc")
    public String getSc() {
        return sc;
    }

    @JsonProperty("sc")
    public void setSc(String sc) {
        this.sc = sc;
    }

    public Context withSc(String sc) {
        this.sc = sc;
        return this;
    }

    @JsonProperty("iiif")
    public String getIiif() {
        return iiif;
    }

    @JsonProperty("iiif")
    public void setIiif(String iiif) {
        this.iiif = iiif;
    }

    public Context withIiif(String iiif) {
        this.iiif = iiif;
        return this;
    }

    @JsonProperty("exif")
    public String getExif() {
        return exif;
    }

    @JsonProperty("exif")
    public void setExif(String exif) {
        this.exif = exif;
    }

    public Context withExif(String exif) {
        this.exif = exif;
        return this;
    }

    @JsonProperty("oa")
    public String getOa() {
        return oa;
    }

    @JsonProperty("oa")
    public void setOa(String oa) {
        this.oa = oa;
    }

    public Context withOa(String oa) {
        this.oa = oa;
        return this;
    }

    @JsonProperty("cnt")
    public String getCnt() {
        return cnt;
    }

    @JsonProperty("cnt")
    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public Context withCnt(String cnt) {
        this.cnt = cnt;
        return this;
    }

    @JsonProperty("dc")
    public String getDc() {
        return dc;
    }

    @JsonProperty("dc")
    public void setDc(String dc) {
        this.dc = dc;
    }

    public Context withDc(String dc) {
        this.dc = dc;
        return this;
    }

    @JsonProperty("dcterms")
    public String getDcterms() {
        return dcterms;
    }

    @JsonProperty("dcterms")
    public void setDcterms(String dcterms) {
        this.dcterms = dcterms;
    }

    public Context withDcterms(String dcterms) {
        this.dcterms = dcterms;
        return this;
    }

    @JsonProperty("dctypes")
    public String getDctypes() {
        return dctypes;
    }

    @JsonProperty("dctypes")
    public void setDctypes(String dctypes) {
        this.dctypes = dctypes;
    }

    public Context withDctypes(String dctypes) {
        this.dctypes = dctypes;
        return this;
    }

    @JsonProperty("doap")
    public String getDoap() {
        return doap;
    }

    @JsonProperty("doap")
    public void setDoap(String doap) {
        this.doap = doap;
    }

    public Context withDoap(String doap) {
        this.doap = doap;
        return this;
    }

    @JsonProperty("foaf")
    public String getFoaf() {
        return foaf;
    }

    @JsonProperty("foaf")
    public void setFoaf(String foaf) {
        this.foaf = foaf;
    }

    public Context withFoaf(String foaf) {
        this.foaf = foaf;
        return this;
    }

    @JsonProperty("rdf")
    public String getRdf() {
        return rdf;
    }

    @JsonProperty("rdf")
    public void setRdf(String rdf) {
        this.rdf = rdf;
    }

    public Context withRdf(String rdf) {
        this.rdf = rdf;
        return this;
    }

    @JsonProperty("rdfs")
    public String getRdfs() {
        return rdfs;
    }

    @JsonProperty("rdfs")
    public void setRdfs(String rdfs) {
        this.rdfs = rdfs;
    }

    public Context withRdfs(String rdfs) {
        this.rdfs = rdfs;
        return this;
    }

    @JsonProperty("xsd")
    public String getXsd() {
        return xsd;
    }

    @JsonProperty("xsd")
    public void setXsd(String xsd) {
        this.xsd = xsd;
    }

    public Context withXsd(String xsd) {
        this.xsd = xsd;
        return this;
    }

    @JsonProperty("svcs")
    public String getSvcs() {
        return svcs;
    }

    @JsonProperty("svcs")
    public void setSvcs(String svcs) {
        this.svcs = svcs;
    }

    public Context withSvcs(String svcs) {
        this.svcs = svcs;
        return this;
    }

    @JsonProperty("as")
    public String getAs() {
        return as;
    }

    @JsonProperty("as")
    public void setAs(String as) {
        this.as = as;
    }

    public Context withAs(String as) {
        this.as = as;
        return this;
    }

    @JsonProperty("license")
    public License getLicense() {
        return license;
    }

    @JsonProperty("license")
    public void setLicense(License license) {
        this.license = license;
    }

    public Context withLicense(License license) {
        this.license = license;
        return this;
    }

    @JsonProperty("service")
    public Service getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(Service service) {
        this.service = service;
    }

    public Context withService(Service service) {
        this.service = service;
        return this;
    }

    @JsonProperty("seeAlso")
    public SeeAlso getSeeAlso() {
        return seeAlso;
    }

    @JsonProperty("seeAlso")
    public void setSeeAlso(SeeAlso seeAlso) {
        this.seeAlso = seeAlso;
    }

    public Context withSeeAlso(SeeAlso seeAlso) {
        this.seeAlso = seeAlso;
        return this;
    }

    @JsonProperty("within")
    public Within getWithin() {
        return within;
    }

    @JsonProperty("within")
    public void setWithin(Within within) {
        this.within = within;
    }

    public Context withWithin(Within within) {
        this.within = within;
        return this;
    }

    @JsonProperty("profile")
    public Profile getProfile() {
        return profile;
    }

    @JsonProperty("profile")
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Context withProfile(Profile profile) {
        this.profile = profile;
        return this;
    }

    @JsonProperty("related")
    public Related getRelated() {
        return related;
    }

    @JsonProperty("related")
    public void setRelated(Related related) {
        this.related = related;
    }

    public Context withRelated(Related related) {
        this.related = related;
        return this;
    }

    @JsonProperty("logo")
    public Logo getLogo() {
        return logo;
    }

    @JsonProperty("logo")
    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public Context withLogo(Logo logo) {
        this.logo = logo;
        return this;
    }

    @JsonProperty("thumbnail")
    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    @JsonProperty("thumbnail")
    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Context withThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    @JsonProperty("startCanvas")
    public StartCanvas getStartCanvas() {
        return startCanvas;
    }

    @JsonProperty("startCanvas")
    public void setStartCanvas(StartCanvas startCanvas) {
        this.startCanvas = startCanvas;
    }

    public Context withStartCanvas(StartCanvas startCanvas) {
        this.startCanvas = startCanvas;
        return this;
    }

    @JsonProperty("contentLayer")
    public ContentLayer getContentLayer() {
        return contentLayer;
    }

    @JsonProperty("contentLayer")
    public void setContentLayer(ContentLayer contentLayer) {
        this.contentLayer = contentLayer;
    }

    public Context withContentLayer(ContentLayer contentLayer) {
        this.contentLayer = contentLayer;
        return this;
    }

    @JsonProperty("members")
    public Members getMembers() {
        return members;
    }

    @JsonProperty("members")
    public void setMembers(Members members) {
        this.members = members;
    }

    public Context withMembers(Members members) {
        this.members = members;
        return this;
    }

    @JsonProperty("collections")
    public Collections getCollections() {
        return collections;
    }

    @JsonProperty("collections")
    public void setCollections(Collections collections) {
        this.collections = collections;
    }

    public Context withCollections(Collections collections) {
        this.collections = collections;
        return this;
    }

    @JsonProperty("manifests")
    public Manifests getManifests() {
        return manifests;
    }

    @JsonProperty("manifests")
    public void setManifests(Manifests manifests) {
        this.manifests = manifests;
    }

    public Context withManifests(Manifests manifests) {
        this.manifests = manifests;
        return this;
    }

    @JsonProperty("sequences")
    public Sequences getSequences() {
        return sequences;
    }

    @JsonProperty("sequences")
    public void setSequences(Sequences sequences) {
        this.sequences = sequences;
    }

    public Context withSequences(Sequences sequences) {
        this.sequences = sequences;
        return this;
    }

    @JsonProperty("canvases")
    public Canvases getCanvases() {
        return canvases;
    }

    @JsonProperty("canvases")
    public void setCanvases(Canvases canvases) {
        this.canvases = canvases;
    }

    public Context withCanvases(Canvases canvases) {
        this.canvases = canvases;
        return this;
    }

    @JsonProperty("resources")
    public Resources getResources() {
        return resources;
    }

    @JsonProperty("resources")
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Context withResources(Resources resources) {
        this.resources = resources;
        return this;
    }

    @JsonProperty("images")
    public Images getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(Images images) {
        this.images = images;
    }

    public Context withImages(Images images) {
        this.images = images;
        return this;
    }

    @JsonProperty("otherContent")
    public OtherContent getOtherContent() {
        return otherContent;
    }

    @JsonProperty("otherContent")
    public void setOtherContent(OtherContent otherContent) {
        this.otherContent = otherContent;
    }

    public Context withOtherContent(OtherContent otherContent) {
        this.otherContent = otherContent;
        return this;
    }

    @JsonProperty("structures")
    public Structures getStructures() {
        return structures;
    }

    @JsonProperty("structures")
    public void setStructures(Structures structures) {
        this.structures = structures;
    }

    public Context withStructures(Structures structures) {
        this.structures = structures;
        return this;
    }

    @JsonProperty("ranges")
    public Ranges getRanges() {
        return ranges;
    }

    @JsonProperty("ranges")
    public void setRanges(Ranges ranges) {
        this.ranges = ranges;
    }

    public Context withRanges(Ranges ranges) {
        this.ranges = ranges;
        return this;
    }

    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Context withMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @JsonProperty("description")
    public Description getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Description description) {
        this.description = description;
    }

    public Context withDescription(Description description) {
        this.description = description;
        return this;
    }

    @JsonProperty("navDate")
    public NavDate getNavDate() {
        return navDate;
    }

    @JsonProperty("navDate")
    public void setNavDate(NavDate navDate) {
        this.navDate = navDate;
    }

    public Context withNavDate(NavDate navDate) {
        this.navDate = navDate;
        return this;
    }

    @JsonProperty("rendering")
    public Rendering getRendering() {
        return rendering;
    }

    @JsonProperty("rendering")
    public void setRendering(Rendering rendering) {
        this.rendering = rendering;
    }

    public Context withRendering(Rendering rendering) {
        this.rendering = rendering;
        return this;
    }

    @JsonProperty("height")
    public Height getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(Height height) {
        this.height = height;
    }

    public Context withHeight(Height height) {
        this.height = height;
        return this;
    }

    @JsonProperty("width")
    public Width getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(Width width) {
        this.width = width;
    }

    public Context withWidth(Width width) {
        this.width = width;
        return this;
    }

    @JsonProperty("attribution")
    public Attribution getAttribution() {
        return attribution;
    }

    @JsonProperty("attribution")
    public void setAttribution(Attribution attribution) {
        this.attribution = attribution;
    }

    public Context withAttribution(Attribution attribution) {
        this.attribution = attribution;
        return this;
    }

    @JsonProperty("viewingDirection")
    public ViewingDirection getViewingDirection() {
        return viewingDirection;
    }

    @JsonProperty("viewingDirection")
    public void setViewingDirection(ViewingDirection viewingDirection) {
        this.viewingDirection = viewingDirection;
    }

    public Context withViewingDirection(ViewingDirection viewingDirection) {
        this.viewingDirection = viewingDirection;
        return this;
    }

    @JsonProperty("viewingHint")
    public ViewingHint getViewingHint() {
        return viewingHint;
    }

    @JsonProperty("viewingHint")
    public void setViewingHint(ViewingHint viewingHint) {
        this.viewingHint = viewingHint;
    }

    public Context withViewingHint(ViewingHint viewingHint) {
        this.viewingHint = viewingHint;
        return this;
    }

    @JsonProperty("left-to-right")
    public String getLeftToRight() {
        return leftToRight;
    }

    @JsonProperty("left-to-right")
    public void setLeftToRight(String leftToRight) {
        this.leftToRight = leftToRight;
    }

    public Context withLeftToRight(String leftToRight) {
        this.leftToRight = leftToRight;
        return this;
    }

    @JsonProperty("right-to-left")
    public String getRightToLeft() {
        return rightToLeft;
    }

    @JsonProperty("right-to-left")
    public void setRightToLeft(String rightToLeft) {
        this.rightToLeft = rightToLeft;
    }

    public Context withRightToLeft(String rightToLeft) {
        this.rightToLeft = rightToLeft;
        return this;
    }

    @JsonProperty("top-to-bottom")
    public String getTopToBottom() {
        return topToBottom;
    }

    @JsonProperty("top-to-bottom")
    public void setTopToBottom(String topToBottom) {
        this.topToBottom = topToBottom;
    }

    public Context withTopToBottom(String topToBottom) {
        this.topToBottom = topToBottom;
        return this;
    }

    @JsonProperty("bottom-to-top")
    public String getBottomToTop() {
        return bottomToTop;
    }

    @JsonProperty("bottom-to-top")
    public void setBottomToTop(String bottomToTop) {
        this.bottomToTop = bottomToTop;
    }

    public Context withBottomToTop(String bottomToTop) {
        this.bottomToTop = bottomToTop;
        return this;
    }

    @JsonProperty("paged")
    public String getPaged() {
        return paged;
    }

    @JsonProperty("paged")
    public void setPaged(String paged) {
        this.paged = paged;
    }

    public Context withPaged(String paged) {
        this.paged = paged;
        return this;
    }

    @JsonProperty("non-paged")
    public String getNonPaged() {
        return nonPaged;
    }

    @JsonProperty("non-paged")
    public void setNonPaged(String nonPaged) {
        this.nonPaged = nonPaged;
    }

    public Context withNonPaged(String nonPaged) {
        this.nonPaged = nonPaged;
        return this;
    }

    @JsonProperty("continuous")
    public String getContinuous() {
        return continuous;
    }

    @JsonProperty("continuous")
    public void setContinuous(String continuous) {
        this.continuous = continuous;
    }

    public Context withContinuous(String continuous) {
        this.continuous = continuous;
        return this;
    }

    @JsonProperty("individuals")
    public String getIndividuals() {
        return individuals;
    }

    @JsonProperty("individuals")
    public void setIndividuals(String individuals) {
        this.individuals = individuals;
    }

    public Context withIndividuals(String individuals) {
        this.individuals = individuals;
        return this;
    }

    @JsonProperty("top")
    public String getTop() {
        return top;
    }

    @JsonProperty("top")
    public void setTop(String top) {
        this.top = top;
    }

    public Context withTop(String top) {
        this.top = top;
        return this;
    }

    @JsonProperty("multi-part")
    public String getMultiPart() {
        return multiPart;
    }

    @JsonProperty("multi-part")
    public void setMultiPart(String multiPart) {
        this.multiPart = multiPart;
    }

    public Context withMultiPart(String multiPart) {
        this.multiPart = multiPart;
        return this;
    }

    @JsonProperty("facing-pages")
    public String getFacingPages() {
        return facingPages;
    }

    @JsonProperty("facing-pages")
    public void setFacingPages(String facingPages) {
        this.facingPages = facingPages;
    }

    public Context withFacingPages(String facingPages) {
        this.facingPages = facingPages;
        return this;
    }

    @JsonProperty("motivation")
    public Motivation getMotivation() {
        return motivation;
    }

    @JsonProperty("motivation")
    public void setMotivation(Motivation motivation) {
        this.motivation = motivation;
    }

    public Context withMotivation(Motivation motivation) {
        this.motivation = motivation;
        return this;
    }

    @JsonProperty("resource")
    public Resource getResource() {
        return resource;
    }

    @JsonProperty("resource")
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Context withResource(Resource resource) {
        this.resource = resource;
        return this;
    }

    @JsonProperty("on")
    public On getOn() {
        return on;
    }

    @JsonProperty("on")
    public void setOn(On on) {
        this.on = on;
    }

    public Context withOn(On on) {
        this.on = on;
        return this;
    }

    @JsonProperty("full")
    public Full getFull() {
        return full;
    }

    @JsonProperty("full")
    public void setFull(Full full) {
        this.full = full;
    }

    public Context withFull(Full full) {
        this.full = full;
        return this;
    }

    @JsonProperty("selector")
    public Selector getSelector() {
        return selector;
    }

    @JsonProperty("selector")
    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Context withSelector(Selector selector) {
        this.selector = selector;
        return this;
    }

    @JsonProperty("stylesheet")
    public Stylesheet getStylesheet() {
        return stylesheet;
    }

    @JsonProperty("stylesheet")
    public void setStylesheet(Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }

    public Context withStylesheet(Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
        return this;
    }

    @JsonProperty("style")
    public Style getStyle() {
        return style;
    }

    @JsonProperty("style")
    public void setStyle(Style style) {
        this.style = style;
    }

    public Context withStyle(Style style) {
        this.style = style;
        return this;
    }

    @JsonProperty("default")
    public Default getDefault() {
        return _default;
    }

    @JsonProperty("default")
    public void setDefault(Default _default) {
        this._default = _default;
    }

    public Context withDefault(Default _default) {
        this._default = _default;
        return this;
    }

    @JsonProperty("item")
    public Item getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(Item item) {
        this.item = item;
    }

    public Context withItem(Item item) {
        this.item = item;
        return this;
    }

    @JsonProperty("chars")
    public Chars getChars() {
        return chars;
    }

    @JsonProperty("chars")
    public void setChars(Chars chars) {
        this.chars = chars;
    }

    public Context withChars(Chars chars) {
        this.chars = chars;
        return this;
    }

    @JsonProperty("encoding")
    public Encoding getEncoding() {
        return encoding;
    }

    @JsonProperty("encoding")
    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public Context withEncoding(Encoding encoding) {
        this.encoding = encoding;
        return this;
    }

    @JsonProperty("bytes")
    public Bytes getBytes() {
        return bytes;
    }

    @JsonProperty("bytes")
    public void setBytes(Bytes bytes) {
        this.bytes = bytes;
    }

    public Context withBytes(Bytes bytes) {
        this.bytes = bytes;
        return this;
    }

    @JsonProperty("format")
    public Format getFormat() {
        return format;
    }

    @JsonProperty("format")
    public void setFormat(Format format) {
        this.format = format;
    }

    public Context withFormat(Format format) {
        this.format = format;
        return this;
    }

    @JsonProperty("language")
    public Language getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Language language) {
        this.language = language;
    }

    public Context withLanguage(Language language) {
        this.language = language;
        return this;
    }

    @JsonProperty("value")
    public Value getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Value value) {
        this.value = value;
    }

    public Context withValue(Value value) {
        this.value = value;
        return this;
    }

    @JsonProperty("label")
    public Label getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(Label label) {
        this.label = label;
    }

    public Context withLabel(Label label) {
        this.label = label;
        return this;
    }

    @JsonProperty("prefix")
    public String getPrefix() {
        return prefix;
    }

    @JsonProperty("prefix")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Context withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @JsonProperty("suffix")
    public String getSuffix() {
        return suffix;
    }

    @JsonProperty("suffix")
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Context withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    @JsonProperty("exact")
    public String getExact() {
        return exact;
    }

    @JsonProperty("exact")
    public void setExact(String exact) {
        this.exact = exact;
    }

    public Context withExact(String exact) {
        this.exact = exact;
        return this;
    }

    @JsonProperty("first")
    public First getFirst() {
        return first;
    }

    @JsonProperty("first")
    public void setFirst(First first) {
        this.first = first;
    }

    public Context withFirst(First first) {
        this.first = first;
        return this;
    }

    @JsonProperty("last")
    public Last getLast() {
        return last;
    }

    @JsonProperty("last")
    public void setLast(Last last) {
        this.last = last;
    }

    public Context withLast(Last last) {
        this.last = last;
        return this;
    }

    @JsonProperty("next")
    public Next getNext() {
        return next;
    }

    @JsonProperty("next")
    public void setNext(Next next) {
        this.next = next;
    }

    public Context withNext(Next next) {
        this.next = next;
        return this;
    }

    @JsonProperty("prev")
    public Prev getPrev() {
        return prev;
    }

    @JsonProperty("prev")
    public void setPrev(Prev prev) {
        this.prev = prev;
    }

    public Context withPrev(Prev prev) {
        this.prev = prev;
        return this;
    }

    @JsonProperty("total")
    public Total getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Total total) {
        this.total = total;
    }

    public Context withTotal(Total total) {
        this.total = total;
        return this;
    }

    @JsonProperty("startIndex")
    public StartIndex getStartIndex() {
        return startIndex;
    }

    @JsonProperty("startIndex")
    public void setStartIndex(StartIndex startIndex) {
        this.startIndex = startIndex;
    }

    public Context withStartIndex(StartIndex startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Context withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
