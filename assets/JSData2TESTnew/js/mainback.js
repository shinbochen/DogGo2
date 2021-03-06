/////////////////////////////////////////////////////////////////////////
// global variable declare
//var	gApp = null;
var	gApp = {
	
	ios		  :true,
	allData   :[{
					name: 'ray',
					address: 'addr1',
					password: '1234',
					rssi:-53,
					state:0,
					battery:1,
					mode:0xc0,
					shockLvl:40,
					vibrationLvl:40,
					voiceType:40,
					voiceLvl:40,slientMode:1, slientNoiseI:30, slientVibrationI:40, slientShockO:50, slientVibrationO:60,
					antimode:1,	antiLostMobileVib:1,	antiLostShockO:50,	antiLostVibrationO:45, antiLostVoiceLvlO:30,	antiLostVoiceTypeO:3,
					antiLostDistLvl:40,
					antiLostMusic:40
				},{
					name: 'tom',
					address: 'addr2',
					password: '1234',
					rssi:-53,
					state:0,
					battery:3300,
					mode:0x80,
					shockLvl:40,
					vibrationLvl:40,
					voiceType:40,
					voiceLvl:40,slientMode:1, slientNoiseI:30, slientVibrationI:40, slientShockO:50, slientVibrationO:60,
					antimode:2,	antiLostMobileVib:1,	antiLostShockO:50,	antiLostVibrationO:45, antiLostVoiceLvlO:30,	antiLostVoiceTypeO:3,
					antiLostDistLvl:40,
					antiLostMusic:40
				},{
					name: 'suke',
					address: 'addr3',
					password: '1234',
					rssi:-53,
					state:2,
					battery:2200,
					mode:0x01,
					shockLvl:40,
					vibrationLvl:40,
					voiceType:40,
					voiceLvl:40,slientMode:1, slientNoiseI:30, slientVibrationI:40, slientShockO:50, slientVibrationO:60,
					antimode:4,	antiLostMobileVib:1,	antiLostShockO:50,	antiLostVibrationO:45, antiLostVoiceLvlO:30,	antiLostVoiceTypeO:3,
					antiLostDistLvl:40,
					antiLostMusic:40
				},{
					name: 'jackey',
					address: 'addr4',
					password: '1234',
					rssi:-53,
					state:2,
					battery:2800,
					mode:0xC0,
					shockLvl:40,
					vibrationLvl:40,
					voiceType:40,
					voiceLvl:40,slientMode:1, slientNoiseI:30, slientVibrationI:40, slientShockO:50, slientVibrationO:60,
					antimode:8,	antiLostMobileVib:1,	antiLostShockO:50,	antiLostVibrationO:45, antiLostVoiceLvlO:30,	antiLostVoiceTypeO:3,
					antiLostDistLvl:40,
					antiLostMusic:40
				},{
					name: 'china',
					address: 'addr5',
					password: '1234',
					rssi:-53,
					state:3,
					battery:3000,
					mode:0xC0,
					shockLvl:40,
					vibrationLvl:40,
					voiceType:40,
					voiceLvl:40,slientMode:1, slientNoiseI:30, slientVibrationI:40, slientShockO:50, slientVibrationO:60,
					antimode:1,	antiLostMobileVib:1,	antiLostShockO:50,	antiLostVibrationO:45, antiLostVoiceLvlO:30,	antiLostVoiceTypeO:3,
					antiLostDistLvl:40,
					antiLostMusic:40
				}],
	getSetting: function(addr, action){ return{val:23,val2:45}},
	getDeviceDataAll: function(){ 
		return this.allData;	
	},
	getDeviceData: function( address ){ 
		
		var s;
		this.allData.forEach(function( d ){
			if( d.address == address ){
				s = d;
			}
		});
		return s;	
	},
	editDevice 	: function( address, name, psd ){
		
	},
	
	toggleConnect: function(){},
	action		 : function(action, addr){ onDeviceDataRead(addr,action) },
	
	changeShock : function(){},
	changeVibration: function(){},
	changeVoiceLvl: function(){},
	changeVoiceType: function(){},
	changeSlientOfVibration: function(){},
	changeSlientOfNoise: function(){},
	changeAntiLostDistance: function(){},
	scan									: function(){ alert('scan');},
	setToAntiLostMode:  function( flag ){},
	
	setSlientVibration:  function( flag ){},
	setSlientVibration:  function( flag ){},
	changeSlientNoiseI:  function( flag ){},
	changeSlientVibrationI:  function( flag ){},
	changeSlientShockO:  function( flag ){},
	changeslientVibrationO:  function( flag ){},
	getMusicList: function( ){
		return musiclist;
	},
	playMusic: function( i ){
		playmusic( i );
	},
	
	changeAntiLostMusic: function(){},
	setToMobileAttMode: function(address,  v ){
	},
	setToDogCtrMode: function( address,  v ){
	},
	setToMobileVibration: function( address,  v){
	},
	setToAntiLostShockCtrl: function( address,  v){
		
	},
	setSlientShock:function(){
	},
	setToAntiLostVibrationCtrl: function(address,  v){
	},
	setToAntiLostVoiceCtrl: function( address,  v){
	},
	changeAntiLostShockO: function( address,  v){
	},
	changeAntiLostVibrationO: function( address,  v){
	},
	changeAntiLostVoiceTypeO: function( address,  v){
	},
	changeAntiLostVoiceLvlO: function( address,  v){
	}
	
};
var gData= function(){
	var _self = this;
	
	_self.actions = [];	// 0.1.2.3.4
	_self.selitems = [];
	_self.selitemn = [];
	
	_self.getLast = function(){
		if( _self.selitems.length > 0 ){
			return _self.selitems[_self.selitems.length-1];
		}
		else{
			return "";
		}		
	}
	_self.getLastAction = function(){
		if( _self.actions.length > 0 ){
			return _self.actions[_self.actions.length-1];
		}
		else{
			return -1;
		}		
	}
	_self.getSelect = function(){
		return _self.selitems.join("|||");
	}
	_self.getAction = function(){
		return _self.actions.join("|||");			
	}
	_self.select = function( s , s1, flag ){
		if( flag ){
			_self.selitems.push(s);
			_self.selitemn.push(s1);
		}
		else{			
			for( var i = 0; i < _self.selitems.length; i++ ){
				if( _self.selitems[i] == s ){
					_self.selitems.splice( i, 1);
					_self.selitemn.splice( i, 1);
				}
			}
		}
	}
	
	_self.isselect = function( ){
		return ( (_self.actions.length >= 0) && (_self.selitems.length > 0) );
	}
	_self.getActionString = function(){
		
		var s = '';
		for( var i = 0; i < _self.actions.length; i++ ){
			if( s.length > 0 ){
				s += ',';
			}
			s += actionstring[_self.actions[i]];
		}
		
		s += ':'+_self.selitemn.join(",");
		return s;
	}
	_self.setAction = function( s, flag ){
		
		if( flag ){
		
			if( s < 3 ){
				for( var i = 0; i < _self.actions.length; i++ ){
					if( _self.actions[i] >= 3 ){
						_self.actions.length = 0;
						break;
					}
				}
			}
			else{
				//for( var i = 0; i < _self.actions.length; i++ ){
				//	if( _self.actions[i] < 3 ){
						_self.actions.length = 0;
				//		break;
				//	}
				//}
			}
			_self.actions.push( s );
		}
		else{
			for( var i = 0; i < _self.actions.length; i++ ){
				if( _self.actions[i] == s ){
					_self.actions.splice( i, 1);	
					break;				
				}
			}
		}
	}
	return _self;
}();

