//utils

var _ajcalls = { };

function isNone(a){
	return (typeof a === typeof undefined);
}


function _ajaxError(x, status, error) {
	if (x.status == 403 || x.status == 401 ) {
		alert("Sorry, your session has expired. Please login again to continue.");
		window.location.href = "../";
	}else if (x.status == 404){
		var f = this.url;
		log ('Couldnt get file: "'+f+'"');
		if (f in _ajcalls){
			var errfunc = _ajcalls[f];
			delete _ajcalls[f];
			try{
				errfunc(x, status, error);
			}catch(error){//like i care
				console.log('error calling error func for "'+f+'"');
			};
		}
	}else{
		var msg = "Error on ajax occurred: " + status + " Error: " + error;
		//alert(msg);
		console.log(msg);
	}
};

jQuery.ajaxSetup({
	error: _ajaxError,
	cache: mapp.v.cache_ajax
});

$.getScript2 = function(file, onsuccess, onerror){
	//Carefull not to call the same url twice...
	if (file in _ajcalls){
		log('file already requested... im not responsible');
	}
	_ajcalls[file] = onerror;
	$.getScript(file, function(response, status, xhr){
		delete _ajcalls[file];
		onsuccess(response, status, xhr);
	});
};

$.js = function(url, data, post, success, error){
	if (isNone(error)){
		error = function(json, suc, h){
			if (!(isNone(json) || isNone(json.success))){
				if(!json.success){//error logico
					mapp.m('('+h.status+') '+json.error);
					return;
				}
			}
			mapp.m(_iz.common.error + h.status);
		};
	}
	
	if (isNone(success)){ success = function(){}; }
	if (isNone(post)){ post = false; }
	
	function handleError(json, suc, h){
		if (h.status == 401 || h.status == 403){
			window.location.href = '../';//relogin
			return;
		}
		error(json, suc, h);
	}
	
	function onError(h, suc){
		handleError({}, suc, h);
	};
	
	function onSuccess(json, suc, h){
		if (h.status != 200){ //error interno //json puede ser invalido (si es que aca llegan los cods != 200
			handleError(json, suc, h);
			return;
		}else if (!json.success){
			handleError(json, suc, h);
			return;
		}
		success(json, suc, h);
	};
	
	$.ajax({
		//cache:false,
		dataType: "json",
		type : post?'POST':'GET',
		url: url,
		data: data,
		success: onSuccess,
		error: onError
	});
};

