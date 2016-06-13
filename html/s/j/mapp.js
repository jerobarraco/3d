//TODO dont open the camera until the user hits take setPict ???
var mapp = { }; //map app

mapp.v = { //values
	prec: 4, //4	0.0001	0° 00′ 0.36″	individual street, land parcel	11.132 m	10.247 m	7.871 m	4.3496 m
	// https://en.wikipedia.org/wiki/Decimal_degrees#Precision
	lupos: [0,0,0,0], //last update pos
	cpos: [0,0,0], //current pos (updated by posChanged)
	cfpos: [0,0,0], //current form pos (updated by onFormShow)
	marks: [], //markers
	photo_bin: false, //is photo input file?
	uid: 0,
	utk: 0,
	info_iid: -1,
	cache_ajax: true,
	cat_filter: -1,
	state_filter: -1,
	o :{//objects (from ui)
		m: 0, //message,
		mg: 0,//marker cluster
		map: 0, //map
		info: 0, //infowindo bootstrap
		infocnt: 0, //infocontent
		infot: 0, //info title
		i_descr: 0,
		b_login: 0, //fb login button
		b_fadd: 0, //button add on form
		b_i_del: 0, //button issue delete
		b_i_ok: 0, //button issue close
		video: 0, //video item
		snap: 0, //snap button
		sel_cat: 0, //new issue, categories selector
		sel_cat_filter: 0, //categories filter selector
		msel: 0, //?
		mselcnt: 0, //?
		
	},
};

mapp.m = function m(s){ //message function, very important
	mapp.v.o.m.innerHTML = s;
	console.log(s);
};

