  slider=null;
  t=null;
  function makeSlider(object){
    if(slider!=object){destroySlider(slider);}
	clearTimeout(t);
	$(object).slider({
		orientation: 'horizontal',
		min: -3,
		max: 3,
		step: 1,
		create: function(event, ui) {
			var prefVal = $(object).attr("preference-value");
			if (prefVal != undefined) {
				$(object).slider("value", prefVal);
			}
		},
		slide: function(event, ui) {resetSlider(object);},
		start: function(event, ui) {resetSlider(object);},
		change: function(event, ui) {resetSlider(object);},
		stop: function(event, ui) { 
			if ($( object ).slider( "option", "value" )!="Object") {
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
  function resetSlider(object){
    clearTimeout(t);
  }
  
  function destroySlider(object){
	  if (!isNaN($(object).slider("value"))) {
		  $(object).attr("preference-value", $(object).slider("value"));
	  }
	  $(object).slider( "destroy" );
  }