var GWIK = {
    refreshTimer: null,
    map: null,
    markers: null
};

function mapEvent(event) {
    clearTimeout(GWIK.refreshTimer);
    GWIK.refreshTimer = setTimeout("refresh()", 2000);
}

$(document).ready(function() {
    GWIK.map = new OpenLayers.Map("basicMap", {eventListeners: {
        "moveend": mapEvent,
        "zoomend": mapEvent}});
    var mapnik = new OpenLayers.Layer.OSM();
    GWIK.map.addLayer(mapnik);
    GWIK.map.setCenter(new OpenLayers.LonLat(30.33, 59.95)// Center of the map
        .transform(
        new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
        new OpenLayers.Projection("EPSG:900913") // to Spherical Mercator Projection
    ), 13 // Zoom level
    );
    GWIK.markers = new OpenLayers.Layer.Markers("Markers");
    GWIK.photos = new OpenLayers.Layer.Markers("Markers");
    GWIK.map.addLayer(GWIK.markers);
    GWIK.map.addLayer(GWIK.photos);

    /* $("#showPhotos").click(function() {
     $.ajax({
     url: 'kmlReq.xml',
     type: "GET",
     dataType: "json",
     async: true,
     data: {
     bbox: GWIK.map.getExtent().transform(
     new OpenLayers.Projection("EPSG:900913"), // from Spherical Mercator Projection
     new OpenLayers.Projection("EPSG:4326") // transform to WGS 1984
     ).toBBOX()
     },
     success: function(data, textStatus) {

     for (var i in data.result) {
     var photoObj = data.result[i];


     var ll = (new OpenLayers.LonLat(photoObj.lon, photoObj.lat).transform(
     new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
     GWIK.map.getProjectionObject() // to Spherical Mercator Projection
     ));
     var size = new OpenLayers.Size(50, 50);
     var offset = new OpenLayers.Pixel(-Math.round(size.w / 2), -size.h);
     var icon = new OpenLayers.Icon(photoObj.iconUrl, size, offset);


     var feature = new OpenLayers.Feature(photos, ll);
     feature.closeBox = true;
     feature.popupClass = OpenLayers.Class(OpenLayers.Popup.Anchored, {
     'autoSize': true
     });
     feature.data.popupContentHTML = '<img src=' + photoObj.mediumImUrl + '></img>';
     feature.data.icon = icon;
     var marker = feature.createMarker();

     var markerClick = function(evt) {
     if (this.popup == null) {
     this.popup = this.createPopup(this.closeBox);
     GWIK.map.addPopup(this.popup);
     this.popup.show();
     }
     else {
     this.popup.toggle();
     }
     OpenLayers.Event.stop(evt);
     };

     marker.events.register("mousedown", feature, markerClick);

     photos.addMarker(marker);
     }

     },
     error: function() {

     }
     });
     });*/

});


function refresh() {
    $.ajax({
        url: '/main.xml',
        type: "GET",
        dataType: "json",
        async: true,
        data: {
            bbox: GWIK.map.getExtent().transform(
                new OpenLayers.Projection("EPSG:900913"), // from Spherical Mercator Projection
                new OpenLayers.Projection("EPSG:4326") // transform to WGS 1984
            ).toBBOX()
        },
        success: function(data, textStatus) {
            $("#rightbar").html("");
            GWIK.markers.clearMarkers();

            for (var i = 0; i < data.result.length; ++i) {
                var wikiObj = data.result[i];

                var shortTitle = wikiObj.title.length > 16
                    ? wikiObj.title.slice(0, 16) + "..."
                    : wikiObj.title;
                $("#rightbar").append(
                    "" +
                        "<div class='wiki-object'>" +
                        "<img class='thumbnail' src='http://placehold.it/50x50' /> " +
                        "<strong><a href='" + wikiObj.url + "'>" + shortTitle + "</a></strong><br>" +
                        "<i>Short snippet from page. Max 100 symbols</i>" +
                        "</div>"
                );

                var coords = (new OpenLayers.LonLat(wikiObj.lon, wikiObj.lat).transform(
                    new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
                    GWIK.map.getProjectionObject() // to Spherical Mercator Projection
                ));

                var feature = new OpenLayers.Feature(GWIK.markers, coords);
                feature.popupClass = OpenLayers.Class(OpenLayers.Popup.Anchored, {
                    'autoSize': true
                });

                feature.data.popupContentHTML = '<h5>' + wikiObj.title + '</h5>' +
                    '<div><img src=' + wikiObj.imageUrl + '></src></div>';
                var marker = feature.createMarker();

                var markerClick = function(evt) {
                    if (this.popup == null) {
                        this.popup = this.createPopup(this.closeBox);
                        GWIK.map.addPopup(this.popup);
                        this.popup.show();
                    }
                    else {
                        this.popup.toggle();
                    }
                    OpenLayers.Event.stop(evt);
                }

                marker.events.register("mousedown", feature, markerClick);
                GWIK.markers.addMarker(marker);
            }

        },
        error: function() {
            alert('error');
        }
    });
}