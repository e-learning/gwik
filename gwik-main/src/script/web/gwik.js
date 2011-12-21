var GWIK = {
    refreshTimer: null,
    map: null,
    markers: null
};

function mapEvent(event) {
    clearTimeout(GWIK.refreshTimer);
    GWIK.refreshTimer = setTimeout("refresh()", 2000);
}

YMaps.jQuery(function () {
            // Создает экземпляр карты и привязывает его к созданному контейнеру
            GWIK.map = new YMaps.Map(YMaps.jQuery("#basicMap")[0]);

            // Устанавливает начальные параметры отображения карты: центр карты и коэффициент масштабирования
            GWIK.map.setCenter(new YMaps.GeoPoint(30.33, 59.95), 13);
            YMaps.Events.observe(GWIK.map, GWIK.map.Events.MoveEnd, function (map, mEvent) {
                mapEvent(mEvent);
            },
            this);
            YMaps.Events.observe(GWIK.map, GWIK.map.Events.DblClick, function (map, mEvent) {
                mapEvent(mEvent);
            },
            this);

            GWIK.map.addControl(new YMaps.TypeControl());
            GWIK.map.addControl(new YMaps.ToolBar());
            GWIK.map.addControl(new YMaps.Zoom());

            GWIK.markers = YMaps.Group();
});

function refresh() {
    $.ajax({
        url: '/main.xml',
        type: "GET",
        dataType: "json",
        async: true,
        data: {
            bbox: GWIK.map.converter.clientPixelsToCoordinates(new YMaps.Point(1, 599)) + "," +
            GWIK.map.converter.clientPixelsToCoordinates(new YMaps.Point(699, 1))
        },
        success: function(data, textStatus) {
            $("#rightbar").html("");
            //GWIK.markers.removeAll();

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

                var placemark = new YMaps.Placemark(new YMaps.GeoPoint(wikiObj.lon, wikiObj.lat));
                placemark.setBalloonContent("<h5>" + wikiObj.title + "</h5>" +"<div><img src=" + wikiObj.imageUrl + "></src></div>");
                GWIK.map.addOverlay(placemark);
                //GWIK.markers.add(placemark, i);
            }

        },
        error: function() {
            alert('error');
        }
    });
}