mapp.pos = { // gps related stuff
	changed: function pos_changed(ev){//updates current position
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
	error:function pos_error(error) {
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
	isSamePos: function pos_isSamePos(lat, lon, lat2, lon2){
		return parseFloat(lat).toFixed(mapp.v.prec) == parseFloat(lat2).toFixed(mapp.v.prec) &&
			parseFloat(lon).toFixed(mapp.v.prec) == parseFloat(lon2).toFixed(mapp.v.prec);
	},
	store: function pos_store(){
		var cp = mapp.v.cpos;
		mapp.v.cfpos = [cp[0], cp[1], cp[2]]; //avoid ref sharing //yeah copy might be useful rite?
		mapp.m("Posición: "+mapp.v.cfpos);
	}
};

mapp.ui = {//ui related stuff
	update: function ui_update(ev, force){//  Updates UI's markers.
		// get map's bounds
		var bounds = mapp.v.o.map.getBounds();
		var ne = bounds.getNorthEast();
		var sw = bounds.getSouthWest();
		
		var params = {
			n: ne.lat, e: ne.lng,
			s: sw.lat, w: sw.lng,
			uid: mapp.v.uid, 
			utk: mapp.v.utk,
			cat: mapp.v.cat_filter,
			state: mapp.v.state_filter
		};
		
		console.log("about to update");
		if ((!force) && mapp.pos.isSamePos(params.n, params.e, mapp.v.lupos[0], mapp.v.lupos[1])){
			console.log("i just updated near that, wont update");
			return;
		}
		
		mapp.v.lupos = [params.n, params.e, params.s, params.w];
		$.js("q.php", params, false, mapp.ui.replaceMarkers);
		console.log("updated");
		/*$.getJSON("q.php", params)
			.done(function(data, textStatus, jqXHR){ // get places within bounds (asynchronously)
				// remove old markers from map
				//removeMarkers();
				if(data.ok){
					mapp.ui.replaceMarkers(data.data);
				}else{
					mapp.m("Update error: "+ data.error);
				}
			})
			.fail(function(jqXHR, textStatus, errorThrown) {
				mapp.m("Update error: "+errorThrown.toString());
			});
		console.log("updated");*/
	},
	setPict: function ui_setPict(){
		// Put event listeners into place
		// Grab elements, create settings, etc.
		var canvas = document.getElementById("canvas"),
			context = canvas.getContext("2d"),
			video = document.getElementById("video"),
			videoObj = { "video": true },
			errBack = function(error) {
				mapp.m("Video capture error: ", error.code); 
				$("#i_photo_bin").show();
				mapp.v.o.video.hide();
				//$("#video").hide();
				mapp.v.o.snap.hide();
				//$("#snap").hide();
				mapp.v.photo_bin = true;
			};
		mapp.v.o.video.show();
		//$("#video").show();
		//$("#snap").show();
		//$("#snap").hide();
		mapp.v.o.snap.hide();
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
		video.addEventListener('click', function(){ //TODO check if this gets added too many times
			console.log("video click");
			context.drawImage(video, 0, 0, w, h);
			//turn off camera
			mapp.ui.closeCam();
			mapp.pos.store(); //the position is bounded to the picture, this is actually neat trick to get an accurate issue
		});
	},
	closeCam: function ui_closeCam(){
		//stop camera
		mapp.v.o.video[0].pause();
		mapp.v.o.video[0].src = null;
		mapp.v.o.video[0].mozSrcObject = null;
		
		//
		mapp.v.o.video.hide();
		//$("#video").hide();
		mapp.v.o.snap.show();
		//$("#snap").show();
		mapp.v.o.snap.click(mapp.ui.setPict);// TODO necesario poner aca?
		//$("#snap").click(mapp.ui.setPict);
		mapp.v.o.i_descr.focus();
		//$("#i_descr").focus();
	},
	addMarker: function ui_addMarker(place){ // Adds marker for place to map.
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
			cat: parseInt(place.cat), state:parseInt(place.state), mine:!!place.mine
		};
		mark.on("click", mapp.ui.showInfo);
		mapp.v.o.mg.addLayer(mark);
		mapp.v.marks.push(mark);
	},
	replaceMarkers: function ui_replaceMarkers (data){//Removes markers from map.
		//in theory js is single threaded so no race conditions here, i suppose
		
		/* this code is interesting only if we need to share the reference to the actual array, which we shouldnt anyway
		//var old = mapp.v.marks.slice();
		var old = mapp.v.marks.splice(0, mapp.v.marks.length);//splicing would keep the reference, yeah but probably will be slower
		mapp.v.oms.clearMarkers();
		mapp.v.marks = [];*/
		if (!isNone(data.data)){
			data = data.data; // to allow to be called directly as json callback
		}
		var old = mapp.v.marks; //faster (?)
		mapp.v.marks = [];
		
		var fi;
		for (var j = 0; j < data.length; j++){
			fi = -1;
			for (i= 0; i < old.length;i++){
				if (data[j].idn == old[i].data.idn){
					fi = i;
					break;
				}
			}
			if (fi<0){//not found 
				mapp.ui.addMarker(data[j]);//adds to oms and markers and CREATES a new "Marker" (gugel data type)
			}else{
				old[fi].data.mine = !!data[j].mine; //update the mine flag, because we can get unlogged at any time
				old[fi].data.state = data[j].state; //update the state flag (might happen after changing )
				mapp.v.marks.push(old[fi]);
				old.splice(fi, 1);
			}
		}
		//for (i = 0; i < old.length; i++){
		for(i in old){
			mapp.v.o.mg.removeLayer(old[i]);
		}
	},
	showInfo: function ui_showInfo(ev){
		var mark = ev.target;
		var res = "";
		//res += '<h4>#'+mark.data.idn+'</h4>';
		res += '<img src="s/i/'+mark.data.idn+'.jpg" class="img-rounded img-responsive" alt="">';
		res += '<div><h4>'+mark.data.descr+'</h4></div>';
		var tit = "";
		tit += '<img src="s/ii/cats/'+mark.data.cat+'.png" alt="'+mapp.v.cats[mark.data.cat]+'">';
		tit += "Evento <a href='#' id='info_iid' idn='"+mark.data.idn+"'>#"+mark.data.idn;
		tit += " <span class='badge'>"+mark.data.score+'</span></a>';
		
		mapp.v.info_iid = mark.data.idn;
		mapp.v.o.infot.html(tit);
		mapp.v.o.infocnt.html(res);
		mapp.v.o.b_i_del.prop('disabled', !mark.data.mine);//if its not mine, can't delete
		mapp.v.o.info.modal();
	},
	loadCategories: function ui_loadCategories(){
		$.js("cats.php", {}, false, function loadCategoriesOk(data){
			var _cats = {}; //its an object, ids are not supposed to be sequential
			data = data.data;
			var h = "";
			//for (var i = 0; i<data.length; i++){
			for (i in data){
				_cats[i] = data[i];
				h += '<option value="'+i+'">'+data[i]+'</option>';
			}
			mapp.v.cats = _cats;
			mapp.v.o.sel_cat.html(h);
			mapp.v.o.sel_cat.val(0);
			h = '<option value="-1"> - Sin Filtro - </option>' +h;
			mapp.v.o.sel_cat_filter.html(h);
			mapp.v.o.sel_cat_filter.val(0);
		});
	},
	loadStates: function ui_loadStates(){
		//TODO
		$.js("states.php", null, false, function (data){
			// TODO crear filtro  de estados
		});
	}
}; 

mapp.on = {//events
	catFilterChanged: function on_catFilterChanged(ev){
		mapp.v.cat_filter = parseInt($(this).val());
		mapp.ui.update(null, true);
	},
	formShow: function on_formShow(ev){
		//mapp.v.b_fadd.hide(); //maybe this is unnecesary because modal form is modal (shows a protective layer)
		//hiding and showing craps on the layout
		mapp.ui.setPict();
	},
	formHide: function on_formHide(ev){
		//mapp.v.b_fadd.show();
		mapp.ui.closeCam();
	},
	load: function on_load(){ // execute when the DOM is fully loaded
		mapp.v.follow = true;

		mapp.v.o.m = document.getElementById("msg");
		mapp.v.o.info = $("#mInfo");
		mapp.v.o.infocnt = $("#mInfoBody");
		mapp.v.o.infot = $("#mInfoTitle");
		mapp.v.o.msel = $("#mMSel");
		mapp.v.o.mselcnt = $("#mMSBody");
		mapp.v.o.i_descr = $("#i_descr");
		mapp.v.o.b_login = $("#b_login");
		mapp.v.o.b_i_del = $("#b_i_del");
		mapp.v.o.b_i_ok = $("#b_i_ok");
		mapp.v.o.b_fadd = $("#b_fadd");
		mapp.v.o.video = $("#video");
		mapp.v.o.snap = $("#snap");
		mapp.v.o.sel_cat = $("#sel_cat");
		mapp.v.o.sel_cat_filter = $("#sel_cat_filter");
		
		mapp.v.o.b_fadd.hide();
		$("#b_follow").hide();
		
		$('#frmAdd').on('shown.bs.modal', mapp.on.formShow);
		$('#frmAdd').on('hidden.bs.modal', mapp.on.formHide);
		$('#sel_cat_filter').change(mapp.on.catFilterChange);
		$('#i_photo_bin')[0].onchange = mapp.pos.store;
		$("#b_add").click(mapp.issue.add);
		mapp.v.o.b_i_del.click(mapp.issue.del);
		mapp.v.o.b_i_ok.click(mapp.issue.close);
	
		mapp.v.o.map = L.map('map-canvas').setView([0, 0], 17); // http://leafletjs.com/
		// http://leafletjs.com/examples/mobile.html
		// https://github.com/leaflet-extras/leaflet-providers
		L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', { // http://www.openstreetmap.org/copyright
			attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
			maxZoom: 20, //20 is kinda too much
			reuseTiles: true //If true, all the tiles that are not visible after panning are placed in a reuse queue from which they will be fetched when new tiles become visible (as opposed to dynamically creating new ones). This will in theory keep memory usage low and eliminate the need for reserving new memory whenever a new tile is needed.
		}).addTo(mapp.v.o.map);
		
		var locopts = { // https://github.com/domoritz/leaflet-locatecontrol
			position: 'topleft',
			setView: 'untilPan',
			keepCurrentZoomLevel: true,
			strings: {
				title: "Show me where I am, yo!"
			}
		};
		
		L.control.locate(locopts).addTo(mapp.v.o.map).start();
		mapp.v.o.mg = L.markerClusterGroup();
		mapp.v.o.map.addLayer(mapp.v.o.mg);
		//mapp.v.o.map.on('locationerror', function(e){ mapp.m( "GPS Error: "+ e.message) });
		mapp.v.o.map.on('locationerror', mapp.pos.error);
		mapp.v.o.map.on('moveend', mapp.ui.update); //magic event handles drag, pan, zoom i love leaflet so far
		mapp.v.o.map.on('locationfound', mapp.pos.changed);
		mapp.ui.loadCategories();
	}
};