// [[time,rssi],[time,rssi]]
var gRSSI = function(){
	
	var _self = this;
	
	_self.min = 0;
	_self.max = 100;
	_self.dataset = [ ];
	
	_self.add = function( addr, name, rssi ){		
		var t,flag= false;
		
		t = new Date();
		t = t.getTime() - t.getTimezoneOffset()*60*1000;
		
		var min = _self.min;
		var max = _self.max;	
		
		rssi = parseInt(rssi)*-1;
		
		rssi = Math.round( (rssi-min)/(max-min) *100 );
		
		
		_self.dataset.forEach( function( item ){
			
			if( item.addr == addr ){
				item.data.push([t,rssi]);
				flag = true;
			}			
		});
		
		if( flag == false ){
			var o = new Object();
			
			o.label = name;
			o.addr = addr;
			o.data = [];
			o.data.push([t,rssi]);
			_self.dataset.push( o );
		}
	};
	
	_self.getlen = function(){		
		return _self.dataset.length;
	}
	
	_self.getDataSet = function( ){
		return _self.dataset;
	};	
	return _self;	
}();


/////////////////////////////////////////////////////////////////////////
// function declare

function alertMsg( text ){
	$.mobile.loading('show', {
		text: text, 	
		textVisible:true,
		theme: 'b',    	
		textonly: true
	});	
	setTimeout( function(){$.mobile.loading('hide')}, 2000 );	
}
function time1s(){
	
	//onDeviceRSSIChange( "addr1", "tom", -48 );
	
	var p = $.mobile.activePage.attr( "id" );
	//alert( p );
	
}
var i = 0;
function test( ){
	/*if( i == 0 ){
		gApp.allData[0].state = 0;
		onDeviceConnected( 'addr1' );
		i = 1;
	}
	else{
		gApp.allData[1].state = 0;
		onDeviceConnected( 'addr2' );
	}*/
	onDeviceDisConnected('addr1');
}
function getBatteryImg( b ){

	var		arr = [0,1000,2200,2800,3000];	
	
	for( var i in arr ){
		
		if( b <= arr[i] ){
			return batteryimglst[i];
		}
	}
	return batteryimglst[arr.length-1];
}

function drawLine( tag ){
		/*
    var d1 = [],d2=[],d3=[];
    for (var i = 0; i < 25; i++ ){
    	var t = new Date().getTime()+i*100;
			d1.push([t, Math.random()*100]);
			d2.push([t, Math.random()*100]);
			d3.push([t, Math.random()*100]);
    }

		var dataset = [
            { label: "suke", 	data:d1 },
            { label: "tom", 	data:d2 },            
            { label: "ddd", 	data:d3 }
        ];
		*/
		
		var p = $.mobile.activePage.attr( "id" );
		if( p == 'page2' ){
		
			var options = {
					xaxis:{ 
						mode: "time",
						timeformat: "%d/%m %H:%M"
					},
					yaxis: { 
						min: 0,
						max: 100
					},
	        grid: {
	            hoverable: true,
	            borderWidth: 1,
	            borderColor: "#633200",
	            backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
	        },
					legend: { position: "ne" }
			};		
			if( gRSSI.getlen() > 0 ){
				$.plot( $(tag), gRSSI.getDataSet(), options );    
			} 
		}
}

function bindConnectListClick( tag ){
	$( tag+' input').bind("click", function(){	
		var flag = $(this).attr('checked') == 'checked' ? true : false;
		gData.select( $(this).attr("addr"), $(this).attr("name"), flag );
		displayOption();		
	});
}
/*
function bindOptionChange( tag ){
	
	
	$( tag+' input').bind("change", function(){		
		var name = $(this).attr("name"), value = $(this).val();
		
		switch( gData.getLastAction() ){
			
			case 0:
				gApp.changeShock(gData.getLast(), value );
				break;
			case 1:
				gApp.changeVibration(gData.getLast(), value );
				break;
			case 2:
				if( name == 'a'){
					gApp.changeVoiceLvl(gData.getLast(), value );
				}
				else{
					gApp.changeVoiceType(gData.getLast(), value );					
				}
				break;
			case 3:
				if( name == 'a' ){
					gApp.changeSlientOfVibration(gData.getLast(), value );
				}
				else{
					gApp.changeSlientOfNoise(gData.getLast(), value );					
				}
				break;
			case 4:
				if( name == 'a'){
					gApp.changeAntiLostDistance(gData.getLast(), value );
				}
				else{
					gApp.changeAntiLostMusic(gData.getLast(), value );					
				}
				break;
			default:
				break;
		}
	});
}
	*/

