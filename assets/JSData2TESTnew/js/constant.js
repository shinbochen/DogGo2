var STATE_CONNECTED = 0;
var STATE_CONNECTING = 1;
var STATE_DISCONNECTED =2;


var	antiLostIcon = 'css/image/antilost.png';
var antiBarkIcon = 'css/image/slient.png';
var sleepIcon = 'css/image/sleep.png';
var blankIcon = 'css/image/blank.png';

/*var btn1 = {
	items: [{id:'_btn_0', type:"checkbox", icon:'star', 	text:lang.shock, 			src:'css/image/shock.png',	name:'btop', 			value:'0' 		},
			{id:'_btn_1', type:"checkbox",			icon:'alert',		text:lang.vibration,src:'css/image/vibration.png', 	name:'btop',	value:'1' 	},
			{id:'_btn_2', type:"checkbox",			icon:'audio',		text:lang.voice, 		src:'css/image/voice.png',	name:'btop',		value:'2'		},
			{id:'_btn_3', type:"checkbox", 		icon:'forbidden',	text:lang.silent, 	src:'css/image/slient.png',	name:'btop',		value:'3' 		},
			{id:'_btn_4', type:"checkbox", 		icon:'lock',		text:lang.antilost, 	src:'css/image/antilost.png',	name:'btop',	value:'4'	}]
};*/
var btn1 = {
	items: [
			{id:'_btn_2', type:"checkbox",		icon:'audio',		text:lang.voice, 		src:'css/image/voice.png',	name:'btop',		value:'2'		},
			{id:'_btn_1', type:"checkbox",		icon:'alert',		text:lang.vibration,src:'css/image/vibration.png', 	name:'btop',	value:'1' 	},
			{id:'_btn_0', type:"checkbox", 		icon:'star', 	text:lang.shock, 			src:'css/image/shock.png',	name:'btop', 			value:'0' 		},
			{id:'_btn_3', type:"checkbox", 		icon:'forbidden',	text:lang.silent, 	src:'css/image/slient.png',	name:'btop',		value:'3' 		},
			{id:'_btn_4', type:"checkbox", 		icon:'lock',		text:lang.antilost, 	src:'css/image/antilost.png',	name:'btop',	value:'4'	},
			{id:'_btn_5', type:"checkbox", 		icon:'lock',		text:lang.sleep, 	src:'css/image/sleep.png',	name:'btop',	value:'5'	}]
};
var actionstring = [
	lang.shock,
	lang.vibration,
	lang.voice,
	lang.silent,
	lang.antilost,
	lang.sleep,
]


var voicetype = [
	{v:0, n:"20 khz"},
	{v:1, n:"18 khz"},
	{v:2, n:"16 khz"},
	{v:3, n:"14 khz"},
	{v:4, n:"12 khz"},
	{v:5, n:"10 khz"},
	{v:6, n:"8 khz"},
	{v:7, n:"6 khz"},
	{v:8, n:"4 khz"},
	{v:9, n:"2 khz"}
];

var musiclist = [
	{v:0, n:"music 1",path:"music/1.mp3"},
	{v:1, n:"music 2",path:"music/2.mp3"},
	{v:2, n:"music 3",path:"music/3.mp3"},
	{v:3, n:"music 4",path:"music/1.mp3"},
	{v:4, n:"music 5",path:"music/2.mp3"}
];


var batteryimglst = [
" ",
"css/image/battery1.png",
"css/image/battery2.png",
"css/image/battery3.png",
"css/image/battery4.png"
];

var signalimglst = [
"css/image/s0.png",
"css/image/s1.png",
"css/image/s2.png",
"css/image/s3.png",
"css/image/s4.png"
];

var footnav = {
	items:[	{ page: 'page1',  icon:'star', text: lang.traing },
	       	{ page: 'page2',  icon:'navigation',  text: lang.position } ,
	       	{ page: 'page3',  icon:'plus',  text: lang.title_bt }]
	       
};