mapp.issue = { //issues
	setState: function issue_setState(iid, stat, msg){
		var params = {
			uid: mapp.v.uid,
			utk: mapp.v.utk,
			iid: iid,
			status: stat
		};
		msg = mapp.u.get(msg, "Issue # "+iid+" nuevo estado " + stat);
		$.js("state.php", params, true, function(json){
			mapp.m(msg);
			mapp.update(null, true);
		});
	},
	doPost: function issue_doPost(params){ //actually makes the post
		//i hate async stuff
		params.uid = mapp.v.uid;
		params.utk = mapp.v.utk;
		$.js("add.php", params, true, function postIssue(data){
			mapp.m("Nuevo issue #"+data.data.iid+" creado");
			mapp.v.o.map.panTo({lat: mapp.v.cfpos[0], lng: mapp.v.cfpos[1]});
			mapp.ui.update(null, true);
		});
	},
	add:function issue_add(){ //when the "add issue form" is accepted
		$("#frmAdd").modal('hide');
		var lat = mapp.v.cfpos[0];
		var lon = mapp.v.cfpos[1];
		var acc = mapp.v.cfpos[2];
		
		var params = {
			lat: lat, lon: lon, acc: acc, 
			cat: mapp.v.o.sel_cat.val(),
			descr: mapp.v.o.i_descr.val()
		}
		if (mapp.v.photo_bin){
			var reader = new FileReader();
			reader.onloadend = function (){
				params['f64'] = reader.result;
				mapp.issue.doPost(params);
				//dataToBeSent = reader.result.split("base64,")[1];
				//$.post(url, {data:dataToBeSent});
			}
			reader.readAsDataURL($('#i_photo_bin')[0].files[0]);
		}else{
			params['f64'] = document.getElementById("canvas").toDataURL("image/jpeg", 0.9);
			mapp.issue.doPost(params);
		}
	},
	close: function issue_close(){
		mapp.issue.setState(mapp.v.info_iid, 1);
	},
	del: function issue_del(){
		var iid = mapp.v.info_iid;
		var params = {
			uid: mapp.v.uid,
			utk: mapp.v.utk,
			iid: iid
		};
		$.js("del.php", params, true, function(json){
			mapp.m("Issue #"+ iid+ " eliminado");
			mapp.ui.update(null, true);
		});
		//mapp.v.b_i_del.prop('disabled', true);
		mapp.v.o.info.modal("hide");
	},
};

$(mapp.on.load);