function bindFunctionBtnClick(  ){
	
	$( "#btn_section input").bind( 'click', function(){			
		var flag = $(this).attr('checked') == 'checked' ? true : false;
		var value = parseInt( $(this).attr('value') );
		
		if( flag ){
			if( value < 3 ){
				$('#_btn_3').prop('checked', false).checkboxradio("refresh"); 
				$('#_btn_4').prop('checked', false).checkboxradio("refresh"); 
			}
			else if( value == 3) {
				$('#_btn_0').prop('checked', false).checkboxradio("refresh"); 
				$('#_btn_1').prop('checked', false).checkboxradio("refresh"); 
				$('#_btn_2').prop('checked', false).checkboxradio("refresh"); 
				$('#_btn_4').prop('checked', false).checkboxradio("refresh"); 
			}
			else{
				$('#_btn_0').prop('checked', false).checkboxradio("refresh"); 
				$('#_btn_1').prop('checked', false).checkboxradio("refresh"); 
				$('#_btn_2').prop('checked', false).checkboxradio("refresh"); 
				$('#_btn_3').prop('checked', false).checkboxradio("refresh"); 
			}
		}
		gData.setAction( value, flag );
		displayOption( );
	});
	
	$( '#btn_section2 a').bind( 'click', function(){
			alertMsg( gData.getActionString( ) );
			gApp.action( gData.getAction(), gData.getSelect() );		
	});
}

function bindDeviceListHrefClick( tag ){	
	
	
	$(tag+" li").bind("click",function(){  
		gApp.toggleConnect( $(this).attr('addr') );
	});             
	$(tag+" li").bind("taphold",function(){	
		//alert('device tapholdd');
		editDeviceDetail( $(this).attr('addr') );
		changePage( "#page_device_detail"  );
	}); 
	
	///$(tag+" [href=#]").bind("tap",function(){
	//	gApp.toggleConnect( $(this).attr('addr') );
	//});             
	//$(tag+" [href=#]").bind("taphold",function(){	
	//	editDeviceDetail( $(this).attr('addr') );
	//	changePage( "#page_device_detail"  );
	//}); 
}

function changePage( pageid ){
	$.mobile.changePage( pageid );
	//$.mobile.changePage( "#page_device_detail", { transition: "slideup"} );
}

// 修改名字及密码
function editDeviceDetail( addr ){
	
	var	tag = '#page_device_detail',data;	
	var s = gApp.getDeviceData( addr );
	if( typeof(s) == 'object' ){			
		data = s;
	}
	else{
		data = eval( '[' + s + ']' );
		data = data[0];
	}	
		
	$(tag+" h1").text(lang.edit);
	
	$(tag+" label:first").text( lang.name );
	$(tag+" label:last").text( lang.password );
	
	
	$(tag+" input:eq(0)").val( data.name );
	$(tag+" input:eq(1)").val( data.password );
	$(tag+" input:eq(2)").val( data.address );
	
	$(tag+" [href]").bind( "click", function(){
		
		var id = $(this).attr('id');		
		if( id == 'btn_edit_ok' ){					
		
			var name = $(tag+" input:eq(0)").val(),
				password = $(tag+" input:eq(1)").val(),
				address = $(tag+" input:eq(2)").val(); 		
			gApp.editDevice( address, name, password );
			updateDeviceName( address, name );
		}
	});
	
}

////////////////////////////////////////////////////////////////////
//
function onStopScanLe( ){	 
	$("#id_scan").removeAttr( "disabled" );		
}
function onStartScanLe( ){
	$("#id_scan").attr( "disabled", "true" );
}
////////////////////////////////////////////////////////////////////
// 设备连上时调用
function onDeviceConnected( addr  ){	
	//alert( 'onDeviceConnected1'+addr );
	var  data, s = gApp.getDeviceData( addr );
	if( typeof(s) == 'object' ){			
		data = s;
	}
	else{
		data = eval( '[' + s + ']' );
		data = data[0];
	}	
	buildConnectList( data, true );	
	
	deleteDeviceList( data );
	buildDeviceList( data, true );
}

// 设备断连时调用
function onDeviceDisConnected( addr ){
	
	//alert( 'onDeviceDisConnected1'+addr );
	var  data, s = gApp.getDeviceData( addr );
	if( typeof(s) == 'object' ){			
		data = s;
	}
	else{
		data = eval( '[' + s + ']' );
		data = data[0];
	}	
	deleteConnectList( data );
	
	deleteDeviceList( data );
	buildDeviceList( data, true );
	
	gData.select( data.address, false );
	displayOption( );
	alertMsg( data.name+':'+lang.disconnect);
}

