//TODO dont open the camera until the user hits take setPict ???

var mapp = {
	v:{
		m: 0, //message,
		mg: 0,//marker cluster
		info: 0, //infowindo bootstrap
		infocnt:0, //infocontent
		infot:0, //info title
		prec: 4, //4	0.0001	0° 00′ 0.36″	individual street, land parcel	11.132 m	10.247 m	7.871 m	4.3496 m
		// https://en.wikipedia.org/wiki/Decimal_degrees#Precision
		lupos: [0,0,0,0], //last update pos
		cpos: [0,0,0], //current pos (updated by posChanged)
		cfpos: [0,0,0], //current form pos (updated by onFormShow)
		marks: [], //markers
		photo_bin: false //is photo input file?
	},
	m: function m(s){
		mapp.v.m.innerHTML = s;
		console.log(s);
	},
	posChanged: function posChanged(ev){//updates current position
		console.log("pos changed");
		var lat = ev.latitude, lon = ev.longitude, acc = ev.accuracy,
			sp = ev.speed;
		var latlon = lat + "," + lon +":"+ acc + "<br />";
		if (sp){
			var kmh = parseFloat(sp*3.6).toFixed(4); //60*60/1000;
			latlon+= "@  "+kmh +"Kmh (" +sp +"M/s)" ;
		}
		mapp.m(latlon);
		mapp.v.cpos = [lat, lon, acc];
		//mapp.showPosition(pos);//actually thanks to a good design i dont need this
	},
	doPostIssue: function doPostIssue(params){//i hate async stuff
		$.ajax({
			type: "POST",
			url: "add.php",
			data: params,
			dataType: "json",
			success: function(data, textStatus, jqXHR) {
				mapp.m( data.ok? "Ok": data.msg);
				mapp.v.map.panTo({lat: mapp.v.cfpos[0], lng:mapp.v.cfpos[1]});
				mapp.update(null, true);
		}});
	},
	addIssue:function addIssue(){ //when the "add issue form" is accepted
		$("#frmAdd").modal('hide');
		var lat = mapp.v.cfpos[0];
		var lon = mapp.v.cfpos[1];
		var acc = mapp.v.cfpos[2];
		
		var params = {
			lat: lat, lon: lon, acc:acc, 
			cat: $("#sel_cat").val(),
			descr: $("#i_descr").val()
		}
		if (mapp.v.photo_bin){
			var reader = new FileReader();
			reader.onloadend = function (){
				params['f64'] = reader.result;
				mapp.doPostIssue(params);
				//dataToBeSent = reader.result.split("base64,")[1];
				
				//$.post(url, {data:dataToBeSent});
			}
			reader.readAsDataURL($('#i_photo_bin')[0].files[0]);
		}else{
			params['f64'] = document.getElementById("canvas").toDataURL("image/jpeg", 0.9);
			mapp.doPostIssue(params);
		};
		/*
		$("#i_f64").val(document.getElementById("canvas").toDataURL("image/jpeg"));
		$("#i_lat").val(lat);
		$("#i_lon").val(lon);
		$("#i_acc").val(acc);
		var data = new FormData($("#frmData")[0]);
		data.append('photo', $('#i_photo_bin')[0].files[0]);
		$.ajax( {
			url: 'add.php',
			type: 'POST',
			data: data,
			processData: false,
			contentType: false
		} );
  */
		/* TODO send file using file input
		 * http://abandon.ie/notebook/simple-file-uploads-using-jquery-ajax
		 * http://portfolio.planetjon.ca/2014/01/26/submit-file-input-via-ajax-jquery-easy-way/
		 * to do this i need to add a hidden input f64 with the data from the canvas
		 *$( '#my-form' )
  .submit( function( e ) {
    $.ajax( {
      url: 'http://host.com/action/',
      type: 'POST',
      data: new FormData($("form-id")[0]),
      processData: false,
      contentType: false
    } );
    e.preventDefault();
  } );
  */
		/*$.ajax({
			type: "POST",
			url: "add.php",
			data: params,
			dataType: "json",
			success: function(data, textStatus, jqXHR) {
				mapp.m( data.ok? "Ok": data.msg);
				mapp.v.map.panTo({lat: lat, lng: lon});
				mapp.update();
		}});*/
	},
	onCatFilterChange: function onCatFilterChange(ev){
		mapp.v.cat_filter = parseInt($(this).val());
		mapp.update(null, true);
	},
	onFormShow: function onFormShow(ev){
		$("#b_fadd").hide();
		//mapp.v.cfpos = mapp.v.cpos;//dangerous! shared reference
		//var cp = mapp.v.cpos;
		//mapp.v.cfpos = [cp[0], cp[1], cp[2]];//store after the user take the picture
		mapp.setPict();
	},
	onFormHide: function onFormHide(ev){
		$("#b_fadd").show();
		mapp.closeCam();
	},
	showError:function showError(error) {
		switch(error.code) {
			case error.PERMISSION_DENIED:
				mapp.m("User denied the request for Geolocation.");
				break;
			case error.POSITION_UNAVAILABLE:
				mapp.m("Location information is unavailable.");
				break;
			case error.TIMEOUT:
				mapp.m("The request to get user location timed out.");
				break;
			case error.UNKNOWN_ERROR:
				mapp.m("An unknown error occurred.");
				break;
		}
	},
	canvas2Img: function canvas2Img(canvas){// Converts canvas to an image
		var image = new Image();
		image.src = canvas.toDataURL("image/png");
		return image;
	},
	isSamePlace: function isSamePlace(lat, lon, lat2, lon2){
		return parseFloat(lat).toFixed(mapp.v.prec) == parseFloat(lat2).toFixed(mapp.v.prec) &&
			parseFloat(lon).toFixed(mapp.v.prec) == parseFloat(lon2).toFixed(mapp.v.prec);
	},
	update: function update(ev, force){//  Updates UI's markers.
		// get map's bounds
		var bounds = mapp.v.map.getBounds();
		var ne = bounds.getNorthEast();
		var sw = bounds.getSouthWest();
		
		var params = {
			n: ne.lat, e:ne.lng,
			s: sw.lat, w:sw.lng,
			cat: mapp.v.cat_filter
		};
		
		console.log("about to update");
		if ((!force) && mapp.isSamePlace(params.n, params.e, mapp.v.lupos[0], mapp.v.lupos[1])){
			console.log("i just updated near that, wont update");
			return;
		}
		
		mapp.v.lupos = [params.n, params.e, params.s, params.w];
		
		$.getJSON("q.php", params)
			.done(function(data, textStatus, jqXHR) {// get places within bounds (asynchronously)
				// remove old markers from map
				//removeMarkers();
				if(data.ok){
					mapp.replaceMarkers(data.data);
				}else{
					mapp.m("Update error: "+ data.error);
				}
			})
			.fail(function(jqXHR, textStatus, errorThrown) {
				mapp.m("Update error: "+errorThrown.toString());
			});
		console.log("updated");
	},
	setPict: function setPict(){
		// Put event listeners into place
		// Grab elements, create settings, etc.
		var canvas = document.getElementById("canvas"),
			context = canvas.getContext("2d"),
			video = document.getElementById("video"),
			videoObj = { "video": true },
			errBack = function(error) {
				mapp.m("Video capture error: ", error.code); 
				$("#i_photo_bin").show();
				$("#video").hide();
				$("#snap").hide();
				mapp.v.photo_bin = true;
			};
		$("#video").show();
		//$("#snap").show();
		$("#snap").hide();
		$("#canvas").hide();
		$("#i_photo_bin").hide();
		mapp.v.photo_bin = false; /// restart maybe next time the camera will work
		// Put video listeners into place
		if(navigator.getUserMedia) { // Standard
			navigator.getUserMedia(videoObj, function(stream) {
				video.src = stream;
				video.play();
			}, errBack);
		} else if(navigator.webkitGetUserMedia) { // WebKit-prefixed
			navigator.webkitGetUserMedia(videoObj, function(stream){
				video.src = window.webkitURL.createObjectURL(stream);
				video.play();
			}, errBack);
		}
		else if(navigator.mozGetUserMedia) { // Firefox-prefixed
			navigator.mozGetUserMedia(videoObj, function(stream){
				video.src = window.URL.createObjectURL(stream);
				video.play();
			}, errBack);
		}else{
			errBack({code:-42});
			return;
		}
		
		var w=640, h=480;
		video.addEventListener("playing", function () {
			setTimeout(function () {
				console.log("Stream dimensions: " + video.videoWidth + "x" + video.videoHeight);
				w = video.videoWidth;
				h = video.videoHeight;
				canvas.width  = w;
				canvas.height = h;
			}, 500);
		});
		video.addEventListener('click', function(){
			//document.getElementById("snap").addEventListener("click", function() {
			//$("#canvas").show();
			context.drawImage(video, 0, 0, w, h);
			//turn off camera
			mapp.closeCam();
			mapp.storePos();
		});
	},
	closeCam: function closeCam(){
		video.pause();
		video.src = null;
		video.mozSrcObject = null;
		$("#video").hide();
		$("#snap").show();
		$("#snap").click(mapp.setPict);
		$("#i_descr").focus();
	},
	onLoad: function onLoad(){ // execute when the DOM is fully loaded
		mapp.v.m = document.getElementById("msg");
		mapp.v.info = $("#mInfo");
		mapp.v.infocnt = $("#mInfoBody");
		mapp.v.infot = $("#mInfoTitle");
		mapp.v.msel = $("#mMSel");
		mapp.v.mselcnt = $("#mMSBody");
		
		$("#b_add").click(mapp.addIssue);
		
		mapp.v.follow = true;
		$("#b_follow").hide();
		$('#frmAdd').on('shown.bs.modal', mapp.onFormShow);
		$('#frmAdd').on('hidden.bs.modal', mapp.onFormHide);
		$('#sel_cat_filter').change(mapp.onCatFilterChange);
		$('#i_photo_bin')[0].onchange = mapp.storePos;
	
		mapp.v.map = L.map('map-canvas').setView([0, 0], 18); // http://leafletjs.com/
		// http://leafletjs.com/examples/mobile.html
		// https://github.com/leaflet-extras/leaflet-providers
		L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', { // http://www.openstreetmap.org/copyright
			attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
			maxZoom: 19, //20 is kinda too much
			reuseTiles: true //If true, all the tiles that are not visible after panning are placed in a reuse queue from which they will be fetched when new tiles become visible (as opposed to dynamically creating new ones). This will in theory keep memory usage low and eliminate the need for reserving new memory whenever a new tile is needed.
			
		}).addTo(mapp.v.map);
		
		var locopts = { // https://github.com/domoritz/leaflet-locatecontrol
			position: 'topleft',
			setView: 'untilPan',
			keepCurrentZoomLevel: true,
			strings: {
				title: "Show me where I am, yo!"
			}
		};
		
		L.control.locate(locopts).addTo(mapp.v.map).start();
		mapp.v.mg = L.markerClusterGroup();
		mapp.v.map.addLayer(mapp.v.mg);
		mapp.v.map.on('locationerror', function(e){ mapp.m( "GPS Error: "+ e.message) });
		mapp.v.map.on('moveend', mapp.update); //magic event handles drag, pan, zoom i love leaflet so far
		mapp.v.map.on('locationfound', mapp.posChanged);
		
		mapp.loadCategories();
	},
	addMarker: function addMarker(place){ // Adds marker for place to map.
		//var mark  = L.marker([parseFloat(place.lat), parseFloat(place.lon)]).addTo(mapp.v.map)
		var mopts = {
			clickable:true, draggable:false, //this are default, but..
			riseOnHover:true, title: "#"+place.idn + ": " + place.descr,
			icon: new L.NumberedDivIcon({number: '#'+place.idn, iconUrl:'s/ii/cats/'+place.cat+'.png'})
			/*icon: L.divIcon({
				className: 'label',
				html: "#"+place.idn,
				iconSize: [100, 40]
			})*/
		};
		var mark  = L.marker([parseFloat(place.lat), parseFloat(place.lon)], mopts);
			//.bindPopup("#"+place.idn + ": " + place.descr);
		mark.data = { 
			idn: parseInt(place.idn), descr:place.descr, score:parseInt(place.score),
			cat: parseInt(place.cat), state:parseInt(place.int)
		};
		mark.on("click", mapp.showInfo);
		mapp.v.mg.addLayer(mark);
		mapp.v.marks.push(mark);
	},
	replaceMarkers: function replaceMarkers (data){//Removes markers from map.
		//in theory js is single threaded so no race conditions here, i suppose
		
		/* this code is interesting only if we need to share the reference to the actual array, which we shouldnt anyway
		//var old = mapp.v.marks.slice();
		var old = mapp.v.marks.splice(0, mapp.v.marks.length);//splicing would keep the reference, yeah but probably will be slower
		mapp.v.oms.clearMarkers();
		mapp.v.marks = [];*/
		
		var old = mapp.v.marks; //faster (?)
		mapp.v.marks = [];
		
		var fi;
		for (var j = 0; j < data.length; j++){
			fi = -1;
			for (i= 0; i < old.length;i++){
				if (data[j].idn == old[i].idn){
					fi = i;
					break;
				}
			}
			if (fi<0){//not found 
				mapp.addMarker(data[j]);//adds to oms and markers and CREATES a new "Marker" (gugel data type)
			}else{
				mapp.v.marks.push(old[fi]);
				old.splice(fi, 1);
			}
		}
		for (i = 0; i < old.length;i++){
			//mapp.v.map.removeLayer(old[i]);
			mapp.v.mg.removeLayer(old[i]);
			//mapp.v.mc.removeMarker(old[i]);
			//old[i].setMap(null);//its a kind of maaaagic
		}
	},
	showInfo: function showInfo(ev){
		var mark = ev.target;
		var res = "";
		//res += '<h4>#'+mark.data.idn+'</h4>';
		res += '<img src="s/i/'+mark.data.idn+'.jpg" class="img-rounded img-responsive" alt="">';
		res += '<div><h4>'+mark.data.descr+'</h4></div>';
		var tit = "";
		tit += '<img src="s/ii/cats/'+mark.data.cat+'.png">';
		tit += "Evento <a href='#'>#"+mark.data.idn+" <span class='badge'>"+mark.data.score+'</span></a>';
		
		mapp.v.infot.html(tit);
		mapp.v.infocnt.html(res);
		mapp.v.info.modal();
	},
	loadCategories: function loadCategories(){
		$.ajax({
			type: "GET",
			url: "cats.php",
			//data: params,
			dataType: "json",
			success: function(data, textStatus, jqXHR) {
				var _cats = {}; //its an object, ids are not supposed to be sequential
				mapp.m( data.ok ? "Ok": data.msg);
				var h = "";
				if (data.ok) {
					data = data.data;
					for (var i = 0; i<data.length; i++){
						_cats [i] = data[i];
						h += '<option value="'+i+'">'+data[i]+'</option>';
					}
				}
				mapp.v.cats = _cats;
				$("#sel_cat").html(h);
				$("#sel_cat").val(0);
				h = '<option value="-1"> - Sin Filtro - </option>' +h;
				$("#sel_cat_filter").html(h);
				$("#sel_cat_filter").val(0);
			}
		});
	},
	storePos: function storePos(){
		var cp = mapp.v.cpos;
		mapp.v.cfpos = [cp[0], cp[1], cp[2]];
		mapp.m("Posición: "+mapp.v.cfpos);
	}
};

$(mapp.onLoad);