<html>
<head>

<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=geometry"></script>
</head>
<body onLoad="goma()">
<div id="mappy" style="width:900px; height:550px;"></div>

<script>
var map, ren, ser;
var d;
var n; 
var start;
var data = {};
function goma()
{
map = new google.maps.Map( document.getElementById('mappy'),
 {'zoom':6,
 'mapTypeId': google.maps.MapTypeId.ROADMAP,
 'center': new google.maps.LatLng(22.755920681486, 78.2666015625) })

	ren = new google.maps.DirectionsRenderer( {'draggable':true} );
	ren.setMap(map);
	ser = new google.maps.DirectionsService();
	
	fetchdata()
	
	var flightPlanCoordinates = [
	new google.maps.LatLng(16.50333165171276, 80.63132286071777)   ,
	new google.maps.LatLng(16.50942136660169, 80.61939239501953)   ,
	new google.maps.LatLng(16.509009905421724, 80.6107234954834)   ,
	new google.maps.LatLng(16.514194252281314, 80.60334205627441)  ,
	new google.maps.LatLng(16.519543035811243, 80.60239791870117)  ,
	new google.maps.LatLng(16.52974645824655, 80.5898666381836)    ,
	new google.maps.LatLng(16.558132174435467, 80.56737899780273)  ,
	new google.maps.LatLng(16.565207383430426, 80.55218696594238)  ,
	new google.maps.LatLng(16.572117801806716, 80.53424835205078)  ,
	new google.maps.LatLng(16.584703997933822, 80.52454948425293)  ,
	new google.maps.LatLng(16.588899213699037, 80.5213737487793)   ,
	new google.maps.LatLng(16.601484311876263, 80.5309009552002)   ,
	new google.maps.LatLng(16.626076339991933, 80.5433464050293)   ,
	new google.maps.LatLng(16.628296869727386, 80.55081367492676)  ,
	new google.maps.LatLng(16.631750976007943, 80.55373191833496)  ,
	new google.maps.LatLng(16.65272099988651, 80.56265830993652)   ,
	new google.maps.LatLng(16.677142016188974, 80.57287216186523)  ,
	new google.maps.LatLng(16.678868636740173, 80.57476043701172)  ,
	new google.maps.LatLng(16.689639108178543, 80.58445930480957)  ,
	new google.maps.LatLng(16.692187756893034, 80.59501647949219)  ,
	new google.maps.LatLng(16.694736371612272, 80.60188293457031)  ,
	new google.maps.LatLng(16.704683864961257, 80.61098098754883)  ,
	new google.maps.LatLng(16.71948082992962, 80.61982154846191)   ,
	new google.maps.LatLng(16.72391969563945, 80.62548637390137)   ,
	new google.maps.LatLng(16.75129041669962, 80.63870429992676)   ,
	new google.maps.LatLng(16.762960819049955, 80.64248085021973)  ,
	new google.maps.LatLng(16.770110641459027, 80.64153671264648)  ,
	new google.maps.LatLng(16.797885560225108, 80.63063621520996)  ,
	new google.maps.LatLng(16.817276289715583, 80.62625885009766)  ,
	new google.maps.LatLng(16.829599875165805, 80.6290054321289)   ,
	new google.maps.LatLng(16.836500732860216, 80.63235282897949)  ,
	new google.maps.LatLng(16.845126451281548, 80.63218116760254)  ,
	new google.maps.LatLng(16.85950177486932, 80.63544273376465)   ,
	new google.maps.LatLng(16.87362959934793, 80.63990592956543)   ,
	new google.maps.LatLng(16.91395379420299, 80.64393997192383)   ,
	new google.maps.LatLng(16.926024737211172, 80.63913345336914)  ,
	new google.maps.LatLng(16.947619119096455, 80.64428329467773)  ,
	new google.maps.LatLng(16.96346436938903, 80.64823150634766)   ,
	new google.maps.LatLng(16.97824111850635, 80.65303802490234)   ,
	new google.maps.LatLng(16.992360036189883, 80.65973281860352)  ,
	new google.maps.LatLng(17.0071345092729, 80.66247940063477)    ,
	new google.maps.LatLng(17.022318170180544, 80.66084861755371)  ,
	new google.maps.LatLng(17.031181575807338, 80.6480598449707)   ,
	new google.maps.LatLng(17.042096118603368, 80.63879013061523)  ,
	new google.maps.LatLng(17.0555537751158, 80.6370735168457)     ,
	new google.maps.LatLng(17.067943808760646, 80.62874794006348)  ,
	new google.maps.LatLng(17.077215282613974, 80.62308311462402)  ,
	new google.maps.LatLng(17.08394296623869, 80.62402725219727)   ,
	new google.maps.LatLng(17.0955107325946, 80.61604499816895)    ,
	new google.maps.LatLng(17.11068720890056, 80.61698913574219)   ,
	new google.maps.LatLng(17.125616372712916, 80.61595916748047)  ,
	new google.maps.LatLng(17.154240921268514, 80.62986373901367)  ,
	new google.maps.LatLng(17.18195905637421, 80.6422233581543)    ,
	new google.maps.LatLng(17.206393500256976, 80.66024780273438)  ,
	new google.maps.LatLng(17.200326187644396, 80.61630249023438)  ,
	new google.maps.LatLng(17.200162203461726, 80.56188583374023)  ,
	new google.maps.LatLng(17.196554514688234, 80.52206039428711)  ,
	new google.maps.LatLng(17.199342280369144, 80.4942512512207)   ,
	new google.maps.LatLng(17.219511333785114, 80.44979095458984)  ,
	new google.maps.LatLng(17.216723872015926, 80.42335510253906)  ,
	new google.maps.LatLng(17.192782764890712, 80.4030990600586)   ,
	new google.maps.LatLng(17.178679019857835, 80.38198471069336)  ,
	new google.maps.LatLng(17.194914632912802, 80.35863876342773)  ,
	new google.maps.LatLng(17.21393636822409, 80.31435012817383)   ,
	new google.maps.LatLng(17.230824717654645, 80.25701522827148)  ,
	new google.maps.LatLng(17.240170033989017, 80.2273178100586)   ,
	new google.maps.LatLng(17.243612926051476, 80.18903732299805)  ,
	new google.maps.LatLng(17.253449406654394, 80.16826629638672)  ,
	new google.maps.LatLng(17.261973932302723, 80.14921188354492)  ,
	new google.maps.LatLng(17.26672782352052, 80.12500762939453)   ,
	new google.maps.LatLng(17.25640024858061, 80.11882781982422)   ,
	new google.maps.LatLng(17.253285469607814, 80.10080337524414)  ,
	new google.maps.LatLng(17.26410500214549, 80.0877571105957)    ,
	new google.maps.LatLng(17.274596063712107, 80.05943298339844)  ,
	new google.maps.LatLng(17.290167631709522, 80.04467010498047)  ,
	new google.maps.LatLng(17.32114293842747, 80.01874923706055)   ,
	new google.maps.LatLng(17.320323553075223, 79.97119903564453)  ,
	new google.maps.LatLng(17.365548153696455, 79.9310302734375)   ,
	new google.maps.LatLng(17.380129070408806, 79.90047454833984)  ,
	new google.maps.LatLng(17.394381203596815, 79.8819351196289)   ,
	new google.maps.LatLng(17.448594680052686, 79.8208236694336)   ,
	new google.maps.LatLng(17.503610605411705, 79.75713729858398)  ,
	new google.maps.LatLng(17.545679999829797, 79.70495223999023)  ,
	new google.maps.LatLng(17.624063419234876, 79.60453033447266)  ,
	new google.maps.LatLng(17.652855460321106, 79.60487365722656)  ,
	new google.maps.LatLng(17.692109914190738, 79.60899353027344)  ,
	new google.maps.LatLng(17.732990849540247, 79.59835052490234)  ,
	new google.maps.LatLng(17.77811257088691, 79.58015441894531)   ,
	new google.maps.LatLng(17.83629622635646, 79.60556030273438)   ,
	new google.maps.LatLng(17.903608638609793, 79.595947265625)    ,
	new google.maps.LatLng(17.95391289081595, 79.6010971069336)    ,
	new google.maps.LatLng(17.95840364668414, 79.60118293762207)   ,
	new google.maps.LatLng(17.977181934922484, 79.59654808044434)  ,
	new google.maps.LatLng(17.99203986095381, 79.5915699005127)    ,
	new google.maps.LatLng(18.00354981167137, 79.57757949829102)   ,
	new google.maps.LatLng(18.008937190030505, 79.56075668334961)  ,
	new google.maps.LatLng(18.020201176238725, 79.54994201660156)  ,
	new google.maps.LatLng(18.070145820303544, 79.52007293701172)  ,
	new google.maps.LatLng(18.095928674182087, 79.44900512695312)  ,
	new google.maps.LatLng(18.224459888941304, 79.376220703125)    ,
	new google.maps.LatLng(18.27597624663713, 79.32060241699219)   ,
	new google.maps.LatLng(18.327151403632502, 79.26532745361328)  ,
	new google.maps.LatLng(18.381569510936767, 79.21194076538086)  ,
	new google.maps.LatLng(18.397370356748134, 79.18567657470703)  ,
	new google.maps.LatLng(18.404211510561133, 79.14499282836914)  ,
	new google.maps.LatLng(18.413006880384255, 79.13761138916016)  ,
	new google.maps.LatLng(18.436621852843622, 79.15460586547852)  ,
	new google.maps.LatLng(18.446066933638484, 79.16095733642578)  ,
	new google.maps.LatLng(18.461210548135874, 79.18619155883789)  ,
	new google.maps.LatLng(18.467397877137373, 79.19580459594727)  ,
	new google.maps.LatLng(18.50988902503296, 79.27871704101562)   ,
	new google.maps.LatLng(18.560669199216157, 79.35562133789062)  ,
	new google.maps.LatLng(18.621520393840992, 79.39132690429688)  ,
	new google.maps.LatLng(18.65177518608126, 79.39510345458984)   ,
	new google.maps.LatLng(18.72332418587802, 79.42840576171875)   ,
	new google.maps.LatLng(18.747383869213394, 79.45175170898438)  ,
	new google.maps.LatLng(18.76786441131958, 79.45449829101562)   ,
	new google.maps.LatLng(18.76103784020659, 79.51148986816406)   ,
	new google.maps.LatLng(18.79516793121411, 79.52075958251953)   ,
	new google.maps.LatLng(18.82604157672509, 79.52831268310547)   ,
	new google.maps.LatLng(18.84683746769596, 79.52762603759766)   ,
	new google.maps.LatLng(18.857884237121382, 79.47372436523438)  ,
	new google.maps.LatLng(18.87104193960541, 79.4447135925293)    ,
	new google.maps.LatLng(18.88939602131517, 79.44076538085938)   ,
	new google.maps.LatLng(18.90969686563054, 79.44488525390625)   ,
	new google.maps.LatLng(18.936977319164196, 79.45140838623047)  ,
	new google.maps.LatLng(18.966363773665115, 79.45681571960449)  ,
	new google.maps.LatLng(19.007918091778937, 79.47681427001953)  ,
	new google.maps.LatLng(19.111109635474076, 79.46617126464844)  ,
	new google.maps.LatLng(19.147438517185837, 79.44557189941406)  ,
	new google.maps.LatLng(19.21164321910282, 79.42325592041016)   ,
	new google.maps.LatLng(19.257997699830604, 79.4150161743164)   ,
	new google.maps.LatLng(19.289757543537625, 79.40608978271484)  ,
	new google.maps.LatLng(19.30304299778644, 79.38102722167969)   ,
	new google.maps.LatLng(19.334794101205166, 79.35905456542969)  ,
	new google.maps.LatLng(19.35795555298785, 79.3073844909668)    ,
	new google.maps.LatLng(19.36621513539742, 79.29193496704102)   ,
	new google.maps.LatLng(19.412849624405244, 79.29073333740234)  ,
	new google.maps.LatLng(19.46982917028776, 79.31991577148438)   ,
	new google.maps.LatLng(19.526465113658926, 79.33433532714844)  ,
	new google.maps.LatLng(19.549760999083695, 79.34326171875)     ,
	new google.maps.LatLng(19.60863281852909, 79.3326187133789)    ,
	new google.maps.LatLng(19.6348270888747, 79.3209457397461)     ,
	new google.maps.LatLng(19.75474865130708, 79.3597412109375)    ,
	new google.maps.LatLng(19.784311164268384, 79.36643600463867)  ,
	new google.maps.LatLng(19.801755319600232, 79.37501907348633)  ,
	new google.maps.LatLng(19.832116509513305, 79.3791389465332)   ,
	new google.maps.LatLng(19.83954442819481, 79.37029838562012)   ,
	new google.maps.LatLng(19.846487603223174, 79.35639381408691)  ,
	new google.maps.LatLng(19.85875851867693, 79.34514999389648)   ,
	new google.maps.LatLng(19.86965623329661, 79.34077262878418)   ,
	new google.maps.LatLng(19.876678808066142, 79.3447208404541)   ,
	new google.maps.LatLng(19.88200607101441, 79.34085845947266)   ,
	new google.maps.LatLng(19.899762320296663, 79.3363094329834)   ,
	new google.maps.LatLng(19.90775198276408, 79.32703971862793)   ,
	new google.maps.LatLng(19.92469841586939, 79.31682586669922)   ,
	new google.maps.LatLng(19.940029337620707, 79.32077407836914)  ,
	new google.maps.LatLng(19.946968107790845, 79.32180404663086)  ,
	new google.maps.LatLng(19.96318427653471, 79.31459426879883)   ,
	new google.maps.LatLng(19.965765801350866, 79.29931640625)     ,
	new google.maps.LatLng(19.969153988545695, 79.28936004638672)  ,
	new google.maps.LatLng(19.9820607013123, 79.27682876586914)    ,
	new google.maps.LatLng(19.994805042810555, 79.25622940063477)  ,
	new google.maps.LatLng(20.009806553421143, 79.22945022583008)  ,
	new google.maps.LatLng(20.02674202383044, 79.19065475463867)   ,
	new google.maps.LatLng(20.03641861645443, 79.18172836303711)   ,
	new google.maps.LatLng(20.08447019127748, 79.15597915649414)   ,
	new google.maps.LatLng(20.10478287093018, 79.12422180175781)   ,
	new google.maps.LatLng(20.11735606623472, 79.11478042602539)   ,
	new google.maps.LatLng(20.143788718605922, 79.08679962158203)  ,
	new google.maps.LatLng(20.19196844356078, 79.04439926147461)   ,
	new google.maps.LatLng(20.23224106205413, 79.01590347290039)   ,
	new google.maps.LatLng(20.243998699909653, 79.01556015014648)  ,
	new google.maps.LatLng(20.268799564945358, 79.01607513427734)  ,
	new google.maps.LatLng(20.308086324952182, 79.02259826660156)  ,
	new google.maps.LatLng(20.332394041597745, 79.02105331420898)  ,
	new google.maps.LatLng(20.352674377747743, 79.02036666870117)  ,
	new google.maps.LatLng(20.38276806317618, 79.00989532470703)   ,
	new google.maps.LatLng(20.539417612661246, 78.97470474243164)  ,
	new google.maps.LatLng(20.615432987667255, 78.93041610717773)  ,
	new google.maps.LatLng(20.6419410637845, 78.92715454101562)    ,
	new google.maps.LatLng(20.732997212795915, 78.92784118652344)  ,
	new google.maps.LatLng(20.79527570473505, 78.958740234375)     ,
	new google.maps.LatLng(20.86490727797997, 78.9700698852539)    ,
	new google.maps.LatLng(20.91302086177151, 78.99101257324219)   ,
	new google.maps.LatLng(20.994137455214727, 79.03461456298828)  ,
	new google.maps.LatLng(21.01545131063977, 79.04714584350586)   ,
	new google.maps.LatLng(21.023623485099527, 79.04388427734375)  ,
	new google.maps.LatLng(21.031154308064544, 78.98431777954102)  ,
	new google.maps.LatLng(21.056467900982707, 78.97144317626953)  ,
	new google.maps.LatLng(21.072166574205767, 78.94895553588867)  ,
	new google.maps.LatLng(21.067841594889025, 78.89144897460938)  ,
	new google.maps.LatLng(21.104679981647198, 78.89780044555664)  ,
	new google.maps.LatLng(21.126778627645717, 78.88509750366211)  ,
	new google.maps.LatLng(21.13846715959418, 78.88406753540039)   ,
	new google.maps.LatLng(21.137666604617912, 78.8463020324707)   ,
	new google.maps.LatLng(21.137826715959143, 78.79840850830078)  ,
	new google.maps.LatLng(21.136385707660697, 78.739013671875)    ,
	new google.maps.LatLng(21.137346381416503, 78.63567352294922)  ,
	new google.maps.LatLng(21.157679181286465, 78.63687515258789)  ,
	new google.maps.LatLng(21.16888504281977, 78.63395690917969)   ,
	new google.maps.LatLng(21.197216077387107, 78.63086700439453)  ,
	new google.maps.LatLng(21.22730184889005, 78.62503051757812)   ,
	new google.maps.LatLng(21.253701859453738, 78.61318588256836)  ,
	new google.maps.LatLng(21.257861429806663, 78.60803604125977)  ,
	new google.maps.LatLng(21.261061019369944, 78.60108375549316)  ,
	new google.maps.LatLng(21.26314071530574, 78.59610557556152)   ,
	new google.maps.LatLng(21.270979305169682, 78.58563423156738)  ,
	new google.maps.LatLng(21.276498061889264, 78.58451843261719)  ,
	new google.maps.LatLng(21.28049703162151, 78.58752250671387)   ,
	new google.maps.LatLng(21.316562824092625, 78.6068344116211)   ,
	new google.maps.LatLng(21.36932606518861, 78.62777709960938)   ,
	new google.maps.LatLng(21.4786293099784, 78.69781494140625)    ,
	new google.maps.LatLng(21.509935025029726, 78.66828918457031)  ,
	new google.maps.LatLng(21.5731647743054, 78.58795166015625)    ,
	new google.maps.LatLng(21.58657360225891, 78.53679656982422)   ,
	new google.maps.LatLng(21.612749163947704, 78.50830078125)     ,
	new google.maps.LatLng(21.66317164159202, 78.43963623046875)   ,
	new google.maps.LatLng(21.696032506167068, 78.38676452636719)  ,
	new google.maps.LatLng(21.74132354006686, 78.34213256835938)   ,
	new google.maps.LatLng(21.76683333154031, 78.29818725585938)   ,
	new google.maps.LatLng(21.763963706283302, 78.25286865234375)  ,
	new google.maps.LatLng(21.80572703209746, 78.22952270507812)   ,
	new google.maps.LatLng(21.81943295135797, 78.18351745605469)   ,
	new google.maps.LatLng(21.837599239731535, 78.13751220703125)  ,
	new google.maps.LatLng(21.841423427469365, 78.07605743408203)  ,
	new google.maps.LatLng(21.846203518278127, 77.99640655517578)  ,
	new google.maps.LatLng(21.867552638274518, 77.91915893554688)  ,
	new google.maps.LatLng(21.887783559390854, 77.88225173950195)  ,
	new google.maps.LatLng(21.924414929964197, 77.87006378173828)  ,
	new google.maps.LatLng(21.975683013841046, 77.86972045898438)  ,
	new google.maps.LatLng(22.107589214963635, 77.88276672363281)  ,
	new google.maps.LatLng(22.107589214963635, 77.88276672363281)  ,
	new google.maps.LatLng(22.268764039073965, 77.87521362304688)  ,
	new google.maps.LatLng(22.43895625329693, 77.83882141113281)   ,
	new google.maps.LatLng(22.648868402849164, 77.76466369628906)  ,
	new google.maps.LatLng(22.734390263126222, 77.7231216430664)   ,
	new google.maps.LatLng(22.747055625892813, 77.71041870117188)  ,
	new google.maps.LatLng(22.760986169250472, 77.67677307128906)  ,
	new google.maps.LatLng(22.80055382204716, 77.69771575927734)   ,
	new google.maps.LatLng(22.900213341547975, 77.64999389648438)  ,
	new google.maps.LatLng(22.953651321088312, 77.63351440429688)  ,
	new google.maps.LatLng(23.04877555969622, 77.54940032958984)   ,
	new google.maps.LatLng(23.149830673947783, 77.49893188476562)  ,
	new google.maps.LatLng(23.215791202149354, 77.51506805419922)  ,
	new google.maps.LatLng(23.29212587501636, 77.51575469970703)   ,
	new google.maps.LatLng(23.336896930851097, 77.47421264648438)  ,
	new google.maps.LatLng(23.340049253678117, 77.40692138671875)  ,
	new google.maps.LatLng(23.316089723883437, 77.31559753417969)  ,
	new google.maps.LatLng(23.400244245689255, 77.12162017822266)  ,
	new google.maps.LatLng(23.441514004659282, 77.08110809326172)  ,
	new google.maps.LatLng(23.4978846716957, 77.04299926757812)    ,
	new google.maps.LatLng(23.55580481650931, 77.0199966430664)    ,
	new google.maps.LatLng(23.61967624035243, 77.0364761352539)    ,
	new google.maps.LatLng(23.65049958627473, 77.06703186035156)   ,
	new google.maps.LatLng(23.707094975866056, 77.07561492919922)  ,
	new google.maps.LatLng(23.76366582789782, 77.0199966430664)    ,
	new google.maps.LatLng(23.846591932199328, 76.93519592285156)  ,
	new google.maps.LatLng(23.923502440596177, 76.90223693847656)  ,
	new google.maps.LatLng(23.993780566296863, 76.79443359375)     ,
	new google.maps.LatLng(24.027650966196067, 76.585693359375)    ,
	new google.maps.LatLng(24.253225122742613, 76.50947570800781)  ,
	new google.maps.LatLng(24.42026814640879, 76.54106140136719)   ,
	new google.maps.LatLng(24.48464899965402, 76.29798889160156)   ,
	new google.maps.LatLng(24.539003293579192, 76.1846923828125)   ,
	new google.maps.LatLng(24.552119771544216, 76.18160247802734)  ,
	new google.maps.LatLng(24.57834861223401, 76.1737060546875)    ,
	new google.maps.LatLng(24.589900050435183, 76.15653991699219)  ,
	new google.maps.LatLng(24.63172606772706, 76.07894897460938)   ,
	new google.maps.LatLng(24.687264355800114, 76.02676391601562)  ,
	new google.maps.LatLng(24.805123124714548, 75.98419189453125)  ,
	new google.maps.LatLng(24.824443781379227, 76.01371765136719)  ,
	new google.maps.LatLng(24.89017375095499, 75.96427917480469)   ,
	new google.maps.LatLng(25.03708281039567, 75.88359832763672)   ,
	new google.maps.LatLng(25.158026219484245, 75.85201263427734)  ,
	new google.maps.LatLng(25.190340428114524, 75.85132598876953)  ,
	new google.maps.LatLng(25.310511227182683, 75.72944641113281)  ,
	new google.maps.LatLng(25.39055896362595, 75.67279815673828)   ,
	new google.maps.LatLng(25.438624158827487, 75.63125610351562)  ,
	new google.maps.LatLng(25.4624945813551, 75.59452056884766)    ,
	new google.maps.LatLng(25.78628999289964, 75.40054321289062)   ,
	new google.maps.LatLng(26.083921329998336, 75.70953369140625)  ,
	new google.maps.LatLng(26.390639505668723, 75.94711303710938)  ,
	new google.maps.LatLng(26.5737895138798, 75.97251892089844)    ,
	new google.maps.LatLng(26.74683674289727, 75.85613250732422)   ,
	new google.maps.LatLng(26.829585485573016, 75.794677734375)    ,
	new google.maps.LatLng(26.829585485573016, 75.794677734375)    ,
	new google.maps.LatLng(26.899108991705745, 75.8441162109375)   ,
	new google.maps.LatLng(26.94043529328535, 75.84342956542969)   ,
	new google.maps.LatLng(27.038333414969433, 75.89286804199219)  ,
	new google.maps.LatLng(27.192349909429396, 75.95809936523438)  ,
	new google.maps.LatLng(27.27843318421324, 75.94985961914062)   ,
	new google.maps.LatLng(27.37115752876398, 75.95809936523438)   ,
	new google.maps.LatLng(27.412004805752012, 75.9979248046875)   ,
	new google.maps.LatLng(27.446743589630966, 76.01783752441406)  ,
	new google.maps.LatLng(27.487867479036478, 76.05234146118164)  ,
	new google.maps.LatLng(27.497308575744984, 76.05783462524414)  ,
	new google.maps.LatLng(27.525779236987805, 76.06470108032227)  ,
	new google.maps.LatLng(27.55226403452445, 76.07431411743164)   ,
	new google.maps.LatLng(27.578438133082315, 76.0744857788086)   ,
	new google.maps.LatLng(27.622098452522106, 76.10984802246094)  ,
	new google.maps.LatLng(27.660419994878637, 76.15053176879883)
   
  ];
 var flightPath = new google.maps.Polyline({
    path: flightPlanCoordinates,
    geodesic: true,
    strokeColor: '#FF0000',
    strokeOpacity: 1.0,
    strokeWeight: 2
  });

  flightPath.setMap(map); 
  
	google.maps.event.addListener(map, "click", function (e) {

	//lat and lng is available in e object
	var latLng = e.latLng;
	alert(latLng);
	});
  //locatepoint()
}