// 设备断连时调用
function onDeviceRSSIChange( addr,name, rssi ){
	gRSSI.add( addr, name, parseInt(rssi) );
	drawLine("#placeholder");
}
// addr only one
// action maybe more and divid by |||
function onDeviceDataRead( addr, act ){
		
	updateConnectList( addr );
	
	
	var  data, s = gApp.getDeviceData( addr );
	if( typeof(s) == 'object' ){			
		data = s;
	}
	else{
		data = eval( '[' + s + ']' );
		data = data[0];
	}	
		
	var actions = act.split("|||"),str='';		
	
	for( var i in actions ){
		
		if( (actions[i] < actionstring.length)  && (actions[i] >= 0) ){
			if( str.length >0 ){
				str += ',';
			}
			str += actionstring[ actions[i]];
		}
	}
	
	if( str.length > 0 ){
		alertMsg( data.name+':'+str+lang.hadexec);
	}
	
}
// 
function onDeviceDataWrite( addr, data ){
	
}
// 如果以前有不在范围的要变成未链接
function onNewDeviceFind( addr ){
	
	
	///alert( 'onNewDeviceFind'+addr );
	var  data, s = gApp.getDeviceData( addr );
	if( typeof(s) == 'object' ){			
		data = s;
	}
	else{
		data = eval( '[' + s + ']' );
		data = data[0];
	}	
	deleteDeviceList( data );
	buildDeviceList( data, true );
	
	
}
function onAlarm( names ){
	
	var  tag='#alarm';	
	
	$(tag).find("h1").text( lang.antilost+lang.alarm);	
	$(tag).find("h3").text( names + ' ' + lang.alarmmsg);	
	$('#iknow').text( lang.iknow );
	
	// ------------
	// response	
	$(tag +" a").bind( 'click', function(){
		gApp.stopMusic( );		
		gApp.stopVibrate( );		
	} );
	
	changePage('#alarm');
	
}
function deleteConnectList( data ){	
	
	var tag = "#connected_section label[addr='"+data.address+"']";		
	$(tag).parent().remove();	
	
	
	//var tag = "#connected_section input[value='"+data.address+"']";		
	//$(tag).parent().remove();	
	
	
	//if( (data != null) && (data.length > 0) ){		
	//	data.forEach( function( d ){
	//		tag1 = tag+" input[value='"+d.address+"']";		
	//		$(tag1).parent().remove();	
	//	});		
	//}
}

function deleteDeviceList( data ){
	
	var tag = "#page_device_list li[addr='"+data.address+"']";		
	$(tag).remove();	
	
	//$('#page_device_list li').each(function(){
	//	var n = $(this).attr("addr");
	//	if( n.indexOf(data.address) != -1){
	// 		$(this).remove();
	//	}
	//});
	
	//if( (data != null) && (data.length > 0) ){		
	//	data.forEach( function( d ){	
	//		$(tag).each(function(){
	//			var id = $(this).attr("id");
	//			if( id.indexOf(d.address) != -1){
	//		 		$(this).remove();
	//			}
	//		});
	//	})
	//}
}
function updateDeviceName( address, name ){	
		
	var tag = "#page_device_list li[addr='"+address+"']";
	$(tag).find("h1").text(name);
	
	
	tag = "#connected_section1 label[addr='"+address+"']";
	$(tag).find("b").text(name);
	
	//$('#page_device_list li').each(function(){
	//	var n = $(this).attr("addr");		
	//	if( n.indexOf(address) != -1){
	//		$(this).find("h1").text(name);
	//	}
	//});	
	
	//$('#connected_section1 label').each(function(){
	//	var n = $(this).attr("addr");
	//	if( n.indexOf(address) != -1){
	//		$(this).find("b").text(name);
	//	}
	//});
	
}

///function buildDeviceListLine( datas ){
	// <li data-icon="${icon}" class="${cls}" addr="${address}"><a href="#"><h1>${name}</h1><p>${states}</p></a></li>
	
//	var s = '';	
//	datas.forEach( function( data ){
		
//		s += "<li data-icon='"+data.icon+"' class='"+data.cls+"' addr='"+data.address+"'><a  class ='ui-btn ui-btn-icon-right ui-icon-check' href='#'><h1>"+data.name+"</h1><p>"+data.states+"</p></a></li>";
//	});
	
//	return s;
//}

function buildDeviceList( datas, isAdded ){	
	
	var	tdata={items:[]},tag='',tpl= '#devicelsttmpl',d1=[],d2=[],d3=[],d4=[];		
	
	if( datas == null ){return}
	
	if( datas.length === undefined ){
		datas = [datas];
	}
	
	
	datas.forEach( function( d ){			
		switch( d.state ){
		case STATE_CONNECTED:
			d.cls = 'li_a';
			d.icon = 'check';
			d.states = lang.connected;
			d1.push( d );
			break;	
		case STATE_CONNECTING:
			d.cls = 'li_b';
			d.icon = 'recycle';
			d.states = lang.connecting;
			d2.push( d );
			break;	
		case STATE_DISCONNECTED:
		default:
			if( d.rssi == 0 ){
				d.cls = 'li_d';
				d.icon = 'info';
				d.states = lang.outofarea;
				d4.push( d );
			}
			else{
				d.cls = 'li_c';
				d.icon = 'arrow-r';
				d.states = lang.disconnected;
				d3.push( d );
			}
			break;	
		}
	});	
	
	if( d1.length > 0 ){
		tag='#page_device_list1';
		tdata.items = d1;
		if( !isAdded ){
			$(tag).empty();
		}	
		$(tpl).tmpl(tdata).appendTo(tag);
	}
	
	if( d2.length > 0 ){
		tag='#page_device_list2';
		tdata.items = d2;
		if( !isAdded ){
			$(tag).empty();
		}
		$(tpl).tmpl(tdata).appendTo(tag);
	}
	
	if( d3.length > 0 ){
		tag='#page_device_list3';
		tdata.items = d3;	
		if( !isAdded ){
			$(tag).empty();
		}
		$(tpl).tmpl(tdata).appendTo(tag);
	}
	
	if( d4.length > 0 ){
		tag='#page_device_list4';
		tdata.items = d4;	
		if( !isAdded ){
			$(tag).empty();
		}	
		$(tpl).tmpl(tdata).appendTo(tag);
	}
	if( d1.length > 0 || d2.length > 0 || d3.length > 0 || d4.length > 0 ){
		tag = "#page_device_list";
		bindDeviceListHrefClick( tag ); 
	}
	return;
}

