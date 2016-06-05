mapp.fb = {
	uid:-1,
	logSẗatus: function logSẗatus(response){
		if (response.status == "connected"){
			mapp.m("welcome to facebook");
			mapp.fb.uid = response.authResponse.userID;
			
			var p = {'fib':mapp.fb.uid, 'atk':response.authResponse.accessToken };
			$.ajax({
				type: "POST",
				url: "login.php",
				data: p,
				dataType: "json",
				success: function(data, textStatus, jqXHR) {
					console.log("login "+data);
					mapp.v.uid = data.data.uid;
					mapp.v.utk = data.data.utk;
					if (data.ok){
						mapp.m("Bienvenido a 3D");
					}else{
						mapp.m("Error al logearse. "+data.msg);
					}
				}
			});
		}else if (response.status="not_authorized"){
			mapp.m("authorize me plz");
		}else{
			mapp.m("error logging to facebook");
		}
	}
};



window.fbAsyncInit = function() {
  FB.init({
    appId      : '1081957648531079',
    cookie     : true,  // enable cookies to allow the server to access 
                        // the session
    xfbml      : true,  // parse social plugins on this page
    version    : 'v2.2' // use version 2.2
  });

  // Now that we've initialized the JavaScript SDK, we call 
  // FB.getLoginStatus().  This function gets the state of the
  // person visiting this page and can return one of three states to
  // the callback you provide.  They can be:
  //
  // 1. Logged into your app ('connected')
  // 2. Logged into Facebook, but not your app ('not_authorized')
  // 3. Not logged into Facebook and can't tell if they are logged into
  //    your app or not.
  //
  // These three cases are handled in the callback function.
  //FB.getLoginStatus(mapp.fb.logSẗatus);
  FB.getLoginStatus(mapp.fb.logSẗatus);
  
  FB.Event.subscribe('auth.statusChange', function(response) {
	console.log(response);
  // do something with response
	});
};

// Load the SDK asynchronously
(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) return;
	js = d.createElement(s); js.id = id;
	js.src = "//connect.facebook.net/en_US/sdk.js";
	fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk')
);

// Here we run a very simple test of the Graph API after login is
// successful.  See statusChangeCallback() for when this call is made.
function testAPI() {
	console.log('Welcome!  Fetching your information.... ');
	FB.api('/me', function(response) {
		console.log('Successful login for: ' + response.name);
		document.getElementById('status').innerHTML =
		'Thanks for logging in, ' + response.name + '!';
	});
}