function setroute(os)
{
	start = (new Date()).getTime();
	var pp = [];
	for(var i=0;i<os.polypoints.length;i++)
	{
		pp[i] = new google.maps.LatLng(os.polypoints[i][0],os.polypoints[i][1]);
	}
	
	var polyline1 = new google.maps.Polyline();
	for(var j=0;j<pp.length;j++)
	{
		polyline1.getPath().push(pp[j]);
	}
	
	var element = new google.maps.LatLng(21.59104293572423, 78.50967407226562);
	if (google.maps.geometry.poly.isLocationOnEdge(element, polyline1, 0.05)) {
		//console.log(element + " on edge");		
		//n1 = d.getMilliseconds(); 
		end = (new Date()).getTime();
		var tot_time=end-start;
		alert("On Edge"+ tot_time);
		
		
	} else {
		//console.log(element + " not on edge");
		alert("Not On Edge");
	}
	
	/*
	for (var a = 0; a < pointsArray.length; a++) {	
		var point1 = new google.maps.Marker ({
		position:pointsArray[a],
		draggable:true,
		map:map,
		flat:true
		});
	}*/

}

function fetchdata()
{
	//d = new Date();
	//n = d.getMilliseconds(); 
	//start = (new Date()).getTime();
	var jax = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject('Microsoft.XMLHTTP');
	jax.open('POST','process_polypoint.php');
	jax.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	jax.send('command=fetch')
	jax.onreadystatechange = function(){ if(jax.readyState==4) {		
		try { setroute( eval('(' + jax.responseText + ')') ); }
		catch(e){ alert(e); }
	}}
}
</script>
</body>
</html>