function updateConnectList( addr ){
	
	
	var  data, s = gApp.getDeviceData( addr );
	if( typeof(s) == 'object' ){			
		data = s;
	}
	else{
		data = eval( '[' + s + ']' );
		data = data[0];
	}	
	
	
	var tag = "#connected_section label[addr='"+data.address+"']",
			n = Number(data.mode),
			states1 = "";
	
	$(tag).find("span").text("");
	
	if( n & 0x40 ){
		states1 = lang.silented;
	}
	if( n & 0x80 ){
		if( states1.length > 0 ){
			states1 += ',';
		}
		states1 += lang.antilosted;	
	}
	$(tag).find("span").text(states1);
	
	var src = getBatteryImg( data.battery );
	$(tag).find("img").attr('src', src );
	
}
function buildConnectList( datas, isAdded ){	  	
	var	tdata={items:[]},tag='#connected_section1',tpl='#connectlsttmpl',n;		
	var id = 0;	
	
	if( datas == null ){return}
	
	if( datas.length === undefined ){
		datas = [datas];
	}
	
	datas.forEach( function( d ){	
		
		id = d.address.replace(/:/g,"_");
		switch( d.state ){
		case STATE_CONNECTED:
			d.id = '_id_conlst_'+id;			
			n = Number(d.mode);
			if( n & 0x40 ){
				d.states1 = lang.silented;
			}
			if( n & 0x80 ){
				if( d.states1 == null ){
					d.states1 = lang.antilosted;
				}
				else{
					d.states1 += ','+lang.antilosted;	
				}
			}
			d.batteryimg = getBatteryImg( d.battery);
			tdata.items.push( d );
			break;	
		default:
			break;	
		}
	});	
	if( tdata.items.length > 0 ){
		if( !isAdded ){
			$(tag).empty();
		}
		$(tpl).tmpl(tdata).appendTo(tag);
		bindConnectListClick( tag );  
		$(tag).trigger( 'create' );	  
	}
	//alert( $('#connected_section').html()	 );
	return;
}

function initView( ){
	var data = eval( gApp.getDeviceDataAll() );
	buildDeviceList( data, false );
	buildConnectList( data, false );
}

function enableBtnSection( flag ){
	if( !flag ){		
		$("div#btn_section input").attr( "disabled", "true" );
	}
	else{
		$("div#btn_section input").removeAttr( "disabled" );		
	}
	$('#btn_section').trigger('create');	
}
function showOptionSection( flag ){
	if( !flag ){		
		$("#option_section").slideUp( "slow" );
	}
	else{
		$("#option_section").slideDown( "slow");		
	}	
}
/*
function displayOption( ){
	
	if( !gData.isselect( ) ){
		showOptionSection( false );
	}	
	else{		
		var data, tag = "#option1", tag2="#option2", tmpl="#optiontmpl";
		var s = gApp.getDeviceData( gData.getLast() );
		if( typeof(s) == 'object' ){			
			data = s;
		}
		else{
			data = eval( '[' + s + ']' );
			data = data[0];
		}	
		
		$(tag).empty( );
		$(tag2).empty( );
		$(tag).hide();
		$(tag2).hide();
		
		var tdata =  {id:'_id_op1', value:0, min:0, max:100 };		
		var tdata2 = {id:'_id_op2', value:0, min:0, max:100 };
		var flag=0;
				

		tdata.label= tdata2.label = data.name + ':';
		
		switch( gData.getLastAction() ){
		case 0:	//shock		
			tdata.label += lang.shock+lang.level;
			tdata.value = parseInt(data.shockLvl);
			tdata.name = 'a';
			$(tmpl).tmpl(tdata).appendTo(tag);
			break;
		case 1: //vibration		
			tdata.label += lang.vibration+lang.level;
			tdata.value = parseInt(data.vibrationLvl);
			tdata.name = 'a';
			$(tmpl).tmpl(tdata).appendTo(tag);
			break;
		case 2:	//voice
			tdata.label += lang.voice+lang.level;
			tdata.value = parseInt(data.voiceLvl);
			tdata.name = 'a';
			
			$(tmpl).tmpl(tdata).appendTo(tag);
			
			//tdata2.value = parseInt(data.voiceType);
			break;
		case 3: //slient
			tdata.label += lang.vibration+lang.level;
			tdata.value = parseInt(data.slientOfVoice);
			tdata.name = 'a';
			
			tdata2.label += lang.noise+lang.level;
			tdata2.value = parseInt(data.slientOfVibration);			
			tdata2.name = 'b';
			
			$(tmpl).tmpl(tdata).appendTo(tag);
			$(tmpl).tmpl(tdata2).appendTo(tag2);	
			flag = 1;
			break;
		case 4: //anti lost		
			tdata.label += lang.antilost+lang.distance;
			tdata.value = parseInt(data.antiLostDistLvl);
			tdata.name = 'a';
			
			//tdata2.value = parseInt(data.antiLostMusic);
			$(tmpl).tmpl(tdata).appendTo(tag);
			break;	
		default:
			break;
		}
		$("#option_section").trigger('create');
		$(tag).fadeIn("slow");
		if( flag ){
			$(tag2).fadeIn("slow");			
		}
		bindOptionChange( "#option_section" );
		showOptionSection( true );		
	}
}*/

function displayOption0( data ){
	var tag = '#option_0';
	// init
	$(tag).find("h3").text(data.name+':'+lang.shock+lang.level);	
	// setup
	$(tag+" input:first").val( parseInt(data.shockLvl) ).slider("refresh");
	//response
	$(tag+' input:first').bind('change', function(){
		gApp.changeShock(gData.getLast(), $(this).val() );
	});
	$(tag).fadeIn('slow');
}

