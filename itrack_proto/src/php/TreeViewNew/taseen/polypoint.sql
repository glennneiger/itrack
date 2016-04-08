-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 21, 2015 at 07:02 AM
-- Server version: 5.5.16
-- PHP Version: 5.3.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `mapdir`
--

-- --------------------------------------------------------

--
-- Table structure for table `polypoint`
--

CREATE TABLE IF NOT EXISTS `polypoint` (
  `sno` int(11) NOT NULL AUTO_INCREMENT,
  `polypoint_value` text NOT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `polypoint`
--

INSERT INTO `polypoint` (`sno`, `polypoint_value`) VALUES
(1, '{"polypoints":[[16.50333165171276, 80.63132286071777],\r\n[16.50942136660169, 80.61939239501953],\r\n[16.509009905421724, 80.6107234954834],\r\n[16.514194252281314, 80.60334205627441],\r\n[16.519543035811243, 80.60239791870117],\r\n[16.52974645824655, 80.5898666381836],\r\n[16.558132174435467, 80.56737899780273],\r\n[16.565207383430426, 80.55218696594238],\r\n[16.572117801806716, 80.53424835205078],\r\n[16.584703997933822, 80.52454948425293],\r\n[16.588899213699037, 80.5213737487793],\r\n[16.601484311876263, 80.5309009552002],\r\n[16.626076339991933, 80.5433464050293],\r\n[16.628296869727386, 80.55081367492676],\r\n[16.631750976007943, 80.55373191833496],\r\n[16.65272099988651, 80.56265830993652],\r\n[16.677142016188974, 80.57287216186523],\r\n[16.678868636740173, 80.57476043701172],\r\n[16.689639108178543, 80.58445930480957],\r\n[16.692187756893034, 80.59501647949219],\r\n[16.694736371612272, 80.60188293457031],\r\n[16.704683864961257, 80.61098098754883],\r\n[16.71948082992962, 80.61982154846191],\r\n[16.72391969563945, 80.62548637390137],\r\n[16.75129041669962, 80.63870429992676],\r\n[16.762960819049955, 80.64248085021973],\r\n[16.770110641459027, 80.64153671264648],\r\n[16.797885560225108, 80.63063621520996],\r\n[16.817276289715583, 80.62625885009766],\r\n[16.829599875165805, 80.6290054321289],\r\n[16.836500732860216, 80.63235282897949],\r\n[16.845126451281548, 80.63218116760254],\r\n[16.85950177486932, 80.63544273376465],\r\n[16.87362959934793, 80.63990592956543],\r\n[16.91395379420299, 80.64393997192383],\r\n[16.926024737211172, 80.63913345336914],\r\n[16.947619119096455, 80.64428329467773],\r\n[16.96346436938903, 80.64823150634766],\r\n[16.97824111850635, 80.65303802490234],\r\n[16.992360036189883, 80.65973281860352],\r\n[17.0071345092729, 80.66247940063477],\r\n[17.022318170180544, 80.66084861755371],\r\n[17.031181575807338, 80.6480598449707],\r\n[17.042096118603368, 80.63879013061523],\r\n[17.0555537751158, 80.6370735168457],\r\n[17.067943808760646, 80.62874794006348],\r\n[17.077215282613974, 80.62308311462402],\r\n[17.08394296623869, 80.62402725219727],\r\n[17.0955107325946, 80.61604499816895],\r\n[17.11068720890056, 80.61698913574219],\r\n[17.125616372712916, 80.61595916748047],\r\n[17.154240921268514, 80.62986373901367],\r\n[17.18195905637421, 80.6422233581543],\r\n[17.206393500256976, 80.66024780273438],\r\n[17.200326187644396, 80.61630249023438],\r\n[17.200162203461726, 80.56188583374023],\r\n[17.196554514688234, 80.52206039428711],\r\n[17.199342280369144, 80.4942512512207],\r\n[17.219511333785114, 80.44979095458984],\r\n[17.216723872015926, 80.42335510253906],\r\n[17.192782764890712, 80.4030990600586],\r\n[17.178679019857835, 80.38198471069336],\r\n[17.194914632912802, 80.35863876342773],\r\n[17.21393636822409, 80.31435012817383],\r\n[17.230824717654645, 80.25701522827148],\r\n[17.240170033989017, 80.2273178100586],\r\n[17.243612926051476, 80.18903732299805],\r\n[17.253449406654394, 80.16826629638672],\r\n[17.261973932302723, 80.14921188354492],\r\n[17.26672782352052, 80.12500762939453],\r\n[17.25640024858061, 80.11882781982422],\r\n[17.253285469607814, 80.10080337524414],\r\n[17.26410500214549, 80.0877571105957],\r\n[17.274596063712107, 80.05943298339844],\r\n[17.290167631709522, 80.04467010498047],\r\n[17.32114293842747, 80.01874923706055],\r\n[17.320323553075223, 79.97119903564453],\r\n[17.365548153696455, 79.9310302734375],\r\n[17.380129070408806, 79.90047454833984],\r\n[17.394381203596815, 79.8819351196289],\r\n[17.448594680052686, 79.8208236694336],\r\n[17.503610605411705, 79.75713729858398],\r\n[17.545679999829797, 79.70495223999023],\r\n[17.624063419234876, 79.60453033447266],\r\n[17.652855460321106, 79.60487365722656],\r\n[17.692109914190738, 79.60899353027344],\r\n[17.732990849540247, 79.59835052490234],\r\n[17.77811257088691, 79.58015441894531],\r\n[17.83629622635646, 79.60556030273438],\r\n[17.903608638609793, 79.595947265625],\r\n[17.95391289081595, 79.6010971069336],\r\n[17.95840364668414, 79.60118293762207],\r\n[17.977181934922484, 79.59654808044434],\r\n[17.99203986095381, 79.5915699005127],\r\n[18.00354981167137, 79.57757949829102],\r\n[18.008937190030505, 79.56075668334961],\r\n[18.020201176238725, 79.54994201660156],\r\n[18.070145820303544, 79.52007293701172],\r\n[18.095928674182087, 79.44900512695312],\r\n[18.224459888941304, 79.376220703125],\r\n[18.27597624663713, 79.32060241699219],\r\n[18.327151403632502, 79.26532745361328],\r\n[18.381569510936767, 79.21194076538086],\r\n[18.397370356748134, 79.18567657470703],\r\n[18.404211510561133, 79.14499282836914],\r\n[18.413006880384255, 79.13761138916016],\r\n[18.436621852843622, 79.15460586547852],\r\n[18.446066933638484, 79.16095733642578],\r\n[18.461210548135874, 79.18619155883789],\r\n[18.467397877137373, 79.19580459594727],\r\n[18.50988902503296, 79.27871704101562],\r\n[18.560669199216157, 79.35562133789062],\r\n[18.621520393840992, 79.39132690429688],\r\n[18.65177518608126, 79.39510345458984],\r\n[18.72332418587802, 79.42840576171875],\r\n[18.747383869213394, 79.45175170898438],\r\n[18.76786441131958, 79.45449829101562],\r\n[18.76103784020659, 79.51148986816406],\r\n[18.79516793121411, 79.52075958251953],\r\n[18.82604157672509, 79.52831268310547],\r\n[18.84683746769596, 79.52762603759766],\r\n[18.857884237121382, 79.47372436523438],\r\n[18.87104193960541, 79.4447135925293],\r\n[18.88939602131517, 79.44076538085938],\r\n[18.90969686563054, 79.44488525390625],\r\n[18.936977319164196, 79.45140838623047],\r\n[18.966363773665115, 79.45681571960449],\r\n[19.007918091778937, 79.47681427001953],\r\n[19.111109635474076, 79.46617126464844],\r\n[19.147438517185837, 79.44557189941406],\r\n[19.21164321910282, 79.42325592041016],\r\n[19.257997699830604, 79.4150161743164],\r\n[19.289757543537625, 79.40608978271484],\r\n[19.30304299778644, 79.38102722167969],\r\n[19.334794101205166, 79.35905456542969],\r\n[19.35795555298785, 79.3073844909668],\r\n[19.36621513539742, 79.29193496704102],\r\n[19.412849624405244, 79.29073333740234],\r\n[19.46982917028776, 79.31991577148438],\r\n[19.526465113658926, 79.33433532714844],\r\n[19.549760999083695, 79.34326171875],\r\n[19.60863281852909, 79.3326187133789],\r\n[19.6348270888747, 79.3209457397461],\r\n[19.75474865130708, 79.3597412109375],\r\n[19.784311164268384, 79.36643600463867],\r\n[19.801755319600232, 79.37501907348633],\r\n[19.832116509513305, 79.3791389465332],\r\n[19.83954442819481, 79.37029838562012],\r\n[19.846487603223174, 79.35639381408691],\r\n[19.85875851867693, 79.34514999389648],\r\n[19.86965623329661, 79.34077262878418],\r\n[19.876678808066142, 79.3447208404541],\r\n[19.88200607101441, 79.34085845947266],\r\n[19.899762320296663, 79.3363094329834],\r\n[19.90775198276408, 79.32703971862793],\r\n[19.92469841586939, 79.31682586669922],\r\n[19.940029337620707, 79.32077407836914],\r\n[19.946968107790845, 79.32180404663086],\r\n[19.96318427653471, 79.31459426879883],\r\n[19.965765801350866, 79.29931640625],\r\n[19.969153988545695, 79.28936004638672],\r\n[19.9820607013123, 79.27682876586914],\r\n[19.994805042810555, 79.25622940063477],\r\n[20.009806553421143, 79.22945022583008],\r\n[20.02674202383044, 79.19065475463867],\r\n[20.03641861645443, 79.18172836303711],\r\n[20.08447019127748, 79.15597915649414],\r\n[20.10478287093018, 79.12422180175781],\r\n[20.11735606623472, 79.11478042602539],\r\n[20.143788718605922, 79.08679962158203],\r\n[20.19196844356078, 79.04439926147461],\r\n[20.23224106205413, 79.01590347290039],\r\n[20.243998699909653, 79.01556015014648],\r\n[20.268799564945358, 79.01607513427734],\r\n[20.308086324952182, 79.02259826660156],\r\n[20.332394041597745, 79.02105331420898],\r\n[20.352674377747743, 79.02036666870117],\r\n[20.38276806317618, 79.00989532470703],\r\n[20.539417612661246, 78.97470474243164],\r\n[20.615432987667255, 78.93041610717773],\r\n[20.6419410637845, 78.92715454101562],\r\n[20.732997212795915, 78.92784118652344],\r\n[20.79527570473505, 78.958740234375],\r\n[20.86490727797997, 78.9700698852539],\r\n[20.91302086177151, 78.99101257324219],\r\n[20.994137455214727, 79.03461456298828],\r\n[21.01545131063977, 79.04714584350586],\r\n[21.023623485099527, 79.04388427734375],\r\n[21.031154308064544, 78.98431777954102],\r\n[21.056467900982707, 78.97144317626953],\r\n[21.072166574205767, 78.94895553588867],\r\n[21.067841594889025, 78.89144897460938],\r\n[21.104679981647198, 78.89780044555664],\r\n[21.126778627645717, 78.88509750366211],\r\n[21.13846715959418, 78.88406753540039],\r\n[21.137666604617912, 78.8463020324707],\r\n[21.137826715959143, 78.79840850830078],\r\n[21.136385707660697, 78.739013671875],\r\n[21.137346381416503, 78.63567352294922],\r\n[21.157679181286465, 78.63687515258789],\r\n[21.16888504281977, 78.63395690917969],\r\n[21.197216077387107, 78.63086700439453],\r\n[21.22730184889005, 78.62503051757812],\r\n[21.253701859453738, 78.61318588256836],\r\n[21.257861429806663, 78.60803604125977],\r\n[21.261061019369944, 78.60108375549316],\r\n[21.26314071530574, 78.59610557556152],\r\n[21.270979305169682, 78.58563423156738],\r\n[21.276498061889264, 78.58451843261719],\r\n[21.28049703162151, 78.58752250671387],\r\n[21.316562824092625, 78.6068344116211],\r\n[21.36932606518861, 78.62777709960938],\r\n[21.4786293099784, 78.69781494140625],\r\n[21.509935025029726, 78.66828918457031],\r\n[21.5731647743054, 78.58795166015625],\r\n[21.58657360225891, 78.53679656982422],\r\n[21.612749163947704, 78.50830078125],\r\n[21.66317164159202, 78.43963623046875],\r\n[21.696032506167068, 78.38676452636719],\r\n[21.74132354006686, 78.34213256835938],\r\n[21.76683333154031, 78.29818725585938],\r\n[21.763963706283302, 78.25286865234375],\r\n[21.80572703209746, 78.22952270507812],\r\n[21.81943295135797, 78.18351745605469],\r\n[21.837599239731535, 78.13751220703125],\r\n[21.841423427469365, 78.07605743408203],\r\n[21.846203518278127, 77.99640655517578],\r\n[21.867552638274518, 77.91915893554688],\r\n[21.887783559390854, 77.88225173950195],\r\n[21.924414929964197, 77.87006378173828],\r\n[21.975683013841046, 77.86972045898438],\r\n[22.107589214963635, 77.88276672363281],\r\n[22.107589214963635, 77.88276672363281],\r\n[22.268764039073965, 77.87521362304688],\r\n[22.43895625329693, 77.83882141113281],\r\n[22.648868402849164, 77.76466369628906],\r\n[22.734390263126222, 77.7231216430664],\r\n[22.747055625892813, 77.71041870117188],\r\n[22.760986169250472, 77.67677307128906],\r\n[22.80055382204716, 77.69771575927734],\r\n[22.900213341547975, 77.64999389648438],\r\n[22.953651321088312, 77.63351440429688],\r\n[23.04877555969622, 77.54940032958984],\r\n[23.149830673947783, 77.49893188476562],\r\n[23.215791202149354, 77.51506805419922],\r\n[23.29212587501636, 77.51575469970703],\r\n[23.336896930851097, 77.47421264648438],\r\n[23.340049253678117, 77.40692138671875],\r\n[23.316089723883437, 77.31559753417969],\r\n[23.400244245689255, 77.12162017822266],\r\n[23.441514004659282, 77.08110809326172],\r\n[23.4978846716957, 77.04299926757812],\r\n[23.55580481650931, 77.0199966430664],\r\n[23.61967624035243, 77.0364761352539],\r\n[23.65049958627473, 77.06703186035156],\r\n[23.707094975866056, 77.07561492919922],\r\n[23.76366582789782, 77.0199966430664],\r\n[23.846591932199328, 76.93519592285156],\r\n[23.923502440596177, 76.90223693847656],\r\n[23.993780566296863, 76.79443359375],\r\n[24.027650966196067, 76.585693359375],\r\n[24.253225122742613, 76.50947570800781],\r\n[24.42026814640879, 76.54106140136719],\r\n[24.48464899965402, 76.29798889160156],\r\n[24.539003293579192, 76.1846923828125],\r\n[24.552119771544216, 76.18160247802734],\r\n[24.57834861223401, 76.1737060546875],\r\n[24.589900050435183, 76.15653991699219],\r\n[24.63172606772706, 76.07894897460938],\r\n[24.687264355800114, 76.02676391601562],\r\n[24.805123124714548, 75.98419189453125],\r\n[24.824443781379227, 76.01371765136719],\r\n[24.89017375095499, 75.96427917480469],\r\n[25.03708281039567, 75.88359832763672],\r\n[25.158026219484245, 75.85201263427734],\r\n[25.190340428114524, 75.85132598876953],\r\n[25.310511227182683, 75.72944641113281],\r\n[25.39055896362595, 75.67279815673828],\r\n[25.438624158827487, 75.63125610351562],\r\n[25.4624945813551, 75.59452056884766],\r\n[25.78628999289964, 75.40054321289062],\r\n[26.083921329998336, 75.70953369140625],\r\n[26.390639505668723, 75.94711303710938],\r\n[26.5737895138798, 75.97251892089844],\r\n[26.74683674289727, 75.85613250732422],\r\n[26.829585485573016, 75.794677734375],\r\n[26.829585485573016, 75.794677734375],\r\n[26.899108991705745, 75.8441162109375],\r\n[26.94043529328535, 75.84342956542969],\r\n[27.038333414969433, 75.89286804199219],\r\n[27.192349909429396, 75.95809936523438],\r\n[27.27843318421324, 75.94985961914062],\r\n[27.37115752876398, 75.95809936523438],\r\n[27.412004805752012, 75.9979248046875],\r\n[27.446743589630966, 76.01783752441406],\r\n[27.487867479036478, 76.05234146118164],\r\n[27.497308575744984, 76.05783462524414],\r\n[27.525779236987805, 76.06470108032227],\r\n[27.55226403452445, 76.07431411743164],\r\n[27.578438133082315, 76.0744857788086],\r\n[27.622098452522106, 76.10984802246094],\r\n[27.660419994878637, 76.15053176879883]]}');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
