  slider=null;
  t=null;
  function makeSlider(object){
    if(slider!=object){destroySlider(slider);}
	clearTimeout(t);
	$(object).slider({
		orientation: 'horizontal',
		min: -2,
		max: 2,
		step: 1,
		create: function(event, ui) {
			var prefVal = $(object).attr("preference-value");
			if (prefVal != undefined) {
				$(object).slider("value", prefVal);
			}
		},
		slide: function(event, ui) {colorSlider(object); resetSlider(object);},
		start: function(event, ui) {resetSlider(object);},
		change: function(event, ui) {resetSlider(object);},
		stop: function(event, ui) { 
			if ($( object ).slider( "option", "value" )!="Object") {
				colorSlider(object);
				preferenceAction(object, $( object ).slider( "option", "value" ));
				destroySlider(object);
			}
		}
	});
	slider = object;
  }
  
  function removeSlider(object){
    t=setTimeout('destroySlider(\''+object+'\')', 500);
  }
  function resetSlider(object) {
	  clearTimeout(t);
  }
  function colorSlider(object) {
	  var color = "";
	  switch ($(object).slider("value")) {
	  	case -2: color = "#980000"; break;
	  	case -1: color = "#FF3030"; break;
	  	case 0: color = "#000"; break;
	  	case 1: color = "#9ED54C"; break;
	  	case 2: color = "#59A80F"; break;
	  	default: color = "";
	  }
	  if (color != "") {
		  $(object).css("color", color);
	  }
  }
  function destroySlider(object){
	  if (!isNaN($(object).slider("value"))) {
		  $(object).attr("preference-value", $(object).slider("value"));
	  }
	  $(object).slider( "destroy" );
  }