function displayOption1( data ){
	var tag = '#option_1';
	// init
	$(tag).find("h3").text(data.name+':'+lang.vibration+lang.level);	
	// setup
	$(tag+" input:first").val( parseInt(data.vibrationLvl) ).slider("refresh");
	// response
	$(tag+' input:first').bind('change', function(){
		gApp.changeVibration(gData.getLast(), $(this).val() );
	});
	$(tag).fadeIn('slow');
}

	
function displayOption2( data ){
	var tag = '#option_2';
	// init
	$(tag).find("h3").text(data.name+':'+lang.voice+lang.level);
		
	var s = $(tag+" select").html().trim();		
	if( s.length == 0 ){
		voicetype.forEach( function(v){
			 $(tag+" select").append("<option value='"+v.v+"'>"+v.n+"</option>");
		});
	}
	
	// setup	
	$(tag+" input:first").val( parseInt(data.voiceLvl) ).slider("refresh");
	$(tag+" select").val( data.voiceType ).selectmenu("refresh");	
	
	// response
	$(tag+' input:first').bind('change', function(){
		gApp.changeVoiceLvl(gData.getLast(), $(this).val() );
	});	
	$(tag+' select').bind('change', function(){
		gApp.changeVoiceType(gData.getLast(), $(this).val() );		
	});	
	$(tag).fadeIn('slow');
}


function displayOption3( data ){
	
	// init
	var tag = '#option_3';
	$(tag).find("h3").text( data.name+':'+lang.silent+lang.setup);	
	$(tag).find("h4:eq(0)").find("a").text( lang.silent+lang.condition);	
	$(tag).find("h4:eq(1)").find("a").text( lang.silent+lang.stimulus);	
	
	//$('#option_3_btn1').parent().find("label").text(  lang.shock );
	//$('#option_3_btn2').parent().find("label").text(  lang.vibration );
	
	$('#op_label30').text( lang.noise + lang.sensor);
	$('#op_label31').text( lang.vibration + lang.sensor );
	$('#op_label32').text( lang.shock + lang.level );
	$('#op_label33').text( lang.vibration + lang.level );
	
	function show1( flag ){
			if( flag ){
				$('#op_vid_32').parent().show('slow');
				$('#op_label32').parent().show('slow');
			}
			else{
				$('#op_vid_32').parent().hide('slow');
				$('#op_label32').parent().hide('slow');
			}
	}
	
	function show2( flag ){
			if( flag ){
				$('#op_vid_33').parent().show('slow');
				$('#op_label33').parent().show('slow');
				
			}
			else{
				$('#op_vid_33').parent().hide('slow');
				$('#op_label33').parent().hide('slow');
			}
	}
	
	// setup
	
	$('#op_vid_30').val( parseInt(data.slientNoiseI) ).slider("refresh");
	$('#op_vid_31').val( parseInt(data.slientVibrationI) ).slider("refresh");
	$('#op_vid_32').val( parseInt(data.slientShockO) ).slider("refresh");
	$('#op_vid_33').val( parseInt(data.slientVibrationO) ).slider("refresh");	
	var m = parseInt(data.slientMode);	
	if( (m&0x01) > 0 ){
		$('#option_3_btn1').prop('checked', true).checkboxradio("refresh"); 		
		show1( true );
	}
	else{
		$('#option_3_btn1').prop('checked', false).checkboxradio("refresh"); 		
		show1( false );	
	}
	
	if( (m&0x02) > 0 ){
		$('#option_3_btn2').prop('checked', true).checkboxradio("refresh"); 			
		show2( true );			
	}
	else{
		$('#option_3_btn2').prop('checked', false).checkboxradio("refresh"); 				
		show2( false );		
	} 
	
	$('#option_3_btn1').bind( "click", function(){		
			var flag = $(this).attr('checked') == 'checked' ? true : false;
			show1( flag );
			if( flag ){
				gApp.setSlientShock( gData.getLast(),1 );
			}
			else{
				gApp.setSlientShock( gData.getLast(),0 );
			}
	});
	$('#option_3_btn2').bind( "click", function(){
			var flag = $(this).attr('checked') == 'checked' ? true : false;
			show2( flag );
			if( flag ){
				gApp.setSlientVibration( gData.getLast(),1 );
			}
			else{
				gApp.setSlientVibration( gData.getLast(),0 );
			}
	});
	
	
	$('#op_vid_30').bind("change", function(){
		gApp.changeSlientNoiseI(gData.getLast(), $(this).val() );
	});
	$('#op_vid_31').bind("change", function(){
		gApp.changeSlientVibrationI(gData.getLast(), $(this).val() );
	});
	$('#op_vid_32').bind("change", function(){
		gApp.changeSlientShockO(gData.getLast(), $(this).val() );
	});
	$('#op_vid_33').bind("change", function(){
		gApp.changeslientVibrationO(gData.getLast(), $(this).val() );		
	});
	
	$(tag).fadeIn('slow');
}