var _u = {
	section:{ name:'', pars:{} },
	sections : {
		'news':"news.php",
		"ranking":"../static/html/ranking.html",
		"trunk":"../static/html/trunk.html",
		"missions":"missions.php",
		"matchs":"../static/html/matchs.html",
		"market":"market.php",
		"shop":"shop.php",
		"tournament":"tournament.php",
		'profile':'../static/html/profile.html',
		'match_history':'../static/html/match_history.html',
		'forum_news':'../static/html/forum_news.html',
		'packets':'packets.php',
		'cart':'cart.php',
		fixture:'fixture.php'
	},
	showUser: function showuser(id){
		//TODO usar un dialogo lindo como en facebook
		_u.load('profile', {id:id});
	},
	loadProfile : function loadprofile(){
		//reload profile info
		$.json('../papi/game/user/', {}, false, function(j){
			_u.anim($('#perfil_marco'), function(o){
				var u = j.data;
				$('#profile_img').attr('src', "../static/img/gallery/avatars/avatar"+u.avatar+'.png');
				$('#button_credits').text(u.credits);
				$('#profile_ranking').text(u.rank);
			});
		});
	},
	jsonForm: function jsform(obj, callback){
		var o = $(obj);
		/*o.children('input').keydown(function(e) {
			if (e.keyCode == 13) {
				o.submit();
			}
		});*/
		o.submit(function (event){
			event.preventDefault();
			var o = $(this);
			var data = o.serialize();
			var url = o.attr('action');
			var m = o.attr('method') || 'get';
			//http://www.w3.org/TR/html401/interact/forms.html#h-17.3
			//default's get
			var post = (m.toLowerCase() == 'post');
			$.json(url, data, post, callback);
		});
		return void(0);
	},
	ts2date:function ts2date(ts){
		return new Date(ts*1000);
	},
	select:function select(o){
		o = $(o);
		o.parent().find('.selected').removeClass('selected');
		o.addClass('selected');
	},
	joinMatch: function joinmatch(key, tournament){
		if(isNone(tournament)){
			tournament = false;
		}
		var tor = tournament?'&torneo=torneo':'';
		SexyLightbox.display('funcs/getDeck.php?height=440&width=550'+tor+'&clave='+key);
	},
	rejoinMatch: function rejoin(key){
		var url = "funcs/startMatch.php?reconectar=true&clave="+key;
		_u._loadContent(url);
	},
	anim: function transition(obj, func){
		obj.fadeOut('fast', function() {
			func(obj);//no try catch on purpose
			obj.fadeIn('slow');
		});
	},
	loadTable: function loadTable(url, data, post, body, rowFunc){
		var o = $(body);
		//cant use anim cuz of javashit nested callbacks
		o.fadeOut('fast', function() {
			$.json(url, data, post, function(j){
				var list = j.data;
				var h = '';
				var ro = '<div';
				var dc = '</div>';
				var doo = '<div class="cell col';
				var doc = '">';
				for(var i = 0; i < list.length; i++){
					var row = rowFunc(list[i]);
					var cols = row[0];
					var keys = row[1];
					if (isNone(keys)){
						keys = {};
					}
					
					if (!('onclick' in keys)){
						keys['onclick'] = "_u.select(this)";
					}
					if (!('class' in keys)){
						keys['class'] = "row";
					}
					
					//row
					h+=ro;
					//rew keys
					for(var k in keys){
						h+= ' ' + k + '="'+keys[k]+'"';
					}
					
					h+='>';//closing
					
					//cols
					for (var j =0; j< cols.length; j++){ 
						h += doo+j+doc+cols[j]+dc;
					}
					//endrow
					h+=dc;
				}
				o.html(h);
				o.fadeIn('slow');
			});
		});
	},
	load:function load(section, params){
			/*TODO
		 * make index set the params on loadSection to fordward url params to script*/
		log("Loading section: "+section);
		 /*
		  * if(window.event is FALSE if the object is set
		  * isNone wont wor because the typeof is UNDEFINED
		  * the magical variable "event" works the same
		if(!isNone(window.event)){  //:((
			window.event.preventDefault();
		}*/
		 
		try{
			var ev = event || window.event;
			ev.preventDefault();
		}catch(ponele){
			//lol
		};
		if(isNone(params)){
			params = {};
		}
		_u.section = {name:section, pars:params};
		
		//_u._loadContent(_u.sections[section]+'?'+$.param(params)); NO!
		var o = $('#content');
		var url = _u.sections[section]+'?'+$.param(params);
		o.fadeOut('fast', function() {
			//al poner el load adentro del fade evitamos quirks
			//al poner el fade'primero damos un feedback visual de carga y evitamos que hagan macana al clickear mucho
			o.load(url, function( response, status, xhr ) {
				_u._loadTrThenCode(section);//racefuckingconditions this MUST BE after content has loaded, because of cache timming and stuff
				o.fadeIn('slow');
			});
		});
		return void(0);
	},
	_loadContent: function(url){
		//DONT USE UNLESS STRICTLY NECESSARY
		/* calls to this function dos not get stored on the history and doesnt load code nor translation
		mostly used for forms
		*/
		var o = $('#content');
		o.fadeOut('fast', function() {
			//al poner el load adentro del fade evitamos quirks
			//al poner el fade'primero damos un feedback visual de carga y evitamos que hagan macana al clickear mucho
			o.load(url, function( response, status, xhr ) {
				o.fadeIn('slow');
			});
		});
	},
	_loadTrThenCode: function loadtr(section, user_lang){
		if(isNone(user_lang)) { user_lang = true;}
		
		var url = '../static/js/i18n/';
		url += user_lang?_user.lang:'es';
		url += '/'+section+'.js';
		
		$.getScript2(
			url,
			function(script, status, jqxhr){
				log('loaded translation file for "'+section+'" '+ (user_lang?'WITH':'WITHOUT')+' current lang');
				_u._loadCode(section);
			},
			function(){
				log('error loading translation file for: "' + section+'" ' + (user_lang?'WITH':'WITHOUT')+' current lang');
				var same = (_user.lang == 'es');
				if (same){
					log('Failed but current language IS default language, translation doesnt exist');
				};
				if (user_lang && (!same)){
					_u._loadTrThenCode(section, false);
				}else{
					_u._loadCode(section);//if its the 2nd time it fails (user_lang and !same) tries to load the code anyway
				}
			}
		);
	},
	_loadCode: function loadcode(section){
		$.getScript2('../static/js/'+section+'.js', function(data, textStatus, jqxhr){
			log('loaded code for: '+section);
		}, function(){
			log('error loading code file for: "'+section+'"');
		});
	},
	setTitle:function (text){
		_u.anim($('#title'), function(o){
			o.text(text);
		});
	},
	tr: function tr(dict){
		/*
		 * param is:
		 { 
			section_name:{
				section_key1:obj_id1,
				section_key2:obj_id2,
			},
			section_name2:{
				section_key1:obj_id3,
				section_key2:obj_id4,
			}
		}*/
		for (var section in dict){
			var i = _iz[section];
			if ( isNone(i) ) {
				log ('couldnt apply translation for undefined section "'+ section+'"');
				continue;
			}
			var d = dict[section];
			for (var key in d){
				$(d[key]).text(i[key]);
			}
		}
	}
};