function displayOption4( data ){
	
	
	// inital being
	var tag = '#option_4';
	$(tag).find("h3").text(data.name+':'+lang.antilost+lang.setup);	
	$(tag).find("h4:eq(0)").find("a").text( lang.mobileattention );	
	$(tag).find("h4:eq(1)").find("a").text( lang.antilost+lang.stimulus);	
		
	
	
	//$('#option_4_btn1').parent().find("label").text(  lang.shock );
	//$('#option_4_btn2').parent().find("label").text(  lang.vibration );
	//$('#option_4_btn3').parent().find("label").text(  lang.voice );
	
	$('#op_label4distance').text( lang.antilostdistance );
	$('#op_label4vibrate').text(  lang.vibration );
	$('#op_label41').text( lang.shock + lang.level );
	$('#op_label42').text( lang.vibration + lang.level );
	$('#op_label43a').text( lang.voice + lang.type );
	$('#op_label43b').text( lang.voice + lang.level );
	
	
	var s = $("#op4_musiclst").html().trim();		
	if( s.length == 0 ){
		
		var musiclist = eval( gApp.getMusicList( ) );
		musiclist.forEach( function(v){
			 $("#op4_musiclst").append("<option value='"+v.v+"'>"+v.n+"</option>");
		});
	}	
	
	
	s = $("#op4_voicelst").html().trim();		
	if( s.length == 0 ){
		voicetype.forEach( function(v){
			 $('#op4_voicelst').append("<option value='"+v.v+"'>"+v.n+"</option>");
		});
	}
	
	// inital end
	
	// setup begin
	$("#op4_distance").val( parseInt(data.antiLostDistLvl) ).slider("refresh");
	if( parseInt( data.antiLostMobileVib ) == 1 ){
		$("#op4_vibrate").val("on").slider("refresh");
	}else{	
		$("#op4_vibrate").val("off").slider("refresh");
	}
	$("#op4_musiclst").val( data.antiLostMusic ).selectmenu("refresh");	
		
	$('#op_vid_41').val( parseInt(data.antiLostShockO) ).slider("refresh");
	$('#op_vid_42').val( parseInt(data.antiLostVibrationO) ).slider("refresh");	
	$('#op_vid_43b').val( parseInt(data.antiLostVoiceLvlO) ).slider("refresh");	
	$("#op4_voicelst").val( parseInt(data.antiLostVoiceTypeO) ).selectmenu("refresh");	
	
	var n = Number(data.mode), n1=Number(data.antimode);
	
	// mobile attention
	if( n1 & 0x01 ){
		$("#op42_div").show("slow");
		$("#op4_onoff2").val("on").slider("refresh");			
	}else{
		$("#op42_div").hide("slow");
		$("#op4_onoff2").val("off").slider("refresh");			
	}
	// device control
	if( n1 & 0x02 ){
		$("#op43_div").show("slow");		
		$("#op4_onoff3").val("on").slider("refresh");				
	}
	else{
		$("#op43_div").hide("slow");			
		$("#op4_onoff3").val("off").slider("refresh");							
	}
	
	// shock
	if( n1 &0x04 ){
		$('#option_4_btn1').prop('checked', true).checkboxradio("refresh"); 		
		$("#op431_div").show("slow");	
	}
	else{
		$('#option_4_btn1').prop('checked', false).checkboxradio("refresh"); 		
		$("#op431_div").hide("slow");	
	}
	// vibration
	if( n1 &0x08 ){
		$('#option_4_btn2').prop('checked', true).checkboxradio("refresh"); 		
		$("#op432_div").show("slow");	
	}
	else{
		$('#option_4_btn2').prop('checked', false).checkboxradio("refresh"); 		
		$("#op432_div").hide("slow");	
	}
	// voice
	if( n1 &0x10 ){
		$('#option_4_btn3').prop('checked', true).checkboxradio("refresh"); 		
		$("#op433a_div").show("slow");	
		$("#op433b_div").show("slow");	
	}
	else{
		$('#option_4_btn3').prop('checked', false).checkboxradio("refresh"); 		
		$("#op433a_div").hide("slow");	
		$("#op433b_div").hide("slow");	
	}
	
	if( n & 0x80 ){
		$("#op4_onoff").val("on").slider("refresh");				
		$("#op40_div").show("slow");	
		$("#op4_div").show("slow");
	}
	else{
		$("#op4_onoff").val("off").slider("refresh");				
		$("#op40_div").hide("slow");		
		$("#op4_div").hide("slow");		
	}	
	// setup end
	
	// response
	$("#op4_onoff").bind('change', function(){
		var v = $(this).val();
		if( v == "on" ){
			$("#op40_div").show("slow");
			$("#op4_div").show("slow");
			gApp.setToAntiLostMode( gData.getLast(), 1 );
		}
		else{
			$("#op40_div").hide("slow");
			$("#op4_div").hide("slow");		
			gApp.setToAntiLostMode( gData.getLast(), 0 );	
		}
		updateConnectList( gData.getLast() );
	});	
	
	$("#op4_onoff2").bind('change', function(){
		var v = $(this).val();
		if( v == "on" ){
			$("#op42_div").show("slow");
			gApp.setToMobileAttMode( gData.getLast(), 1 );
		}
		else{
			$("#op42_div").hide("slow");		
			gApp.setToMobileAttMode( gData.getLast(), 0 );	
		}
	});	
	
	$("#op4_onoff3").bind('change', function(){
		var v = $(this).val();
		if( v == "on" ){
			$("#op43_div").show("slow");
			gApp.setToDogCtrMode( gData.getLast(), 1 );
		}
		else{
			$("#op43_div").hide("slow");		
			gApp.setToDogCtrMode( gData.getLast(), 0 );	
		}
	});	
	
	$("#op4_distance").bind('change', function(){
		gApp.changeAntiLostDistance(gData.getLast(), $(this).val() );
	});
	$("#op4_vibrate").bind('change',function(){
		var v = $(this).val();
		if( v == "on" ){
			gApp.setToMobileVibration( gData.getLast( ), 1 );
		}
		else{
			gApp.setToMobileVibration( gData.getLast( ), 0 );
		}		
	});
	
	$("#op4_musiclst").bind('change', function(){
		document.flag = 1;
		gApp.playMusic( $(this).val() );
		gApp.changeAntiLostMusic(gData.getLast(), $(this).val() );		
	});		
	
	
	$('#op_vid_41').bind("change", function(){
		gApp.changeAntiLostShockO(gData.getLast(), $(this).val() );
	});
	$('#op_vid_42').bind("change", function(){
		gApp.changeAntiLostVibrationO(gData.getLast(), $(this).val() );
	});
	
	$("#option_4_btn1").bind('change', function(){		
		var flag = $(this).attr('checked') == 'checked' ? true : false;
		if( flag ){		
			gApp.setToAntiLostShockCtrl(gData.getLast(), 1 );
			$("#op431_div").show("slow");	
		}
		else{
			gApp.setToAntiLostShockCtrl(gData.getLast(), 0 );
			$("#op431_div").hide("slow");	
		}
		
	});
	$("#option_4_btn2").bind('change', function(){
		var flag = $(this).attr('checked') == 'checked' ? true : false;
		if( flag ){
			gApp.setToAntiLostVibrationCtrl(gData.getLast(), 1 );
			$("#op432_div").show("slow");	
		}
		else{	
			gApp.setToAntiLostVibrationCtrl(gData.getLast(), 0 );
			$("#op432_div").hide("slow");	
		}
	});
	$("#option_4_btn3").bind('change', function(){
		var flag = $(this).attr('checked') == 'checked' ? true : false;
		if( flag ){
			gApp.setToAntiLostVoiceCtrl(gData.getLast(), 1 );
			$("#op433a_div").show("slow");	
			$("#op433b_div").show("slow");	
		}
		else{
			gApp.setToAntiLostVoiceCtrl(gData.getLast(), 0 );
			$("#op433a_div").hide("slow");	
			$("#op433b_div").hide("slow");	
		}
	});
	
	
	$('#op4_voicelst').bind("change", function(){
		gApp.changeAntiLostVoiceTypeO(gData.getLast(), $(this).val() );
	});
	$('#op_vid_43b').bind("change", function(){
		gApp.changeAntiLostVoiceLvlO(gData.getLast(), $(this).val() );		
	});
	
	$(tag).bind( "tap", function(){
		if( (document.flag != undefined) &&  (document.flag == 1) ){
			document.flag = 0;			
			gApp.stopMusic( );		
		}
	});
	
	$(tag).fadeIn('slow');
}

function displayOption( ){
	
	if( !gData.isselect( ) ){
		enableBtnSection( false );	
		showOptionSection( false );  
		$('#btn_section2').hide('slow');
	}	
	else{		
		$('#option_0').hide();
		$('#option_1').hide();
		$('#option_2').hide();
		$('#option_3').hide();
		$('#option_4').hide();	
		
		var data,s = gApp.getDeviceData( gData.getLast() );
		if( typeof(s) == 'object' ){			
			data = s;
		}
		else{
			data = eval( '[' + s + ']' );
			data = data[0];
		}	
		
		switch( gData.getLastAction() ){
		case 0:	//shock		
			displayOption0( data );
			break;
		case 1: //vibration		
			displayOption1( data );
			break;
		case 2:	//voice		
			displayOption2( data );
			break;
		case 3: //slient
			displayOption3( data );
			break;
		case 4: //anti lost		
			displayOption4( data );
			break;	
		default:
			break;			
		}
		enableBtnSection( true );
		showOptionSection( true );		
		var action = gData.getLastAction();
		if( (action == 4) || (action == -1) ){
			$('#btn_section2').hide('slow');
		}
		else{
			$('#btn_section2').show('slow');
		}
	}
}
function playmusic( n ){	
	var src = "";
	n = parseInt( n );
	alert( "playmusic" + n );
	musiclist.forEach( function( d ){
		if( d.v == n ){
			src = d.path;		
		}		
	});
	src ="http://113.12.81.52/pushfile/C/FA/FA2F0DB58636E7035F432685B3E43835?key=a05af3036f316c2513f79256c3e7ad74&tt=1430368000&st=j9-QfJR0Fr1qVHLTZpwbnA&filename=www.haoduoge.com.mp3";
	 
	alert( src );
	if( src.length > 0 ){
		$('#myaudio').attr('src', src );	
		document.getElementById("myaudio").addEventListener("canplaythrough", function () {
			this.play();
		}, false);	
	}
}

function stopmusic( ){
	document.getElementById("myaudio").pause();	
}

///////////////////////////////////////////////////////////////////////
// 
$(document).ready( function(){
	if( window.App){
		gApp = window.App;
	}
	$('#connected_title').text( lang.connected );	
	initView( );
	
	//setInterval("time1s()", 2000); 
});

$(document).on("pageinit","#page1",function(){	
	$('#foottmpl').tmpl(footnav).appendTo('#foot1');	
	$('#btntmpl').tmpl(btn1).appendTo('#btn_section');	
		
	$('#btn_section2 a:first').html("<img src='css/image/exec.png'></img>"+lang.exec);	
	$(this).find("h1:first").text( lang.title );
	
	enableBtnSection( false );	
	showOptionSection( false ); 
	$('#btn_section2').hide('slow'); 	
	
	bindFunctionBtnClick(  );
	
	$(this).trigger('create');
});


$(document).on("pageinit","#page2",function(){
	
	var tag = "#placeholder",h,w;
	
	h =$(window).height()-$('#foot1').height()-20,w=$(window).width();
	
	$('#foottmpl').tmpl(footnav).appendTo('#foot2');	
	$(tag).width( w+'px' );
	$(tag).height( h+'px' );
	$(this).trigger('create');
	drawLine(tag);
});
$(document).on("pageinit","#page3",function(){	
	$('#foottmpl').tmpl(footnav).appendTo('#foot3');	
	$('#id_scan').text(lang.scan );
	$('#id_scan').bind('click', function(){
		//alert('scan');
		//onStartScanLe();
		gApp.scan();
	});
	$(this).find("h1:first").text( lang.title_bt );
	$(this).trigger('create');
});
$(document).bind('mobileinit',function(){
	$.mobile.page.prototype.options.keepNative = "select, input.foo, textarea.bar ul";
});

$(document).bind("scrollstart", function(){
});


$(document).bind("swipeleft", function(){
	alert( "swipeleft" );
	var p = $.mobile.activePage.attr( "id" );
	if( p == 'page3' ){
		p = '#page2';
	}
	else if( p == 'page2' ){
		p = '#page1';		
	}
	else{
		p = '#page3';				
	}
	//onAlarm( 'addr1' );
	//changePage( p );
});

$(document).bind("swiperight", function(){
	alert( "swiperight" );
	var p = $.mobile.activePage.attr( "id" );
	if( p == 'page1' ){
		p = '#page2';
	}
	else if( p == 'page2' ){
		p = '#page3';		
	}
	else{
		p = '#page1';				
	}
	//onAlarm( 'addr1' );
	//changePage( p );
});