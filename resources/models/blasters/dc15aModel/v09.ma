//Maya ASCII 2020 scene
//Name: v09.ma
//Last modified: Wed, Jun 30, 2021 09:35:19 PM
//Codeset: 1252
requires maya "2020";
requires "stereoCamera" "10.0";
requires "stereoCamera" "10.0";
currentUnit -l centimeter -a degree -t film;
fileInfo "application" "maya";
fileInfo "product" "Maya 2020";
fileInfo "version" "2020";
fileInfo "cutIdentifier" "201911140446-42a737a01c";
fileInfo "osv" "Microsoft Windows 10 Technical Preview  (Build 19041)\n";
fileInfo "UUID" "1B6B93B8-41E7-A37A-283A-85A9ABFD13E3";
createNode transform -s -n "persp";
	rename -uid "0C7DAFFE-42DA-4EE5-A8A0-16B108F2FD0E";
	setAttr ".v" no;
	setAttr ".t" -type "double3" -23.619412316585827 2.8738662585931047 3.4494876758475947 ;
	setAttr ".r" -type "double3" -18.871220634420656 993.00000000004434 0 ;
createNode camera -s -n "perspShape" -p "persp";
	rename -uid "C09FF979-4F46-75EF-36E3-2C858F11C423";
	setAttr -k off ".v" no;
	setAttr ".fl" 34.999999999999993;
	setAttr ".coi" 26.579231502809648;
	setAttr ".imn" -type "string" "persp";
	setAttr ".den" -type "string" "persp_depth";
	setAttr ".man" -type "string" "persp_mask";
	setAttr ".hc" -type "string" "viewSet -p %camera";
createNode transform -s -n "top";
	rename -uid "92725ED6-48AF-0E1A-4DCA-A89DD694CD2D";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 0 1000.1 0 ;
	setAttr ".r" -type "double3" -90 0 0 ;
createNode camera -s -n "topShape" -p "top";
	rename -uid "73BDF9A0-4393-4FB0-192E-AAB074EA276C";
	setAttr -k off ".v" no;
	setAttr ".rnd" no;
	setAttr ".coi" 1000.1;
	setAttr ".ow" 30;
	setAttr ".imn" -type "string" "top";
	setAttr ".den" -type "string" "top_depth";
	setAttr ".man" -type "string" "top_mask";
	setAttr ".hc" -type "string" "viewSet -t %camera";
	setAttr ".o" yes;
createNode transform -s -n "front";
	rename -uid "89C52399-466E-5ED6-4C61-019EA3C08889";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 0 0 1000.1 ;
createNode camera -s -n "frontShape" -p "front";
	rename -uid "F8F6F1FD-4338-F448-374B-10A992C86B8C";
	setAttr -k off ".v" no;
	setAttr ".rnd" no;
	setAttr ".coi" 1000.1;
	setAttr ".ow" 4.3483532190413099;
	setAttr ".imn" -type "string" "front";
	setAttr ".den" -type "string" "front_depth";
	setAttr ".man" -type "string" "front_mask";
	setAttr ".hc" -type "string" "viewSet -f %camera";
	setAttr ".o" yes;
createNode transform -s -n "side";
	rename -uid "157B6A83-4169-1C85-20FD-85B9385B406A";
	setAttr ".t" -type "double3" 1000.1 1.9399806508357851 -4.8100509899766903 ;
	setAttr ".r" -type "double3" 0 90 0 ;
createNode camera -s -n "sideShape" -p "side";
	rename -uid "E4E2265E-40CB-1A56-8B5E-B29305D8D682";
	setAttr -k off ".v";
	setAttr ".rnd" no;
	setAttr ".coi" 1000.1;
	setAttr ".ow" 5.6945511365856651;
	setAttr ".imn" -type "string" "side";
	setAttr ".den" -type "string" "side_depth";
	setAttr ".man" -type "string" "side_mask";
	setAttr ".hc" -type "string" "viewSet -s %camera";
	setAttr ".o" yes;
createNode transform -n "imagePlane1";
	rename -uid "8F15C810-44FD-AC32-F571-90814E2AE768";
	setAttr ".t" -type "double3" -41.22933970079449 0 0 ;
	setAttr ".r" -type "double3" 0 -90 0 ;
createNode imagePlane -n "imagePlaneShape1" -p "imagePlane1";
	rename -uid "09B226D4-4ACB-78F2-7170-658C89519E01";
	setAttr -k off ".v";
	setAttr ".fc" 102;
	setAttr ".imn" -type "string" "C:/Users/Liam - Moose/Downloads/DC-15A_blaster_-_SW_Card_Trader.png";
	setAttr ".cov" -type "short2" 1201 479 ;
	setAttr ".dlc" no;
	setAttr ".w" 12.01;
	setAttr ".h" 4.79;
	setAttr ".cs" -type "string" "sRGB";
createNode transform -n "group3";
	rename -uid "059781F5-4F47-22AD-A686-1194F8049B4B";
	setAttr ".rp" -type "double3" 0 -0.05493236480005248 0.044196844100952148 ;
	setAttr ".sp" -type "double3" 0 -0.05493236480005248 0.044196844100952148 ;
createNode mesh -n "group3Shape" -p "group3";
	rename -uid "D60477F0-4009-C3EF-3D46-1BAECEBDD470";
	setAttr -k off ".v";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.44560635462403297 0.26572959311306477 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
createNode transform -n "group4";
	rename -uid "A1A17606-4A29-577E-1264-5587AD9DC728";
createNode transform -n "pCube25" -p "group4";
	rename -uid "F1FBE5B5-47F7-A6C8-C63B-87BDC58C60DC";
	setAttr ".t" -type "double3" 0.22780749932010735 1.5127799062455027 -4.6566050865774233 ;
	setAttr ".r" -type "double3" 0 0 -20 ;
	setAttr ".s" -type "double3" 0.16816892219159299 0.16816892219159299 0.16816892219159299 ;
createNode transform -n "transform16" -p "pCube25";
	rename -uid "4E4A2E32-4F82-CAA3-3B8F-DE858123482C";
	setAttr ".v" no;
createNode mesh -n "pCubeShape25" -p "transform16";
	rename -uid "974AC3FC-414D-E6D7-B81C-A2A47D5C81D5";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr ".iog[0].og[0].gcl" -type "componentList" 1 "f[0:5]";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.25 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr -s 14 ".uvst[0].uvsp[0:13]" -type "float2" 0.375 0 0.625 0 0.375
		 0.25 0.625 0.25 0.375 0.5 0.625 0.5 0.375 0.75 0.625 0.75 0.375 1 0.625 1 0.875 0
		 0.875 0.25 0.125 0 0.125 0.25;
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 -0.22489055 2.5808611 0 
		-0.22489055 2.5808611 0 2.0240152 0.449781 0 2.0240152 0.449781 0 2.0561435 -2.4095421 
		0 2.0561435 -2.4095421 0 -0.22489055 -2.5808611 0 -0.22489055 -2.5808611;
	setAttr -s 8 ".vt[0:7]"  -0.5 -0.5 0.5 0.5 -0.5 0.5 -0.5 0.5 0.5 0.5 0.5 0.5
		 -0.5 0.5 -0.5 0.5 0.5 -0.5 -0.5 -0.5 -0.5 0.5 -0.5 -0.5;
	setAttr -s 12 ".ed[0:11]"  0 1 0 2 3 0 4 5 0 6 7 0 0 2 0 1 3 0 2 4 0
		 3 5 0 4 6 0 5 7 0 6 0 0 7 1 0;
	setAttr -s 6 -ch 24 ".fc[0:5]" -type "polyFaces" 
		f 4 0 5 -2 -5
		mu 0 4 0 1 3 2
		f 4 1 7 -3 -7
		mu 0 4 2 3 5 4
		f 4 2 9 -4 -9
		mu 0 4 4 5 7 6
		f 4 3 11 -1 -11
		mu 0 4 6 7 9 8
		f 4 -12 -10 -8 -6
		mu 0 4 1 10 11 3
		f 4 10 4 6 8
		mu 0 4 12 0 2 13;
	setAttr ".cd" -type "dataPolyComponent" Index_Data Edge 0 ;
	setAttr ".cvd" -type "dataPolyComponent" Index_Data Vertex 0 ;
	setAttr ".pd[0]" -type "dataPolyComponent" Index_Data UV 0 ;
	setAttr ".hfd" -type "dataPolyComponent" Index_Data Face 0 ;
createNode transform -n "pCube24" -p "group4";
	rename -uid "F79D248D-4FA7-4BD3-BB46-D4BCB23B9E67";
	setAttr ".t" -type "double3" -0.25161216906460931 1.5127799062455027 -4.6566050865774233 ;
	setAttr ".r" -type "double3" 0 0 20 ;
	setAttr ".s" -type "double3" 0.16816892219159299 0.16816892219159299 0.16816892219159299 ;
createNode transform -n "transform17" -p "pCube24";
	rename -uid "1A72003F-43AA-76E8-551D-7CBF07273847";
	setAttr ".v" no;
createNode mesh -n "pCubeShape22" -p "transform17";
	rename -uid "B97597FF-4B46-25E8-F7AD-6BBC795A3C06";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.25 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 -0.22489055 2.5808611 0 
		-0.22489055 2.5808611 0 2.0240152 0.449781 0 2.0240152 0.449781 0 2.0561435 -2.4095421 
		0 2.0561435 -2.4095421 0 -0.22489055 -2.5808611 0 -0.22489055 -2.5808611;
createNode transform -n "group2" -p "group4";
	rename -uid "EAAB93A0-4A95-0C51-ABF8-3D9DE4DC7B21";
	setAttr ".rp" -type "double3" 0 -0.02809173147930899 0.041230024043987346 ;
	setAttr ".sp" -type "double3" 0 -0.02809173147930899 0.041230024043987346 ;
createNode transform -n "transform18" -p "group2";
	rename -uid "DA5F4242-47CC-1318-A746-BFABE7067E78";
	setAttr ".v" no;
createNode mesh -n "group2Shape" -p "transform18";
	rename -uid "F6D1D16F-4001-8BC9-8994-2F821633A350";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.87116187810897827 0.60273182392120361 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
createNode transform -n "group1" -p "group4";
	rename -uid "B8C2FE7C-4060-268A-CFE4-DFB2FBDA8C4B";
createNode transform -n "pCube1" -p "group1";
	rename -uid "EE2EB6F4-4B77-F051-3FCA-C1B040BF3C23";
	setAttr ".t" -type "double3" 0 -1.3156913456617667 3.4786789038614772 ;
	setAttr ".r" -type "double3" -31.67118216744338 0 0 ;
createNode transform -n "transform15" -p "pCube1";
	rename -uid "FABDFD6E-4B10-2E7C-DD8B-01A667522F28";
	setAttr ".v" no;
createNode mesh -n "pCubeShape1" -p "transform15";
	rename -uid "A34969B0-420E-DCE4-9159-85A16B76C13C";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.25 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 20 ".pt[0:19]" -type "float3"  0.1890211 0 0 -0.1890211 
		0 0 0.1890211 0 0 -0.1890211 0 0 0.1890211 0 0 -0.1890211 0 0 0.1890211 0 0 -0.1890211 
		0 0 0.1890211 0 0 -0.1890211 0 0 0.1890211 0 0 -0.1890211 0 0 0.1890211 0 0 -0.1890211 
		0 0 -0.1890211 0 0 0.1890211 0 0 0.1890211 0 0 -0.1890211 0 0 -0.1890211 0 0 0.1890211 
		0 0;
createNode transform -n "pCube2" -p "group1";
	rename -uid "07D8F196-4A4E-8B6D-5FCD-AD9DC476E402";
	setAttr ".t" -type "double3" 0 0.93282540049497009 2.6511879803541265 ;
	setAttr ".s" -type "double3" 1.2125922352957998 1.2125922352957998 4.9197559987144626 ;
createNode transform -n "transform14" -p "pCube2";
	rename -uid "D946A5FB-466A-0097-912D-88AB826C5069";
	setAttr ".v" no;
createNode mesh -n "pCubeShape2" -p "transform14";
	rename -uid "85D28FDD-40A1-48E8-35E3-A89A5D920A10";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt[12:15]" -type "float3"  0 0 0.099793702 0 0 0.099793702 
		0 0 0.047402013 0 0 0.047402013;
createNode transform -n "pCube3" -p "group1";
	rename -uid "55889786-4711-6BDF-87E2-69B8DAA8CC07";
	setAttr ".t" -type "double3" 0 0.66079242188143039 1.3205719410276524 ;
	setAttr ".s" -type "double3" 1.4598901810172218 0.44499998772886112 1 ;
createNode transform -n "transform13" -p "pCube3";
	rename -uid "5877EE2D-4858-AE01-5F66-BC8C200EC2BC";
	setAttr ".v" no;
createNode mesh -n "pCubeShape3" -p "transform13";
	rename -uid "6B089A81-44E1-7506-6D8D-9AAFA511215A";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.625 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 0 3.8682742 0 0 3.8682742 
		0 0 3.8682742 0 0 3.8682742 0 0 -0.61078048 0 0 -0.61078048 0 0 -0.61078048 0 0 -0.61078048;
createNode transform -n "pCube4" -p "group1";
	rename -uid "D794C8F9-4DB7-7C8A-F81C-BBB1A5F5EBCB";
	setAttr ".t" -type "double3" 0 0.97490057406070585 -0.22411507449671442 ;
	setAttr ".s" -type "double3" 0.89648568112789473 0.89648568112789473 0.89648568112789473 ;
createNode transform -n "transform12" -p "pCube4";
	rename -uid "B563922A-4119-4CE0-2018-9D8764343021";
	setAttr ".v" no;
createNode mesh -n "pCubeShape4" -p "transform12";
	rename -uid "B35219DC-425D-9299-BDD9-F5B122376731";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.625 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt[4:7]" -type "float3"  0 0 -5.1748581 0 0 -5.1748581 
		0 0 -5.1748581 0 0 -5.1748581;
createNode transform -n "pCube5" -p "group1";
	rename -uid "43172664-42A6-A844-2F78-A8A3DA45F11A";
	setAttr ".t" -type "double3" 0 0.11205753724835618 0.6163164548659632 ;
	setAttr ".s" -type "double3" 0.50830698535750252 0.50830698535750252 0.50830698535750252 ;
createNode transform -n "transform11" -p "pCube5";
	rename -uid "CBBF80EA-4E68-ADAA-D28E-A68669A866C8";
	setAttr ".v" no;
createNode mesh -n "pCubeShape5" -p "transform11";
	rename -uid "E9109D31-429E-75CA-3AE9-C0BEDAD53F89";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.375 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt";
	setAttr ".pt[0]" -type "float3" 0 -0.19840725 0 ;
	setAttr ".pt[1]" -type "float3" 0 -0.19840725 0 ;
	setAttr ".pt[6]" -type "float3" 0 -0.19840725 0.264543 ;
	setAttr ".pt[7]" -type "float3" 0 -0.19840725 0.264543 ;
createNode transform -n "pCube6" -p "group1";
	rename -uid "22251C1A-47EF-600E-BCD7-5DB433B01F06";
	setAttr ".t" -type "double3" 0 -0.046690655669919812 0.4513430048092264 ;
	setAttr ".r" -type "double3" 3.5129566598039679 0 0 ;
	setAttr ".s" -type "double3" 0.8172000364802614 0.38888887690922047 1 ;
createNode transform -n "transform10" -p "pCube6";
	rename -uid "33954B80-4C43-9160-7100-43A10A39C81D";
	setAttr ".v" no;
createNode mesh -n "pCubeShape6" -p "transform10";
	rename -uid "187D5131-49CC-FD53-9BAA-568A4455D917";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.375 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[0:7]" -type "float3"  0.1716404 0 0 -0.1716404 
		0 0 0.1716404 0 0 -0.1716404 0 0 0.1716404 0.11907308 -5.2946792 -0.1716404 0.11907308 
		-5.2946792 0.1716404 0.18500863 -5.5295472 -0.1716404 0.18500863 -5.5295472;
createNode transform -n "pCube7" -p "group1";
	rename -uid "C42684DF-4B84-4279-6012-08BA1FA8C024";
	setAttr ".t" -type "double3" 0 0.12286043102526145 -3.854746023417583 ;
createNode transform -n "transform9" -p "pCube7";
	rename -uid "D797D829-4664-2990-33E5-3182644235E3";
	setAttr ".v" no;
createNode mesh -n "pCubeShape7" -p "transform9";
	rename -uid "A1DDEC2D-4FBC-600A-8CA5-199A4ED099DB";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.5 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 -0.046072662 0.82162917 
		0 -0.046072662 0.82162917 0 -0.69108987 0.8753804 0 -0.69108987 0.8753804 0 -0.15357554 
		-1.1594949 0 -0.15357554 -1.1594949 0 0.49144173 -1.2516401 0 0.49144173 -1.2516401;
createNode transform -n "pCube8" -p "group1";
	rename -uid "061854B0-4F0D-E142-8C7E-468AC296E18C";
	setAttr ".t" -type "double3" 0 1.5934217954249659 -0.28584284116402081 ;
	setAttr ".s" -type "double3" 0.3820099219704774 0.3820099219704774 0.3820099219704774 ;
createNode transform -n "transform8" -p "pCube8";
	rename -uid "84669BD5-45C9-DB80-8202-03992C0EFC38";
	setAttr ".v" no;
createNode mesh -n "pCubeShape8" -p "transform8";
	rename -uid "FE6EBB15-4435-1E62-62A8-0BADADD66E92";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.5 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt[0:3]" -type "float3"  0 0 0.50945365 0 0 0.50945365 
		0 0 -0.39801076 0 0 -0.39801076;
createNode transform -n "pCube17" -p "group1";
	rename -uid "973A77D6-4090-9F07-5F6A-8D98718F6109";
	setAttr ".t" -type "double3" 0 -0.11208116301210203 2.2823800467919027 ;
	setAttr ".s" -type "double3" 0.36407306142350204 0.51761569543980535 0.51761569543980535 ;
createNode transform -n "transform7" -p "pCube17";
	rename -uid "747FCE33-418C-58BA-192F-E4AB78BD30C3";
	setAttr ".v" no;
createNode mesh -n "pCubeShape15" -p "transform7";
	rename -uid "C0FFB518-497C-DAAF-B660-599C2FF4AFE4";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.75 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt[16:19]" -type "float3"  0 0.92137527 -0.23570053 
		0 0.92137527 -0.23570053 0 0.95708746 -0.38569197 0 0.95708746 -0.38569197;
createNode transform -n "pCube18" -p "group1";
	rename -uid "A09C38DA-4664-715C-FC86-5AB5AFD99BEC";
	setAttr ".t" -type "double3" 0 0.073940792833933899 1.9964014065162192 ;
	setAttr ".s" -type "double3" 0.15422656515727901 0.54325350605690292 0.54325350605690292 ;
createNode transform -n "transform6" -p "pCube18";
	rename -uid "99D2D589-46E0-6FBF-7B2B-618159E96070";
	setAttr ".v" no;
createNode mesh -n "pCubeShape16" -p "transform6";
	rename -uid "0CA2955D-4885-C10A-046B-95809383D986";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.25 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
createNode transform -n "pCube19" -p "group1";
	rename -uid "C8179A77-4E1C-A899-E0EB-6A8D97D1B22D";
	setAttr ".t" -type "double3" 0 1.4419092096954955 -4.2111699586564759 ;
	setAttr ".s" -type "double3" 0.15648832866824108 0.15648832866824108 0.15648832866824108 ;
createNode transform -n "transform1" -p "pCube19";
	rename -uid "0414EDFC-47D7-EE1F-A66E-B093D4A32543";
	setAttr ".v" no;
createNode mesh -n "pCubeShape17" -p "transform1";
	rename -uid "7E5C98A3-4F79-6D13-6557-A7B01845AF7E";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
createNode transform -n "pCube20" -p "group1";
	rename -uid "45AA8864-4D70-92E9-670D-56ABA304DE59";
	setAttr ".t" -type "double3" 0 1.4419092096954955 -5.106035564964893 ;
	setAttr ".s" -type "double3" 0.15648832866824108 0.15648832866824108 0.15648832866824108 ;
createNode transform -n "transform2" -p "pCube20";
	rename -uid "76F409D6-4222-EDFA-A2A4-E1B37B069EE3";
	setAttr ".v" no;
createNode mesh -n "pCubeShape20" -p "transform2";
	rename -uid "F33B11C6-4EA4-F2D7-B9BF-81BC228515F9";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr ".iog[0].og[0].gcl" -type "componentList" 1 "f[0:5]";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr -s 14 ".uvst[0].uvsp[0:13]" -type "float2" 0.375 0 0.625 0 0.375
		 0.25 0.625 0.25 0.375 0.5 0.625 0.5 0.375 0.75 0.625 0.75 0.375 1 0.625 1 0.875 0
		 0.875 0.25 0.125 0 0.125 0.25;
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".vt[0:7]"  -0.5 -0.5 0.5 0.5 -0.5 0.5 -0.5 0.5 0.5 0.5 0.5 0.5
		 -0.5 0.5 -0.5 0.5 0.5 -0.5 -0.5 -0.5 -0.5 0.5 -0.5 -0.5;
	setAttr -s 12 ".ed[0:11]"  0 1 0 2 3 0 4 5 0 6 7 0 0 2 0 1 3 0 2 4 0
		 3 5 0 4 6 0 5 7 0 6 0 0 7 1 0;
	setAttr -s 6 -ch 24 ".fc[0:5]" -type "polyFaces" 
		f 4 0 5 -2 -5
		mu 0 4 0 1 3 2
		f 4 1 7 -3 -7
		mu 0 4 2 3 5 4
		f 4 2 9 -4 -9
		mu 0 4 4 5 7 6
		f 4 3 11 -1 -11
		mu 0 4 6 7 9 8
		f 4 -12 -10 -8 -6
		mu 0 4 1 10 11 3
		f 4 10 4 6 8
		mu 0 4 12 0 2 13;
	setAttr ".cd" -type "dataPolyComponent" Index_Data Edge 0 ;
	setAttr ".cvd" -type "dataPolyComponent" Index_Data Vertex 0 ;
	setAttr ".pd[0]" -type "dataPolyComponent" Index_Data UV 0 ;
	setAttr ".hfd" -type "dataPolyComponent" Index_Data Face 0 ;
createNode transform -n "pCube21" -p "group1";
	rename -uid "969E8FC2-43B8-5A23-6CFF-0585399C110B";
	setAttr ".t" -type "double3" 0 1.5278921827203977 -4.2125055478497684 ;
	setAttr ".s" -type "double3" 0.067692757377719751 0.067692757377719751 0.067692757377719751 ;
createNode transform -n "transform3" -p "pCube21";
	rename -uid "D3AD16CA-4FC6-30ED-7DFF-40B269E7E3A8";
	setAttr ".v" no;
createNode mesh -n "pCubeShape21" -p "transform3";
	rename -uid "9230CF78-423D-F40E-E9CA-919B47B80587";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.375 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[8:15]" -type "float3"  0 0 0.27871156 0 0 0.27871156 
		0 0 -0.42735761 0 0 -0.42735761 0 -7.2650819 -0.83613414 0 -7.2650819 -0.83613414 
		0 -6.4661088 -1.4121388 0 -6.4661088 -1.4121388;
createNode transform -n "pCube22" -p "group1";
	rename -uid "1F57A716-4515-C0FB-BB06-A98B8B9CE3A1";
	setAttr ".t" -type "double3" -0.32077428083493331 0 0 ;
	setAttr ".r" -type "double3" 0 0 20 ;
	setAttr ".rp" -type "double3" 0 1.6774949477931655 -4.658602761810684 ;
	setAttr ".sp" -type "double3" 0 1.6774949477931655 -4.658602761810684 ;
createNode transform -n "transform5" -p "pCube22";
	rename -uid "4AA1C899-4508-DE3A-54CC-39A3D1E757D2";
	setAttr ".v" no;
createNode mesh -n "pCube22Shape" -p "transform5";
	rename -uid "62951C09-48BF-4DBB-3F0A-C3B240DCF7DE";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
createNode transform -n "pCube23" -p "group1";
	rename -uid "53AEAF0D-4480-EA35-98E2-B5B534B41EE1";
	setAttr ".t" -type "double3" 0.32094200949149804 0 0 ;
	setAttr ".r" -type "double3" 0 0 -20 ;
	setAttr ".rp" -type "double3" 0 1.6774949477931655 -4.658602761810684 ;
	setAttr ".sp" -type "double3" 0 1.6774949477931655 -4.658602761810684 ;
createNode transform -n "transform4" -p "pCube23";
	rename -uid "A1BF9BDB-48BE-5236-E7A9-429B71EF9023";
	setAttr ".v" no;
createNode mesh -n "pCube23Shape" -p "transform4";
	rename -uid "67C5C431-4297-F4BF-AC91-179B6E225201";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr ".iog[0].og[0].gcl" -type "componentList" 1 "f[0:25]";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr -s 50 ".uvst[0].uvsp[0:49]" -type "float2" 0.375 0 0.625 0 0.375
		 0.25 0.625 0.25 0.375 0.5 0.625 0.5 0.375 0.75 0.625 0.75 0.375 1 0.625 1 0.875 0
		 0.875 0.25 0.125 0 0.125 0.25 0.375 0.25 0.625 0.25 0.625 0.5 0.375 0.5 0.375 0.25
		 0.625 0.25 0.625 0.5 0.375 0.5 0.375 0 0.625 0 0.625 0.25 0.375 0.25 0.625 0.5 0.375
		 0.5 0.625 0.75 0.375 0.75 0.625 1 0.375 1 0.875 0 0.875 0.25 0.125 0 0.125 0.25 0.375
		 0 0.625 0 0.625 0.25 0.375 0.25 0.625 0.5 0.375 0.5 0.625 0.75 0.375 0.75 0.625 1
		 0.375 1 0.875 0 0.875 0.25 0.125 0 0.125 0.25;
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 32 ".vt[0:31]"  -0.033846375 1.49404585 -4.17865801 0.033846375 1.49404585 -4.17865801
		 -0.033846375 1.9913249 -4.58351231 0.033846375 1.9913249 -4.58351231 -0.033846375 1.93708885 -4.62743187
		 0.033846375 1.93708885 -4.62743187 -0.033846375 1.49404585 -4.24635077 0.033846375 1.49404585 -4.24635077
		 -0.033846375 1.9913249 -5.055034637 0.033846375 1.9913249 -5.055034637 0.033846375 1.93708885 -5.02110672
		 -0.033846375 1.93708885 -5.02110672 -0.033846375 1.49953151 -5.13050127 0.033846375 1.49953151 -5.13050127
		 0.033846375 1.49938011 -5.087769032 -0.033846375 1.49938011 -5.087769032 -0.078244165 1.36366498 -5.0277915
		 0.078244165 1.36366498 -5.0277915 -0.078244165 1.5201534 -5.0277915 0.078244165 1.5201534 -5.0277915
		 -0.078244165 1.5201534 -5.18427992 0.078244165 1.5201534 -5.18427992 -0.078244165 1.36366498 -5.18427992
		 0.078244165 1.36366498 -5.18427992 -0.078244165 1.36366498 -4.13292599 0.078244165 1.36366498 -4.13292599
		 -0.078244165 1.5201534 -4.13292599 0.078244165 1.5201534 -4.13292599 -0.078244165 1.5201534 -4.28941441
		 0.078244165 1.5201534 -4.28941441 -0.078244165 1.36366498 -4.28941441 0.078244165 1.36366498 -4.28941441;
	setAttr -s 52 ".ed[0:51]"  0 1 0 2 3 0 4 5 0 6 7 0 0 2 0 1 3 0 2 4 0
		 3 5 0 4 6 0 5 7 0 6 0 0 7 1 0 2 8 0 3 9 0 8 9 0 5 10 0 9 10 0 4 11 0 11 10 0 8 11 0
		 8 12 0 9 13 0 12 13 0 10 14 0 13 14 0 11 15 0 15 14 0 12 15 0 16 17 0 18 19 0 20 21 0
		 22 23 0 16 18 0 17 19 0 18 20 0 19 21 0 20 22 0 21 23 0 22 16 0 23 17 0 24 25 0 26 27 0
		 28 29 0 30 31 0 24 26 0 25 27 0 26 28 0 27 29 0 28 30 0 29 31 0 30 24 0 31 25 0;
	setAttr -s 26 -ch 104 ".fc[0:25]" -type "polyFaces" 
		f 4 0 5 -2 -5
		mu 0 4 0 1 3 2
		f 4 22 24 -27 -28
		mu 0 4 18 19 20 21
		f 4 2 9 -4 -9
		mu 0 4 4 5 7 6
		f 4 3 11 -1 -11
		mu 0 4 6 7 9 8
		f 4 -12 -10 -8 -6
		mu 0 4 1 10 11 3
		f 4 10 4 6 8
		mu 0 4 12 0 2 13
		f 4 1 13 -15 -13
		mu 0 4 2 3 15 14
		f 4 7 15 -17 -14
		mu 0 4 3 5 16 15
		f 4 -3 17 18 -16
		mu 0 4 5 4 17 16
		f 4 -7 12 19 -18
		mu 0 4 4 2 14 17
		f 4 14 21 -23 -21
		mu 0 4 14 15 19 18
		f 4 16 23 -25 -22
		mu 0 4 15 16 20 19
		f 4 -19 25 26 -24
		mu 0 4 16 17 21 20
		f 4 -20 20 27 -26
		mu 0 4 17 14 18 21
		f 4 28 33 -30 -33
		mu 0 4 22 23 24 25
		f 4 29 35 -31 -35
		mu 0 4 25 24 26 27
		f 4 30 37 -32 -37
		mu 0 4 27 26 28 29
		f 4 31 39 -29 -39
		mu 0 4 29 28 30 31
		f 4 -40 -38 -36 -34
		mu 0 4 23 32 33 24
		f 4 38 32 34 36
		mu 0 4 34 22 25 35
		f 4 40 45 -42 -45
		mu 0 4 36 37 38 39
		f 4 41 47 -43 -47
		mu 0 4 39 38 40 41
		f 4 42 49 -44 -49
		mu 0 4 41 40 42 43
		f 4 43 51 -41 -51
		mu 0 4 43 42 44 45
		f 4 -52 -50 -48 -46
		mu 0 4 37 46 47 38
		f 4 50 44 46 48
		mu 0 4 48 36 39 49;
	setAttr ".cd" -type "dataPolyComponent" Index_Data Edge 0 ;
	setAttr ".cvd" -type "dataPolyComponent" Index_Data Vertex 0 ;
	setAttr ".pd[0]" -type "dataPolyComponent" Index_Data UV 0 ;
	setAttr ".hfd" -type "dataPolyComponent" Index_Data Face 0 ;
createNode transform -n "group5";
	rename -uid "D39B734D-44C9-06DF-069C-938F2A2DA69E";
	setAttr ".rp" -type "double3" 0 -0.05493236480005248 0.044196844100952148 ;
	setAttr ".sp" -type "double3" 0 -0.05493236480005248 0.044196844100952148 ;
createNode mesh -n "group5Shape" -p "group5";
	rename -uid "60DA3C2C-40D4-EDEA-3398-E9A5B50A1EE5";
	setAttr -k off ".v";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.36698895692825317 0.41810725629329681 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr -s 232 ".uvst[0].uvsp[0:231]" -type "float2" 0.085619286 0.11698535
		 0.085650697 0.12488662 0.015930064 0.12488656 0.048896998 0.050294213 0.076947495
		 0.067715384 0.056798637 0.10511798 0.062430359 0.11698535 0.070149407 0.11698535
		 0.074387714 0.11147551 0.083288223 0.11147551 0.13311885 0.11688657 0.13545559 0.11136323
		 0.14437787 0.11136323 0.14862658 0.11688657 0.15636888 0.11688657 0.16201 0.1049902
		 0.14181186 0.067496054 0.16993092 0.050032333 0.20297852 0.12480702 0.13308746 0.12480702
		 0.32600066 0.072842836 0.33450398 0.066087589 0.33788159 0.074241444 0.33543476 0.094740227
		 0.35348859 0.067485198 0.34941217 0.07179556 0.36886278 0.078084037 0.36362156 0.078667283
		 0.37515214 0.093691044 0.36746511 0.093691997 0.29071876 0.066062406 0.29923448 0.072827511
		 0.28978658 0.094756819 0.28733623 0.074228175 0.27578878 0.0717787 0.27170634 0.067462064
		 0.26155865 0.078660466 0.25630981 0.078076318 0.2577095 0.0937071 0.25001127 0.093706153
		 0.26845893 0.15612611 0.26845893 0.14067423 0.27779797 0.14067423 0.27779797 0.15612611
		 0.29688868 0.14067423 0.29688868 0.15612611 0.26171321 0.0467478 0.26171321 0.031262234
		 0.2854763 0.031262234 0.2854763 0.0467478 0.25004056 0.0467478 0.25004056 0.031262234
		 0.31237942 0.031262234 0.31237942 0.0467478 0.24997684 0.15612611 0.24997684 0.14067423
		 0.3126125 0.14067423 0.3126125 0.15612611 0.33582789 0.031262234 0.33582789 0.0467478
		 0.26593965 0.11858566 0.26294094 0.10938135 0.27332333 0.10938135 0.2733233 0.12490442
		 0.25683632 0.12490442 0.29655322 0.11862598 0.30572543 0.12499256 0.2891137 0.12499254
		 0.28911367 0.10935201 0.29957461 0.10935199 0.33587781 0.10938268 0.32812175 0.10938268
		 0.32812175 0.1016105 0.33587781 0.1016105 0.33587781 0.11717102 0.32812175 0.11717102
		 0.33587781 0.12497079 0.32812175 0.12497079 0.20312646 0.18728139 0.35935512 0.18728139
		 0.35935512 0.22643448 0.20312646 0.2264774 0.35935512 0.30463016 0.20312646 0.30463016
		 0.20312646 0.26552755 0.35935512 0.26552755 0.35935512 0.34362358 0.20312646 0.34362358
		 0.39848256 0.22643448 0.39848256 0.18728139 0.19887117 0.33946609 0.19887117 0.30894858
		 0.16407868 0.34362358 0.16823106 0.33946609 0.16407868 0.30463016 0.16823106 0.30894858
		 0.17206095 0.37512335 0.35929152 0.37512335 0.35929152 0.39083615 0.17206095 0.39083615
		 0.35929152 0.46094853 0.17206095 0.46094853 0.17206095 0.44537836 0.35929152 0.44537836
		 0.35929152 0.51581109 0.17206095 0.51581109 0.15651973 0.51581109 0.15651973 0.46094853
		 0.37468639 0.39083615 0.37468639 0.44537836 0.18755516 0.19533636 0.18755516 0.22667192
		 0.17976102 0.22667192 0.17135949 0.19533636 0.18755516 0.25800323 0.18755516 0.28899467
		 0.17170407 0.28899467 0.17976102 0.25800323 0.14839134 0.25800323 0.14839134 0.22667192
		 0.39059988 0.15629534 0.40006629 0.15629534 0.40345225 0.17190461 0.39059988 0.17190461
		 0.36530599 0.15629534 0.37495133 0.15629534 0.37495133 0.17190461 0.36190298 0.17190461
		 0.4217701 0.17183402 0.40624547 0.17183402 0.40624547 0.15626353 0.4217701 0.15626353
		 0.7108916 0.16406456 0.46934488 0.16406456 0.47657341 0.14849761 0.7108916 0.14849761
		 0.4687666 0.10943872 0.7108916 0.10943872 0.7108916 0.12498415 0.47657341 0.12498415
		 0.46084929 0.14849761 0.46084929 0.12498415 0.4687666 0.086122751 0.7108916 0.086122751
		 0.72657615 0.12498415 0.72657615 0.14849761 0.74218327 0.10933428 0.85953838 0.10933428
		 0.85953838 0.12492417 0.74218327 0.12492417 0.85953838 0.07031332 0.74218327 0.07031332
		 0.74218327 0.054622222 0.85953838 0.054622222 0.74218327 0.015698481 0.85953838 0.015698481
		 0.72691703 0.054622222 0.72691703 0.015698481 0.87528265 0.07031332 0.87528265 0.10933428
		 0.42976534 0.19532381 0.61727577 0.19532381 0.61727577 0.22659305 0.42976534 0.22659305
		 0.61727577 0.28913277 0.42976534 0.28913277 0.42976534 0.25780267 0.61727577 0.25780267
		 0.61727577 0.32022724 0.42976534 0.32022724 0.64850157 0.22659305 0.64850157 0.19532381
		 0.43781233 0.33595002 0.46103162 0.33595002 0.46103162 0.35151336 0.45307392 0.35151336
		 0.46103162 0.38272592 0.4379656 0.38272592 0.45307392 0.36724705 0.46103162 0.36724705
		 0.42965126 0.36724705 0.42965126 0.35151336 0.47641388 0.35151336 0.47641388 0.36724705
		 0.10175242 0.078144312 0.11716683 0.078144312 0.11716683 0.085900538 0.10175242 0.085900538
		 0.11716683 0.093827538 0.10175242 0.093827538 0.11716683 0.10155322 0.10175242 0.10155322
		 0.10175242 0.023500051 0.14067559 0.031254329 0.11716683 0.062480912 0.10175242 0.062480912
		 0.17191124 0.015648101 0.17191124 0.031254329 0.10175242 0.10942077 0.11716683 0.10942077
		 0.11716683 0.11718297 0.10175242 0.11718297 0.23461433 0.12521338 0.21883427 0.12521338
		 0.21883427 0.046917051 0.23461433 0.046917051 0.11716683 0.023500051 0.14067559 0.015648101
		 0.50004613 0.33592275 0.53899634 0.33592275 0.53899634 0.35158622 0.51399446 0.35158622
		 0.53899634 0.37482074 0.50038964 0.37482074 0.51399446 0.35934255 0.53899634 0.35934255
		 0.57027572 0.33579305 0.60938483 0.33579305 0.60938483 0.35155255 0.58407915 0.35155255
		 0.60938483 0.37488204 0.57038593 0.37488204 0.58407915 0.35943168 0.60938483 0.35943168
		 0.50024879 0.35934255 0.50024879 0.35158622 0.57043666 0.35943168 0.57043666 0.35155255
		 0.55482554 0.35158622 0.55482554 0.35934255 0.62505192 0.35155255 0.62505192 0.35943168;
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 146 ".vt[0:145]"  -0.26370749 -1.47870767 4.16673851 0.26370749 -1.47870767 4.16673851
		 -0.26370749 -0.24069023 3.49981642 0.26370749 -0.24069023 3.49981642 -0.26370749 0.41364491 2.14707661
		 0.26370749 0.41364491 2.14707661 -0.26370749 -2.055343151 3.23827457 0.26370749 -2.055343151 3.23827457
		 -0.26370749 0.15211594 3.67929959 0.26370749 0.15211594 3.67929959 -0.26370749 0.15211594 3.94172239
		 0.26370749 0.15211594 3.94172239 -0.26370749 0.15211594 4.45377254 0.26370749 0.15211594 4.45377254
		 0.26370749 0.41364515 4.45481014 -0.26370749 0.41364515 4.45481014 -0.26370749 -0.030258298 4.082010269
		 0.26370749 -0.030258298 4.082010269 0.26370749 -0.030258298 4.37661457 -0.26370749 -0.030258298 4.37661457
		 -0.60629612 0.32652926 5.11106586 0.60629612 0.32652926 5.11106586 -0.60629612 1.53912139 5.11106586
		 0.60629612 1.53912139 5.11106586 -0.60629612 1.53912139 0.19130993 0.60629612 1.53912139 0.19130993
		 -0.60629612 0.32652926 0.19130993 0.60629612 0.32652926 0.19130993 -0.47611052 0.5627712 5.11106586
		 0.47611052 0.5627712 5.11106586 0.47611052 1.40893579 5.11106586 -0.47611052 1.40893579 5.11106586
		 -0.47611052 0.5627712 5.60202646 0.47611052 0.5627712 5.60202646 0.47611052 1.40893579 5.34427214
		 -0.47611052 1.40893579 5.34427214 -0.72994506 0.43829241 5.68884611 0.72994506 0.43829241 5.68884611
		 -0.72994506 0.88329244 5.68884611 0.72994506 0.88329244 5.68884611 -0.72994506 0.88329244 0.20979142
		 0.72994506 0.88329244 0.20979142 -0.72994506 0.43829241 0.20979142 0.72994506 0.43829241 0.20979142
		 -0.44824284 0.52665776 0.22412777 0.44824284 0.52665776 0.22412777 -0.44824284 1.42314339 0.22412777
		 0.44824284 1.42314339 0.22412777 -0.44824284 1.42314339 -5.31154394 0.44824284 1.42314339 -5.31154394
		 -0.44824284 0.52665776 -5.31154394 0.44824284 0.52665776 -5.31154394 -0.25415349 -0.24294773 0.87046993
		 0.25415349 -0.24294773 0.87046993 -0.25415349 0.36621103 0.87046993 0.25415349 0.36621103 0.87046993
		 -0.25415349 0.36621103 0.36216295 0.25415349 0.36621103 0.36216295 -0.25415349 -0.24294773 0.49663201
		 0.25415349 -0.24294773 0.49663201 -0.31393042 -0.33555466 0.93848908 0.31393042 -0.33555466 0.93848908
		 -0.31393042 0.052603491 0.96231794 0.31393042 0.052603491 0.96231794 -0.31393042 0.54867226 -5.31769562
		 0.31393042 0.54867226 -5.31769562 -0.31393042 0.20049888 -5.52326679 0.31393042 0.20049888 -5.52326679
		 -0.5 -0.48560983 -2.52718306 0.5 -0.48560983 -2.52718306 -0.5 -0.068229437 -2.47936583
		 0.5 -0.068229437 -2.47936583 -0.5 0.46928489 -5.51424122 0.5 0.46928489 -5.51424122
		 -0.5 0.051904548 -5.60045242 0.5 0.051904548 -5.60045242 -0.19100496 1.40241683 0.099778473
		 0.19100496 1.40241683 0.099778473 -0.19100496 1.78442681 -0.28415281 0.19100496 1.78442681 -0.28415281
		 -0.19100496 1.78442681 -0.4768478 0.19100496 1.78442681 -0.4768478 -0.19100496 1.40241683 -0.4768478
		 0.19100496 1.40241683 -0.4768478 -0.15436545 -0.31173629 2.50052047 0.15436545 -0.31173629 2.50052047
		 -0.15436545 0.38333717 2.2010603 0.15436545 0.38333717 2.2010603 -0.15436545 -0.26734185 2.12339234
		 0.15436545 -0.26734185 2.12339234 -0.15436545 -0.52616447 2.23060656 0.15436545 -0.52616447 2.23060656
		 -0.15436545 -0.34497964 1.75738549 0.15436545 -0.34497964 1.75738549 0.15436545 -0.48180008 1.62798929
		 -0.15436545 -0.48180008 1.62798929 -0.15436545 -0.12685435 1.30634654 0.15436545 -0.12685435 1.30634654
		 0.15436545 -0.14536947 1.13998008 -0.15436545 -0.14536947 1.13998008 -0.15436545 0.35006392 1.18434417
		 0.15436545 0.35006392 1.18434417 0.15436545 0.350034 0.94033992 -0.15436545 0.350034 0.94033992
		 -0.077113286 -0.19768596 2.26802826 0.077113286 -0.19768596 2.26802826 -0.077113286 0.12004818 2.013143778
		 0.077113286 0.12004818 2.013143778 -0.077113286 -0.19768596 1.90962672 0.077113286 -0.19768596 1.90962672
		 -0.077113286 0.33817345 2.26802826 0.077113286 0.33817345 2.26802826 0.077113286 0.33817345 1.69889545
		 -0.077113286 0.33817345 1.69889545 -0.28696364 1.35583019 -5.0277915 -0.13991269 1.4093523 -5.0277915
		 -0.28696364 1.35583019 -5.18427992 -0.13991269 1.4093523 -5.18427992 -0.28696364 1.35583019 -4.13292599
		 -0.13991269 1.4093523 -4.13292599 -0.28696364 1.35583019 -4.28941441 -0.13991269 1.4093523 -4.28941441
		 0.14008042 1.4093523 -5.0277915 0.28713137 1.35583019 -5.0277915 0.14008042 1.4093523 -5.18427992
		 0.28713137 1.35583019 -5.18427992 0.14008042 1.4093523 -4.13292599 0.28713137 1.35583019 -4.13292599
		 0.14008042 1.4093523 -4.28941441 0.28713137 1.35583019 -4.28941441 -0.28893206 1.36946905 -4.13850021
		 -0.13090497 1.42698622 -4.13850021 -0.42338282 1.78360009 -4.49688148 -0.26535574 1.84111726 -4.49688148
		 -0.42523074 1.78867722 -5.14589977 -0.26720366 1.84619439 -5.14589977 -0.28893206 1.36946905 -5.17471027
		 -0.13090497 1.42698622 -5.17471027 0.1071003 1.42698622 -4.13850021 0.26512739 1.36946905 -4.13850021
		 0.25104129 1.84200084 -4.49688148 0.40906838 1.78448367 -4.49688148 0.25288922 1.84707797 -5.14589977
		 0.4109163 1.78956079 -5.14589977 0.1071003 1.42698622 -5.17471027 0.26512739 1.36946905 -5.17471027;
	setAttr -s 221 ".ed";
	setAttr ".ed[0:165]"  0 1 0 2 3 0 4 5 0 6 7 0 0 2 0 1 3 0 4 6 0 5 7 0 6 0 0
		 7 1 0 2 8 0 3 9 0 8 9 0 8 10 0 9 11 0 10 11 0 12 13 0 5 14 0 13 14 0 4 15 0 15 14 0
		 12 15 0 10 16 0 11 17 0 16 17 0 13 18 0 17 18 0 12 19 0 19 18 0 16 19 0 20 21 0 22 23 0
		 24 25 0 26 27 0 20 22 0 21 23 0 22 24 0 23 25 0 24 26 0 25 27 0 26 20 0 27 21 0 20 28 0
		 21 29 0 28 29 0 23 30 0 29 30 0 22 31 0 31 30 0 28 31 0 28 32 0 29 33 0 32 33 0 30 34 0
		 33 34 0 31 35 0 35 34 0 32 35 0 36 37 0 38 39 0 40 41 0 42 43 0 36 38 0 37 39 0 38 40 0
		 39 41 0 40 42 0 41 43 0 42 36 0 43 37 0 44 45 0 46 47 0 48 49 0 50 51 0 44 46 0 45 47 0
		 46 48 0 47 49 0 48 50 0 49 51 0 50 44 0 51 45 0 52 53 0 54 55 0 56 57 0 58 59 0 52 54 0
		 53 55 0 54 56 0 55 57 0 56 58 0 57 59 0 58 52 0 59 53 0 60 61 0 62 63 0 64 65 0 66 67 0
		 60 62 0 61 63 0 62 64 0 63 65 0 64 66 0 65 67 0 66 60 0 67 61 0 68 69 0 70 71 0 72 73 0
		 74 75 0 68 70 0 69 71 0 70 72 0 71 73 0 72 74 0 73 75 0 74 68 0 75 69 0 76 77 0 78 79 0
		 80 81 0 82 83 0 76 78 0 77 79 0 78 80 0 79 81 0 80 82 0 81 83 0 82 76 0 83 77 0 84 85 0
		 86 87 0 88 89 0 90 91 0 84 86 0 85 87 0 86 88 0 87 89 0 88 90 0 89 91 0 90 84 0 91 85 0
		 88 92 0 89 93 0 92 93 0 91 94 0 93 94 0 90 95 0 95 94 0 92 95 0 92 96 0 93 97 0 96 97 0
		 94 98 0 97 98 0 95 99 0 99 98 0 96 99 0 96 100 0 97 101 0 100 101 0 98 102 0 101 102 0
		 99 103 0 103 102 0 100 103 0;
	setAttr ".ed[166:220]" 104 105 0 106 107 0 108 109 0 106 108 0 107 109 0 108 104 0
		 109 105 0 104 110 0 105 111 0 110 111 0 107 112 0 111 112 0 106 113 0 113 112 0 110 113 0
		 114 115 0 116 117 0 116 114 0 117 115 0 118 119 0 120 121 0 120 118 0 121 119 0 122 123 0
		 124 125 0 124 122 0 125 123 0 126 127 0 128 129 0 128 126 0 129 127 0 130 131 0 132 133 0
		 134 135 0 136 137 0 130 132 0 131 133 0 132 134 0 133 135 0 134 136 0 135 137 0 136 130 0
		 137 131 0 138 139 0 140 141 0 142 143 0 144 145 0 138 140 0 139 141 0 140 142 0 141 143 0
		 142 144 0 143 145 0 144 138 0 145 139 0;
	setAttr -s 103 -ch 426 ".fc[0:102]" -type "polyFaces" 
		f 4 0 5 -2 -5
		mu 0 4 192 206 194 195
		f 4 16 18 -21 -22
		mu 0 4 198 199 200 201
		f 4 2 7 -4 -7
		mu 0 4 202 203 204 205
		f 4 3 9 -1 -9
		mu 0 4 196 197 193 207
		f 10 21 -20 6 8 4 10 13 22 29 -28
		mu 0 10 0 1 2 3 4 5 6 7 8 9
		f 4 1 11 -13 -11
		mu 0 4 195 194 185 184
		f 4 -3 19 20 -18
		f 4 12 14 -16 -14
		mu 0 4 184 185 186 187
		f 4 24 26 -29 -30
		mu 0 4 189 188 190 191
		f 4 15 23 -25 -23
		mu 0 4 187 186 188 189
		f 10 25 -27 -24 -15 -12 -6 -10 -8 17 -19
		mu 0 10 10 11 12 13 14 15 16 17 18 19
		f 4 -17 27 28 -26
		mu 0 4 199 198 191 190
		f 4 52 54 -57 -58
		mu 0 4 118 119 112 117
		f 4 31 37 -33 -37
		mu 0 4 84 81 80 85
		f 4 32 39 -34 -39
		mu 0 4 88 80 79 89
		f 4 33 41 -31 -41
		mu 0 4 82 86 87 83
		f 4 -42 -40 -38 -36
		mu 0 4 78 79 80 81
		f 4 40 34 36 38
		mu 0 4 82 83 84 85
		f 4 30 43 -45 -43
		mu 0 4 83 87 90 91
		f 4 35 45 -47 -44
		mu 0 4 87 92 93 90
		f 4 -32 47 48 -46
		mu 0 4 92 94 95 93
		f 4 -35 42 49 -48
		mu 0 4 94 83 91 95
		f 4 44 51 -53 -51
		f 4 46 53 -55 -52
		mu 0 4 110 111 112 113
		f 4 -49 55 56 -54
		mu 0 4 111 114 117 112
		f 4 -50 50 57 -56
		mu 0 4 114 115 116 117
		f 4 58 63 -60 -63
		mu 0 4 101 105 106 107
		f 4 59 65 -61 -65
		mu 0 4 102 99 98 103
		f 4 60 67 -62 -67
		mu 0 4 103 98 108 109
		f 4 61 69 -59 -69
		mu 0 4 100 104 105 101
		f 4 -70 -68 -66 -64
		mu 0 4 96 97 98 99
		f 4 68 62 64 66
		mu 0 4 100 101 102 103
		f 4 70 75 -72 -75
		f 4 71 77 -73 -77
		mu 0 4 166 163 162 167
		f 4 72 79 -74 -79
		mu 0 4 170 162 161 171
		f 4 73 81 -71 -81
		mu 0 4 164 168 169 165
		f 4 -82 -80 -78 -76
		mu 0 4 160 161 162 163
		f 4 80 74 76 78
		mu 0 4 164 165 166 167
		f 4 82 87 -84 -87
		mu 0 4 125 120 123 126
		f 4 83 89 -85 -89
		f 4 84 91 -86 -91
		mu 0 4 128 129 130 131
		f 4 85 93 -83 -93
		f 4 -94 -92 -90 -88
		mu 0 4 120 121 122 123
		f 4 92 86 88 90
		mu 0 4 124 125 126 127
		f 4 94 99 -96 -99
		mu 0 4 144 145 135 138
		f 4 95 101 -97 -101
		mu 0 4 138 135 134 139
		f 4 96 103 -98 -103
		mu 0 4 139 134 140 141
		f 4 97 105 -95 -105
		mu 0 4 136 142 143 137
		f 4 -106 -104 -102 -100
		mu 0 4 132 133 134 135
		f 4 104 98 100 102
		mu 0 4 136 137 138 139
		f 4 106 111 -108 -111
		mu 0 4 156 157 154 152
		f 4 107 113 -109 -113
		mu 0 4 152 154 155 153
		f 4 108 115 -110 -115
		mu 0 4 158 159 147 150
		f 4 109 117 -107 -117
		mu 0 4 150 147 146 151
		f 4 -118 -116 -114 -112
		mu 0 4 146 147 148 149
		f 4 116 110 112 114
		mu 0 4 150 151 152 153
		f 4 118 123 -120 -123
		mu 0 4 180 181 175 178
		f 4 119 125 -121 -125
		mu 0 4 178 175 174 179
		f 4 120 127 -122 -127
		mu 0 4 179 174 182 183
		f 4 121 129 -119 -129
		f 4 -130 -128 -126 -124
		mu 0 4 172 173 174 175
		f 4 128 122 124 126
		mu 0 4 176 177 178 179
		f 4 130 135 -132 -135
		f 4 131 137 -133 -137
		mu 0 4 54 55 41 40
		f 4 160 162 -165 -166
		f 4 133 141 -131 -141
		mu 0 4 47 46 50 51
		f 4 -142 -140 -138 -136
		mu 0 4 20 21 22 23
		f 4 140 134 136 138
		mu 0 4 30 31 32 33
		f 4 132 143 -145 -143
		mu 0 4 40 41 42 43
		f 4 139 145 -147 -144
		mu 0 4 22 21 24 25
		f 4 -134 147 148 -146
		mu 0 4 46 47 48 49
		f 4 -139 142 149 -148
		mu 0 4 30 33 34 35
		f 4 144 151 -153 -151
		mu 0 4 43 42 44 45
		f 4 146 153 -155 -152
		mu 0 4 25 24 26 27
		f 4 -149 155 156 -154
		mu 0 4 49 48 52 53
		f 4 -150 150 157 -156
		mu 0 4 35 34 36 37
		f 4 152 159 -161 -159
		mu 0 4 45 44 56 57
		f 4 154 161 -163 -160
		mu 0 4 27 26 28 29
		f 4 -157 163 164 -162
		mu 0 4 53 52 58 59
		f 4 -158 158 165 -164
		mu 0 4 37 36 38 39
		f 4 175 177 -180 -181
		f 4 167 170 -169 -170
		mu 0 4 74 75 71 70
		f 4 168 172 -167 -172
		mu 0 4 70 71 72 73
		f 5 169 171 173 180 -179
		mu 0 5 60 61 62 63 64
		f 4 174 -176 -174 166
		f 5 176 -178 -175 -173 -171
		mu 0 5 65 66 67 68 69
		f 4 -168 178 179 -177
		mu 0 4 75 74 76 77
		f 4 182 184 -182 -184
		f 4 186 188 -186 -188
		f 4 190 192 -190 -192
		f 4 194 196 -194 -196
		f 4 197 202 -199 -202
		mu 0 4 224 225 211 214
		f 4 198 204 -200 -204
		mu 0 4 214 211 210 215
		f 4 199 206 -201 -206
		mu 0 4 215 210 228 229
		f 4 200 208 -198 -208
		f 4 -209 -207 -205 -203
		mu 0 4 208 209 210 211
		f 4 207 201 203 205
		mu 0 4 212 213 214 215
		f 4 209 214 -211 -214
		mu 0 4 226 227 219 222
		f 4 210 216 -212 -216
		mu 0 4 222 219 218 223
		f 4 211 218 -213 -218
		mu 0 4 223 218 230 231
		f 4 212 220 -210 -220
		f 4 -221 -219 -217 -215
		mu 0 4 216 217 218 219
		f 4 219 213 215 217
		mu 0 4 220 221 222 223;
	setAttr ".cd" -type "dataPolyComponent" Index_Data Edge 0 ;
	setAttr ".cvd" -type "dataPolyComponent" Index_Data Vertex 0 ;
	setAttr ".pd[0]" -type "dataPolyComponent" Index_Data UV 0 ;
	setAttr ".hfd" -type "dataPolyComponent" Index_Data Face 0 ;
createNode lightLinker -s -n "lightLinker1";
	rename -uid "FA6C8A0E-43C4-80AE-1177-1FB2104F0082";
	setAttr -s 4 ".lnk";
	setAttr -s 4 ".slnk";
createNode shapeEditorManager -n "shapeEditorManager";
	rename -uid "8F17AEC0-4E7C-4395-3488-F5B9C9B75056";
createNode poseInterpolatorManager -n "poseInterpolatorManager";
	rename -uid "FB4D395B-41FB-25ED-9118-B9BD34CB1CF8";
createNode displayLayerManager -n "layerManager";
	rename -uid "BFCC5FC2-41D7-9AEE-8ABD-A791F9696F1F";
	setAttr ".cdl" 1;
	setAttr -s 2 ".dli[1]"  1;
	setAttr -s 2 ".dli";
createNode displayLayer -n "defaultLayer";
	rename -uid "F4F1AE37-4CDD-363A-BA04-A19251DAD408";
createNode renderLayerManager -n "renderLayerManager";
	rename -uid "9E35689C-408A-55F4-A016-0B980E4A8785";
createNode renderLayer -n "defaultRenderLayer";
	rename -uid "6969C4C6-434C-3D6C-DCA8-8F91B62734A8";
	setAttr ".g" yes;
createNode displayLayer -n "layer1";
	rename -uid "A2934A8A-4AB9-0584-00D8-F4866AB890C9";
	setAttr ".dt" 2;
	setAttr ".do" 1;
createNode polyCube -n "polyCube1";
	rename -uid "9C60FAAF-480B-E13A-3D04-22ADF92A0138";
	setAttr ".cuv" 4;
createNode polyExtrudeFace -n "polyExtrudeFace1";
	rename -uid "2092A6A1-4194-263C-A98A-7F8629EBFC01";
	setAttr ".ics" -type "componentList" 1 "f[1]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 0.85107529620271793 -0.52504365551204968 0
		 0 0.52504365551204968 0.85107529620271793 0 0 -1.3156913456617667 3.4786789038614772 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.051406026 2.855011 ;
	setAttr ".rs" 48544;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.5 -0.24068955751222854 2.2102058367226265 ;
	setAttr ".cbx" -type "double3" 0.5 0.3435016113058329 3.4998159238451492 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak1";
	rename -uid "E85925E4-4878-A4C4-421C-11B76D86F79A";
	setAttr ".uopa" yes;
	setAttr -s 6 ".tk[2:7]" -type "float3"  0 0.40380961 0.082412086 0
		 0.40380961 0.082412086 0 1.57810187 0.29158264 0 1.57810187 0.29158264 0 -0.0032766229
		 -0.092951648 0 -0.0032766229 -0.092951648;
createNode polyTweak -n "polyTweak2";
	rename -uid "974740F4-47C9-2F6E-C887-D9AD1E129BB8";
	setAttr ".uopa" yes;
	setAttr -s 6 ".tk";
	setAttr ".tk[4]" -type "float3" 0 0.092843495 -0.016899355 ;
	setAttr ".tk[5]" -type "float3" 0 0.092843495 -0.016899355 ;
	setAttr ".tk[8]" -type "float3" 0 0.26801592 0.31369612 ;
	setAttr ".tk[9]" -type "float3" 0 0.26801592 0.31369612 ;
	setAttr ".tk[10]" -type "float3" 0 -0.68423975 1.2427226 ;
	setAttr ".tk[11]" -type "float3" 0 -0.68423975 1.2427226 ;
createNode deleteComponent -n "deleteComponent1";
	rename -uid "6E360993-4039-9347-5D02-229A72D0F9F4";
	setAttr ".dc" -type "componentList" 1 "e[6:7]";
createNode polyExtrudeFace -n "polyExtrudeFace2";
	rename -uid "3AB4F0C9-4D5E-66D4-308E-C4BB057940EC";
	setAttr ".ics" -type "componentList" 1 "f[1]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 0.85107529620271793 -0.52504365551204968 0
		 0 0.52504365551204968 0.85107529620271793 0 0 -1.3156913456617667 3.4786789038614772 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.28288093 3.6265936 ;
	setAttr ".rs" 42862;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.5 0.15211621040922951 3.6260749171333444 ;
	setAttr ".cbx" -type "double3" 0.5 0.41364566894771726 3.6271120538808481 ;
	setAttr ".raf" no;
createNode polyExtrudeFace -n "polyExtrudeFace3";
	rename -uid "6548D0B7-4A73-77B7-90E8-C19D97F7E8C5";
	setAttr ".ics" -type "componentList" 1 "f[1]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 0.85107529620271793 -0.52504365551204968 0
		 0 0.52504365551204968 0.85107529620271793 0 0 -1.3156913456617667 3.4786789038614772 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.28288078 3.942241 ;
	setAttr ".rs" 38250;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.5 0.15211607807771954 3.9417223744169383 ;
	setAttr ".cbx" -type "double3" 0.5 0.41364549775020687 3.9427596752106044 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak3";
	rename -uid "2790ABB7-4238-112D-FC0C-B0808E971AB9";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[12:15]" -type "float3"  0 -0.16572876 0.26863992 0
		 -0.16572876 0.26863992 0 -0.16572876 0.26863992 0 -0.16572876 0.26863992;
createNode polyExtrudeFace -n "polyExtrudeFace4";
	rename -uid "09CCECB8-4978-451D-2CFD-1CBA463AB768";
	setAttr ".ics" -type "componentList" 1 "f[12]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 0.85107529620271793 -0.52504365551204968 0
		 0 0.52504365551204968 0.85107529620271793 0 0 -1.3156913456617667 3.4786789038614772 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.152116 4.1977477 ;
	setAttr ".rs" 55018;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.5 0.15211598394277925 3.941722272960857 ;
	setAttr ".cbx" -type "double3" 0.5 0.1521160154876382 4.4537727783942875 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak4";
	rename -uid "8F8C4B59-4677-3AF6-75A5-A58E05DA7033";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[16:19]" -type "float3"  0 -0.26884893 0.43579352 0
		 -0.26884893 0.43579352 0 -0.26884893 0.43579352 0 -0.26884893 0.43579352;
createNode polyTweak -n "polyTweak5";
	rename -uid "C7509316-4852-9FF0-692D-F9B3EE74C82B";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[20:23]" -type "float3"  0 -0.22887138 0.023641124
		 0 -0.22887138 0.023641124 0 -0.11470271 -0.16142194 0 -0.11470271 -0.16142194;
createNode deleteComponent -n "deleteComponent2";
	rename -uid "F9C8D67F-42BB-D77D-5FF4-D2BA438A4DF7";
	setAttr ".dc" -type "componentList" 4 "e[14]" "e[17]" "e[22]" "e[25:27]";
createNode deleteComponent -n "deleteComponent3";
	rename -uid "86B7886D-444E-E464-196F-AA81BFE1DA14";
	setAttr ".dc" -type "componentList" 2 "vtx[10:11]" "vtx[14:15]";
createNode deleteComponent -n "deleteComponent4";
	rename -uid "BAC72BDC-4526-333E-5EAA-ED901BE7C21A";
	setAttr ".dc" -type "componentList" 2 "e[15]" "e[21]";
createNode deleteComponent -n "deleteComponent5";
	rename -uid "FAEFA11B-4472-A87B-02CA-E891D8BD9470";
	setAttr ".dc" -type "componentList" 2 "vtx[10:11]" "vtx[14:15]";
createNode polyCube -n "polyCube2";
	rename -uid "4D39B3FC-4309-1982-FD4B-58952B169CF1";
	setAttr ".cuv" 4;
createNode polyExtrudeFace -n "polyExtrudeFace5";
	rename -uid "1DAB0055-413F-D771-C5DD-1C9A76C53C9C";
	setAttr ".ics" -type "componentList" 1 "f[0]";
	setAttr ".ix" -type "matrix" 1.2125922352957998 0 0 0 0 1.2125922352957998 0 0 0 0 4.9197559987144626 0
		 0 0.93282540049497009 2.6511879803541265 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.93282539 5.1110659 ;
	setAttr ".rs" 44869;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.60629611764789992 0.32652928284707017 5.1110658330912031 ;
	setAttr ".cbx" -type "double3" 0.60629611764789992 1.5391215181428701 5.1110659797113573 ;
	setAttr ".raf" no;
createNode polyExtrudeFace -n "polyExtrudeFace6";
	rename -uid "EF173996-4132-A76A-7CC8-92B794D94A83";
	setAttr ".ics" -type "componentList" 1 "f[0]";
	setAttr ".ix" -type "matrix" 1.2125922352957998 0 0 0 0 1.2125922352957998 0 0 0 0 4.9197559987144626 0
		 0 0.93282540049497009 2.6511879803541265 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.93282539 5.1110659 ;
	setAttr ".rs" 40984;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.47611047411723834 0.45671492637773176 5.1110659797113573 ;
	setAttr ".cbx" -type "double3" 0.47611047411723834 1.4089358384741437 5.1110659797113573 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak6";
	rename -uid "22077345-4314-EC69-E68E-6396C1B6029A";
	setAttr ".uopa" yes;
	setAttr -s 6 ".tk";
	setAttr ".tk[0]" -type "float3" 0 0 -1.5832484e-08 ;
	setAttr ".tk[1]" -type "float3" 0 0 -1.5832484e-08 ;
	setAttr ".tk[8]" -type "float3" 0.10736144 0.10736144 -3.565555e-09 ;
	setAttr ".tk[9]" -type "float3" -0.10736144 0.10736144 -3.565555e-09 ;
	setAttr ".tk[10]" -type "float3" -0.10736144 -0.10736141 -3.565555e-09 ;
	setAttr ".tk[11]" -type "float3" 0.10736144 -0.10736141 -3.565555e-09 ;
createNode polyCube -n "polyCube3";
	rename -uid "3A1B0563-4B28-D3AE-C139-4AAE98F83C5E";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube4";
	rename -uid "3B180B21-4E79-23DD-129A-C2B978C11F35";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube5";
	rename -uid "ED08F93F-4532-A5DD-5551-AF830A4AB005";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube6";
	rename -uid "2F63B29E-4825-5461-673A-28AA909DE5B9";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube7";
	rename -uid "0C4CEF22-4327-4CE4-4CAB-B5B18673797D";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube8";
	rename -uid "3649FF24-4170-0768-DFDB-4EA40C2185B5";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube11";
	rename -uid "E71BF60A-404C-7E83-5249-0B8E955FFF78";
	setAttr ".cuv" 4;
createNode polyExtrudeFace -n "polyExtrudeFace11";
	rename -uid "03B3493D-4B99-37F4-CE4B-1CB16CC56E93";
	setAttr ".ics" -type "componentList" 1 "f[2]";
	setAttr ".ix" -type "matrix" 0.36407306142350204 0 0 0 0 0.51761569543980535 0 0
		 0 0 0.51761569543980535 0 0 -0.11208116301210203 2.2823800467919027 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -0.39675325 2.1769993 ;
	setAttr ".rs" 61096;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.18203653071175102 -0.52616468581060682 2.1233922538708874 ;
	setAttr ".cbx" -type "double3" 0.18203653071175102 -0.26734184387307086 2.230606432510136 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak11";
	rename -uid "755108B0-47A8-F061-8E6F-DD84754B9B30";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk[0:7]" -type "float3"  0 0.11427905 -0.078566842
		 0 0.11427905 -0.078566842 0 0.4571164 -0.65710461 0 0.4571164 -0.65710461 0 -0.79995358
		 0.1928459 0 -0.79995358 0.1928459 0 -0.29998252 0.39997673 0 -0.29998252 0.39997673;
createNode polyExtrudeFace -n "polyExtrudeFace12";
	rename -uid "A709157F-48AE-A9F6-0015-ABBC07C174FB";
	setAttr ".ics" -type "componentList" 1 "f[2]";
	setAttr ".ix" -type "matrix" 0.36407306142350204 0 0 0 0 0.51761569543980535 0 0
		 0 0 0.51761569543980535 0 0 -0.11208116301210203 2.2823800467919027 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -0.41338992 1.6926873 ;
	setAttr ".rs" 40101;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.18203653071175102 -0.4818001587345353 1.6279892577627293 ;
	setAttr ".cbx" -type "double3" 0.18203653071175102 -0.34497966598622209 1.7573852834339685 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak12";
	rename -uid "38050AE5-494A-00E3-45B0-DEBE8AB3C6FC";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[8:11]" -type "float3"  0 -0.14999124 -0.7071017 0
		 -0.14999124 -0.7071017 0 0.085709289 -1.16421735 0 0.085709289 -1.16421735;
createNode polyExtrudeFace -n "polyExtrudeFace13";
	rename -uid "D663B658-4359-BDF6-2713-39882BD1C1D7";
	setAttr ".ics" -type "componentList" 1 "f[2]";
	setAttr ".ix" -type "matrix" 0.36407306142350204 0 0 0 0 0.51761569543980535 0 0
		 0 0 0.51761569543980535 0 0 -0.11208116301210203 2.2823800467919027 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -0.1361119 1.2231632 ;
	setAttr ".rs" 34873;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.18203653071175102 -0.14536946769296483 1.1399799844150371 ;
	setAttr ".cbx" -type "double3" 0.18203653071175102 -0.12685433934444268 1.3063464441742858 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak13";
	rename -uid "7806F8D0-46B9-9013-4747-B2AD4FAA43E4";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[12:15]" -type "float3"  0 0.42140397 -0.87137794 0
		 0.42140397 -0.87137794 0 0.64996225 -0.94280225 0 0.64996225 -0.94280225;
createNode polyCube -n "polyCube12";
	rename -uid "E1D44E6D-4A1E-CA06-3CC0-6FB47C77F518";
	setAttr ".cuv" 4;
createNode script -n "uiConfigurationScriptNode";
	rename -uid "BDE3FA0F-4BDB-32D3-0B9D-CE86B77551D1";
	setAttr ".b" -type "string" (
		"// Maya Mel UI Configuration File.\n//\n//  This script is machine generated.  Edit at your own risk.\n//\n//\n\nglobal string $gMainPane;\nif (`paneLayout -exists $gMainPane`) {\n\n\tglobal int $gUseScenePanelConfig;\n\tint    $useSceneConfig = $gUseScenePanelConfig;\n\tint    $nodeEditorPanelVisible = stringArrayContains(\"nodeEditorPanel1\", `getPanel -vis`);\n\tint    $nodeEditorWorkspaceControlOpen = (`workspaceControl -exists nodeEditorPanel1Window` && `workspaceControl -q -visible nodeEditorPanel1Window`);\n\tint    $menusOkayInPanels = `optionVar -q allowMenusInPanels`;\n\tint    $nVisPanes = `paneLayout -q -nvp $gMainPane`;\n\tint    $nPanes = 0;\n\tstring $editorName;\n\tstring $panelName;\n\tstring $itemFilterName;\n\tstring $panelConfig;\n\n\t//\n\t//  get current state of the UI\n\t//\n\tsceneUIReplacement -update $gMainPane;\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Top View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Top View\")) -mbv $menusOkayInPanels  $panelName;\n"
		+ "\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"top\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 0\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n"
		+ "            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n"
		+ "            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Side View\")) `;\n"
		+ "\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Side View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"side\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 0\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n"
		+ "            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n"
		+ "            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n"
		+ "\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Front View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Front View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"side\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 0\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n"
		+ "            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n"
		+ "            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 0\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n"
		+ "            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Persp View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Persp View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"persp\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 1\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n"
		+ "            -activeComponentsXray 0\n            -displayTextures 1\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n"
		+ "            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 0\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n"
		+ "            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1054\n            -height 663\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"outlinerPanel\" (localizedPanelLabel(\"ToggledOutliner\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\toutlinerPanel -edit -l (localizedPanelLabel(\"ToggledOutliner\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        outlinerEditor -e \n            -showShapes 1\n            -showAssignedMaterials 0\n            -showTimeEditor 1\n            -showReferenceNodes 1\n            -showReferenceMembers 1\n            -showAttributes 0\n            -showConnected 0\n            -showAnimCurvesOnly 0\n            -showMuteInfo 0\n            -organizeByLayer 1\n"
		+ "            -organizeByClip 1\n            -showAnimLayerWeight 1\n            -autoExpandLayers 1\n            -autoExpand 0\n            -showDagOnly 1\n            -showAssets 1\n            -showContainedOnly 1\n            -showPublishedAsConnected 0\n            -showParentContainers 0\n            -showContainerContents 1\n            -ignoreDagHierarchy 0\n            -expandConnections 0\n            -showUpstreamCurves 1\n            -showUnitlessCurves 1\n            -showCompounds 1\n            -showLeafs 1\n            -showNumericAttrsOnly 0\n            -highlightActive 1\n            -autoSelectNewObjects 0\n            -doNotSelectNewObjects 0\n            -dropIsParent 1\n            -transmitFilters 0\n            -setFilter \"defaultSetFilter\" \n            -showSetMembers 1\n            -allowMultiSelection 1\n            -alwaysToggleSelect 0\n            -directSelect 0\n            -isSet 0\n            -isSetMember 0\n            -displayMode \"DAG\" \n            -expandObjects 0\n            -setsIgnoreFilters 1\n            -containersIgnoreFilters 0\n"
		+ "            -editAttrName 0\n            -showAttrValues 0\n            -highlightSecondary 0\n            -showUVAttrsOnly 0\n            -showTextureNodesOnly 0\n            -attrAlphaOrder \"default\" \n            -animLayerFilterOptions \"allAffecting\" \n            -sortOrder \"none\" \n            -longNames 0\n            -niceNames 1\n            -showNamespace 1\n            -showPinIcons 0\n            -mapMotionTrails 0\n            -ignoreHiddenAttribute 0\n            -ignoreOutlinerColor 0\n            -renderFilterVisible 0\n            -renderFilterIndex 0\n            -selectionOrder \"chronological\" \n            -expandAttribute 0\n            $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"outlinerPanel\" (localizedPanelLabel(\"Outliner\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\toutlinerPanel -edit -l (localizedPanelLabel(\"Outliner\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        outlinerEditor -e \n"
		+ "            -showShapes 0\n            -showAssignedMaterials 0\n            -showTimeEditor 1\n            -showReferenceNodes 0\n            -showReferenceMembers 0\n            -showAttributes 0\n            -showConnected 0\n            -showAnimCurvesOnly 0\n            -showMuteInfo 0\n            -organizeByLayer 1\n            -organizeByClip 1\n            -showAnimLayerWeight 1\n            -autoExpandLayers 1\n            -autoExpand 0\n            -showDagOnly 1\n            -showAssets 1\n            -showContainedOnly 1\n            -showPublishedAsConnected 0\n            -showParentContainers 0\n            -showContainerContents 1\n            -ignoreDagHierarchy 0\n            -expandConnections 0\n            -showUpstreamCurves 1\n            -showUnitlessCurves 1\n            -showCompounds 1\n            -showLeafs 1\n            -showNumericAttrsOnly 0\n            -highlightActive 1\n            -autoSelectNewObjects 0\n            -doNotSelectNewObjects 0\n            -dropIsParent 1\n            -transmitFilters 0\n"
		+ "            -setFilter \"defaultSetFilter\" \n            -showSetMembers 1\n            -allowMultiSelection 1\n            -alwaysToggleSelect 0\n            -directSelect 0\n            -displayMode \"DAG\" \n            -expandObjects 0\n            -setsIgnoreFilters 1\n            -containersIgnoreFilters 0\n            -editAttrName 0\n            -showAttrValues 0\n            -highlightSecondary 0\n            -showUVAttrsOnly 0\n            -showTextureNodesOnly 0\n            -attrAlphaOrder \"default\" \n            -animLayerFilterOptions \"allAffecting\" \n            -sortOrder \"none\" \n            -longNames 0\n            -niceNames 1\n            -showNamespace 1\n            -showPinIcons 0\n            -mapMotionTrails 0\n            -ignoreHiddenAttribute 0\n            -ignoreOutlinerColor 0\n            -renderFilterVisible 0\n            $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"graphEditor\" (localizedPanelLabel(\"Graph Editor\")) `;\n"
		+ "\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Graph Editor\")) -mbv $menusOkayInPanels  $panelName;\n\n\t\t\t$editorName = ($panelName+\"OutlineEd\");\n            outlinerEditor -e \n                -showShapes 1\n                -showAssignedMaterials 0\n                -showTimeEditor 1\n                -showReferenceNodes 0\n                -showReferenceMembers 0\n                -showAttributes 1\n                -showConnected 1\n                -showAnimCurvesOnly 1\n                -showMuteInfo 0\n                -organizeByLayer 1\n                -organizeByClip 1\n                -showAnimLayerWeight 1\n                -autoExpandLayers 1\n                -autoExpand 1\n                -showDagOnly 0\n                -showAssets 1\n                -showContainedOnly 0\n                -showPublishedAsConnected 0\n                -showParentContainers 0\n                -showContainerContents 0\n                -ignoreDagHierarchy 0\n                -expandConnections 1\n"
		+ "                -showUpstreamCurves 1\n                -showUnitlessCurves 1\n                -showCompounds 0\n                -showLeafs 1\n                -showNumericAttrsOnly 1\n                -highlightActive 0\n                -autoSelectNewObjects 1\n                -doNotSelectNewObjects 0\n                -dropIsParent 1\n                -transmitFilters 1\n                -setFilter \"0\" \n                -showSetMembers 0\n                -allowMultiSelection 1\n                -alwaysToggleSelect 0\n                -directSelect 0\n                -displayMode \"DAG\" \n                -expandObjects 0\n                -setsIgnoreFilters 1\n                -containersIgnoreFilters 0\n                -editAttrName 0\n                -showAttrValues 0\n                -highlightSecondary 0\n                -showUVAttrsOnly 0\n                -showTextureNodesOnly 0\n                -attrAlphaOrder \"default\" \n                -animLayerFilterOptions \"allAffecting\" \n                -sortOrder \"none\" \n                -longNames 0\n"
		+ "                -niceNames 1\n                -showNamespace 1\n                -showPinIcons 1\n                -mapMotionTrails 1\n                -ignoreHiddenAttribute 0\n                -ignoreOutlinerColor 0\n                -renderFilterVisible 0\n                $editorName;\n\n\t\t\t$editorName = ($panelName+\"GraphEd\");\n            animCurveEditor -e \n                -displayValues 0\n                -snapTime \"integer\" \n                -snapValue \"none\" \n                -showPlayRangeShades \"on\" \n                -lockPlayRangeShades \"off\" \n                -smoothness \"fine\" \n                -resultSamples 1\n                -resultScreenSamples 0\n                -resultUpdate \"delayed\" \n                -showUpstreamCurves 1\n                -stackedCurvesMin -1\n                -stackedCurvesMax 1\n                -stackedCurvesSpace 0.2\n                -preSelectionHighlight 0\n                -constrainDrag 0\n                -valueLinesToggle 0\n                -highlightAffectedCurves 0\n                $editorName;\n"
		+ "\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"dopeSheetPanel\" (localizedPanelLabel(\"Dope Sheet\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Dope Sheet\")) -mbv $menusOkayInPanels  $panelName;\n\n\t\t\t$editorName = ($panelName+\"OutlineEd\");\n            outlinerEditor -e \n                -showShapes 1\n                -showAssignedMaterials 0\n                -showTimeEditor 1\n                -showReferenceNodes 0\n                -showReferenceMembers 0\n                -showAttributes 1\n                -showConnected 1\n                -showAnimCurvesOnly 1\n                -showMuteInfo 0\n                -organizeByLayer 1\n                -organizeByClip 1\n                -showAnimLayerWeight 1\n                -autoExpandLayers 1\n                -autoExpand 0\n                -showDagOnly 0\n                -showAssets 1\n                -showContainedOnly 0\n                -showPublishedAsConnected 0\n"
		+ "                -showParentContainers 0\n                -showContainerContents 0\n                -ignoreDagHierarchy 0\n                -expandConnections 1\n                -showUpstreamCurves 1\n                -showUnitlessCurves 0\n                -showCompounds 1\n                -showLeafs 1\n                -showNumericAttrsOnly 1\n                -highlightActive 0\n                -autoSelectNewObjects 0\n                -doNotSelectNewObjects 1\n                -dropIsParent 1\n                -transmitFilters 0\n                -setFilter \"0\" \n                -showSetMembers 0\n                -allowMultiSelection 1\n                -alwaysToggleSelect 0\n                -directSelect 0\n                -displayMode \"DAG\" \n                -expandObjects 0\n                -setsIgnoreFilters 1\n                -containersIgnoreFilters 0\n                -editAttrName 0\n                -showAttrValues 0\n                -highlightSecondary 0\n                -showUVAttrsOnly 0\n                -showTextureNodesOnly 0\n                -attrAlphaOrder \"default\" \n"
		+ "                -animLayerFilterOptions \"allAffecting\" \n                -sortOrder \"none\" \n                -longNames 0\n                -niceNames 1\n                -showNamespace 1\n                -showPinIcons 0\n                -mapMotionTrails 1\n                -ignoreHiddenAttribute 0\n                -ignoreOutlinerColor 0\n                -renderFilterVisible 0\n                $editorName;\n\n\t\t\t$editorName = ($panelName+\"DopeSheetEd\");\n            dopeSheetEditor -e \n                -displayValues 0\n                -snapTime \"integer\" \n                -snapValue \"none\" \n                -outliner \"dopeSheetPanel1OutlineEd\" \n                -showSummary 1\n                -showScene 0\n                -hierarchyBelow 0\n                -showTicks 1\n                -selectionWindow 0 0 0 0 \n                $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"timeEditorPanel\" (localizedPanelLabel(\"Time Editor\")) `;\n\tif (\"\" != $panelName) {\n"
		+ "\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Time Editor\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"clipEditorPanel\" (localizedPanelLabel(\"Trax Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Trax Editor\")) -mbv $menusOkayInPanels  $panelName;\n\n\t\t\t$editorName = clipEditorNameFromPanel($panelName);\n            clipEditor -e \n                -displayValues 0\n                -snapTime \"none\" \n                -snapValue \"none\" \n                -initialized 0\n                -manageSequencer 0 \n                $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"sequenceEditorPanel\" (localizedPanelLabel(\"Camera Sequencer\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n"
		+ "\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Camera Sequencer\")) -mbv $menusOkayInPanels  $panelName;\n\n\t\t\t$editorName = sequenceEditorNameFromPanel($panelName);\n            clipEditor -e \n                -displayValues 0\n                -snapTime \"none\" \n                -snapValue \"none\" \n                -initialized 0\n                -manageSequencer 1 \n                $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"hyperGraphPanel\" (localizedPanelLabel(\"Hypergraph Hierarchy\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Hypergraph Hierarchy\")) -mbv $menusOkayInPanels  $panelName;\n\n\t\t\t$editorName = ($panelName+\"HyperGraphEd\");\n            hyperGraph -e \n                -graphLayoutStyle \"hierarchicalLayout\" \n                -orientation \"horiz\" \n                -mergeConnections 0\n                -zoom 1\n                -animateTransition 0\n                -showRelationships 1\n"
		+ "                -showShapes 0\n                -showDeformers 0\n                -showExpressions 0\n                -showConstraints 0\n                -showConnectionFromSelected 0\n                -showConnectionToSelected 0\n                -showConstraintLabels 0\n                -showUnderworld 0\n                -showInvisible 0\n                -transitionFrames 1\n                -opaqueContainers 0\n                -freeform 0\n                -imagePosition 0 0 \n                -imageScale 1\n                -imageEnabled 0\n                -graphType \"DAG\" \n                -heatMapDisplay 0\n                -updateSelection 1\n                -updateNodeAdded 1\n                -useDrawOverrideColor 0\n                -limitGraphTraversal -1\n                -range 0 0 \n                -iconSize \"smallIcons\" \n                -showCachedConnections 0\n                $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"hyperShadePanel\" (localizedPanelLabel(\"Hypershade\")) `;\n"
		+ "\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Hypershade\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"visorPanel\" (localizedPanelLabel(\"Visor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Visor\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"nodeEditorPanel\" (localizedPanelLabel(\"Node Editor\")) `;\n\tif ($nodeEditorPanelVisible || $nodeEditorWorkspaceControlOpen) {\n\t\tif (\"\" == $panelName) {\n\t\t\tif ($useSceneConfig) {\n\t\t\t\t$panelName = `scriptedPanel -unParent  -type \"nodeEditorPanel\" -l (localizedPanelLabel(\"Node Editor\")) -mbv $menusOkayInPanels `;\n\n\t\t\t$editorName = ($panelName+\"NodeEditorEd\");\n            nodeEditor -e \n                -allAttributes 0\n"
		+ "                -allNodes 0\n                -autoSizeNodes 1\n                -consistentNameSize 1\n                -createNodeCommand \"nodeEdCreateNodeCommand\" \n                -connectNodeOnCreation 0\n                -connectOnDrop 0\n                -copyConnectionsOnPaste 0\n                -connectionStyle \"bezier\" \n                -defaultPinnedState 0\n                -additiveGraphingMode 0\n                -settingsChangedCallback \"nodeEdSyncControls\" \n                -traversalDepthLimit -1\n                -keyPressCommand \"nodeEdKeyPressCommand\" \n                -nodeTitleMode \"name\" \n                -gridSnap 0\n                -gridVisibility 1\n                -crosshairOnEdgeDragging 0\n                -popupMenuScript \"nodeEdBuildPanelMenus\" \n                -showNamespace 1\n                -showShapes 1\n                -showSGShapes 0\n                -showTransforms 1\n                -useAssets 1\n                -syncedSelection 1\n                -extendToShapes 1\n                -editorMode \"default\" \n"
		+ "                -hasWatchpoint 0\n                $editorName;\n\t\t\t}\n\t\t} else {\n\t\t\t$label = `panel -q -label $panelName`;\n\t\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Node Editor\")) -mbv $menusOkayInPanels  $panelName;\n\n\t\t\t$editorName = ($panelName+\"NodeEditorEd\");\n            nodeEditor -e \n                -allAttributes 0\n                -allNodes 0\n                -autoSizeNodes 1\n                -consistentNameSize 1\n                -createNodeCommand \"nodeEdCreateNodeCommand\" \n                -connectNodeOnCreation 0\n                -connectOnDrop 0\n                -copyConnectionsOnPaste 0\n                -connectionStyle \"bezier\" \n                -defaultPinnedState 0\n                -additiveGraphingMode 0\n                -settingsChangedCallback \"nodeEdSyncControls\" \n                -traversalDepthLimit -1\n                -keyPressCommand \"nodeEdKeyPressCommand\" \n                -nodeTitleMode \"name\" \n                -gridSnap 0\n                -gridVisibility 1\n                -crosshairOnEdgeDragging 0\n"
		+ "                -popupMenuScript \"nodeEdBuildPanelMenus\" \n                -showNamespace 1\n                -showShapes 1\n                -showSGShapes 0\n                -showTransforms 1\n                -useAssets 1\n                -syncedSelection 1\n                -extendToShapes 1\n                -editorMode \"default\" \n                -hasWatchpoint 0\n                $editorName;\n\t\t\tif (!$useSceneConfig) {\n\t\t\t\tpanel -e -l $label $panelName;\n\t\t\t}\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"createNodePanel\" (localizedPanelLabel(\"Create Node\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Create Node\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"polyTexturePlacementPanel\" (localizedPanelLabel(\"UV Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"UV Editor\")) -mbv $menusOkayInPanels  $panelName;\n"
		+ "\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"renderWindowPanel\" (localizedPanelLabel(\"Render View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Render View\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"shapePanel\" (localizedPanelLabel(\"Shape Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tshapePanel -edit -l (localizedPanelLabel(\"Shape Editor\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"posePanel\" (localizedPanelLabel(\"Pose Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tposePanel -edit -l (localizedPanelLabel(\"Pose Editor\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n"
		+ "\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"dynRelEdPanel\" (localizedPanelLabel(\"Dynamic Relationships\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Dynamic Relationships\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"relationshipPanel\" (localizedPanelLabel(\"Relationship Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Relationship Editor\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"referenceEditorPanel\" (localizedPanelLabel(\"Reference Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Reference Editor\")) -mbv $menusOkayInPanels  $panelName;\n"
		+ "\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"componentEditorPanel\" (localizedPanelLabel(\"Component Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Component Editor\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"dynPaintScriptedPanelType\" (localizedPanelLabel(\"Paint Effects\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Paint Effects\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"scriptEditorPanel\" (localizedPanelLabel(\"Script Editor\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Script Editor\")) -mbv $menusOkayInPanels  $panelName;\n"
		+ "\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"profilerPanel\" (localizedPanelLabel(\"Profiler Tool\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Profiler Tool\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"contentBrowserPanel\" (localizedPanelLabel(\"Content Browser\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Content Browser\")) -mbv $menusOkayInPanels  $panelName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextScriptedPanel \"Stereo\" (localizedPanelLabel(\"Stereo\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tscriptedPanel -edit -l (localizedPanelLabel(\"Stereo\")) -mbv $menusOkayInPanels  $panelName;\n"
		+ "{ string $editorName = ($panelName+\"Editor\");\n            stereoCameraView -e \n                -camera \"persp\" \n                -useInteractiveMode 0\n                -displayLights \"default\" \n                -displayAppearance \"smoothShaded\" \n                -activeOnly 0\n                -ignorePanZoom 0\n                -wireframeOnShaded 0\n                -headsUpDisplay 1\n                -holdOuts 1\n                -selectionHiliteDisplay 1\n                -useDefaultMaterial 0\n                -bufferMode \"double\" \n                -twoSidedLighting 0\n                -backfaceCulling 0\n                -xray 0\n                -jointXray 0\n                -activeComponentsXray 0\n                -displayTextures 0\n                -smoothWireframe 0\n                -lineWidth 1\n                -textureAnisotropic 0\n                -textureHilight 1\n                -textureSampling 2\n                -textureDisplay \"modulate\" \n                -textureMaxSize 32768\n                -fogging 0\n                -fogSource \"fragment\" \n"
		+ "                -fogMode \"linear\" \n                -fogStart 0\n                -fogEnd 100\n                -fogDensity 0.1\n                -fogColor 0.5 0.5 0.5 1 \n                -depthOfFieldPreview 1\n                -maxConstantTransparency 1\n                -objectFilterShowInHUD 1\n                -isFiltered 0\n                -colorResolution 4 4 \n                -bumpResolution 4 4 \n                -textureCompression 0\n                -transparencyAlgorithm \"frontAndBackCull\" \n                -transpInShadows 0\n                -cullingOverride \"none\" \n                -lowQualityLighting 0\n                -maximumNumHardwareLights 0\n                -occlusionCulling 0\n                -shadingModel 0\n                -useBaseRenderer 0\n                -useReducedRenderer 0\n                -smallObjectCulling 0\n                -smallObjectThreshold -1 \n                -interactiveDisableShadows 0\n                -interactiveBackFaceCull 0\n                -sortTransparent 1\n                -controllers 1\n                -nurbsCurves 1\n"
		+ "                -nurbsSurfaces 1\n                -polymeshes 1\n                -subdivSurfaces 1\n                -planes 1\n                -lights 1\n                -cameras 1\n                -controlVertices 1\n                -hulls 1\n                -grid 1\n                -imagePlane 1\n                -joints 1\n                -ikHandles 1\n                -deformers 1\n                -dynamics 1\n                -particleInstancers 1\n                -fluids 1\n                -hairSystems 1\n                -follicles 1\n                -nCloths 1\n                -nParticles 1\n                -nRigids 1\n                -dynamicConstraints 1\n                -locators 1\n                -manipulators 1\n                -pluginShapes 1\n                -dimensions 1\n                -handles 1\n                -pivots 1\n                -textures 1\n                -strokes 1\n                -motionTrails 1\n                -clipGhosts 1\n                -greasePencils 1\n                -shadows 0\n                -captureSequenceNumber -1\n"
		+ "                -width 0\n                -height 0\n                -sceneRenderFilter 0\n                -displayMode \"centerEye\" \n                -viewColor 0 0 0 1 \n                -useCustomBackground 1\n                $editorName;\n            stereoCameraView -e -viewSelected 0 $editorName; };\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\tif ($useSceneConfig) {\n        string $configName = `getPanel -cwl (localizedPanelLabel(\"Current Layout\"))`;\n        if (\"\" != $configName) {\n\t\t\tpanelConfiguration -edit -label (localizedPanelLabel(\"Current Layout\")) \n\t\t\t\t-userCreated false\n\t\t\t\t-defaultImage \"vacantCell.xP:/\"\n\t\t\t\t-image \"\"\n\t\t\t\t-sc false\n\t\t\t\t-configString \"global string $gMainPane; paneLayout -e -cn \\\"single\\\" -ps 1 100 100 $gMainPane;\"\n\t\t\t\t-removeAllPanels\n\t\t\t\t-ap false\n\t\t\t\t\t(localizedPanelLabel(\"Persp View\")) \n\t\t\t\t\t\"modelPanel\"\n"
		+ "\t\t\t\t\t\"$panelName = `modelPanel -unParent -l (localizedPanelLabel(\\\"Persp View\\\")) -mbv $menusOkayInPanels `;\\n$editorName = $panelName;\\nmodelEditor -e \\n    -cam `findStartUpCamera persp` \\n    -useInteractiveMode 0\\n    -displayLights \\\"default\\\" \\n    -displayAppearance \\\"smoothShaded\\\" \\n    -activeOnly 0\\n    -ignorePanZoom 0\\n    -wireframeOnShaded 1\\n    -headsUpDisplay 1\\n    -holdOuts 1\\n    -selectionHiliteDisplay 1\\n    -useDefaultMaterial 0\\n    -bufferMode \\\"double\\\" \\n    -twoSidedLighting 0\\n    -backfaceCulling 0\\n    -xray 0\\n    -jointXray 0\\n    -activeComponentsXray 0\\n    -displayTextures 1\\n    -smoothWireframe 0\\n    -lineWidth 1\\n    -textureAnisotropic 0\\n    -textureHilight 1\\n    -textureSampling 2\\n    -textureDisplay \\\"modulate\\\" \\n    -textureMaxSize 32768\\n    -fogging 0\\n    -fogSource \\\"fragment\\\" \\n    -fogMode \\\"linear\\\" \\n    -fogStart 0\\n    -fogEnd 100\\n    -fogDensity 0.1\\n    -fogColor 0.5 0.5 0.5 1 \\n    -depthOfFieldPreview 1\\n    -maxConstantTransparency 1\\n    -rendererName \\\"vp2Renderer\\\" \\n    -objectFilterShowInHUD 1\\n    -isFiltered 0\\n    -colorResolution 256 256 \\n    -bumpResolution 512 512 \\n    -textureCompression 0\\n    -transparencyAlgorithm \\\"frontAndBackCull\\\" \\n    -transpInShadows 0\\n    -cullingOverride \\\"none\\\" \\n    -lowQualityLighting 0\\n    -maximumNumHardwareLights 1\\n    -occlusionCulling 0\\n    -shadingModel 0\\n    -useBaseRenderer 0\\n    -useReducedRenderer 0\\n    -smallObjectCulling 0\\n    -smallObjectThreshold -1 \\n    -interactiveDisableShadows 0\\n    -interactiveBackFaceCull 0\\n    -sortTransparent 1\\n    -controllers 1\\n    -nurbsCurves 1\\n    -nurbsSurfaces 1\\n    -polymeshes 1\\n    -subdivSurfaces 1\\n    -planes 1\\n    -lights 1\\n    -cameras 1\\n    -controlVertices 1\\n    -hulls 1\\n    -grid 0\\n    -imagePlane 1\\n    -joints 1\\n    -ikHandles 1\\n    -deformers 1\\n    -dynamics 1\\n    -particleInstancers 1\\n    -fluids 1\\n    -hairSystems 1\\n    -follicles 1\\n    -nCloths 1\\n    -nParticles 1\\n    -nRigids 1\\n    -dynamicConstraints 1\\n    -locators 1\\n    -manipulators 1\\n    -pluginShapes 1\\n    -dimensions 1\\n    -handles 1\\n    -pivots 1\\n    -textures 1\\n    -strokes 1\\n    -motionTrails 1\\n    -clipGhosts 1\\n    -greasePencils 1\\n    -shadows 0\\n    -captureSequenceNumber -1\\n    -width 1054\\n    -height 663\\n    -sceneRenderFilter 0\\n    $editorName;\\nmodelEditor -e -viewSelected 0 $editorName\"\n"
		+ "\t\t\t\t\t\"modelPanel -edit -l (localizedPanelLabel(\\\"Persp View\\\")) -mbv $menusOkayInPanels  $panelName;\\n$editorName = $panelName;\\nmodelEditor -e \\n    -cam `findStartUpCamera persp` \\n    -useInteractiveMode 0\\n    -displayLights \\\"default\\\" \\n    -displayAppearance \\\"smoothShaded\\\" \\n    -activeOnly 0\\n    -ignorePanZoom 0\\n    -wireframeOnShaded 1\\n    -headsUpDisplay 1\\n    -holdOuts 1\\n    -selectionHiliteDisplay 1\\n    -useDefaultMaterial 0\\n    -bufferMode \\\"double\\\" \\n    -twoSidedLighting 0\\n    -backfaceCulling 0\\n    -xray 0\\n    -jointXray 0\\n    -activeComponentsXray 0\\n    -displayTextures 1\\n    -smoothWireframe 0\\n    -lineWidth 1\\n    -textureAnisotropic 0\\n    -textureHilight 1\\n    -textureSampling 2\\n    -textureDisplay \\\"modulate\\\" \\n    -textureMaxSize 32768\\n    -fogging 0\\n    -fogSource \\\"fragment\\\" \\n    -fogMode \\\"linear\\\" \\n    -fogStart 0\\n    -fogEnd 100\\n    -fogDensity 0.1\\n    -fogColor 0.5 0.5 0.5 1 \\n    -depthOfFieldPreview 1\\n    -maxConstantTransparency 1\\n    -rendererName \\\"vp2Renderer\\\" \\n    -objectFilterShowInHUD 1\\n    -isFiltered 0\\n    -colorResolution 256 256 \\n    -bumpResolution 512 512 \\n    -textureCompression 0\\n    -transparencyAlgorithm \\\"frontAndBackCull\\\" \\n    -transpInShadows 0\\n    -cullingOverride \\\"none\\\" \\n    -lowQualityLighting 0\\n    -maximumNumHardwareLights 1\\n    -occlusionCulling 0\\n    -shadingModel 0\\n    -useBaseRenderer 0\\n    -useReducedRenderer 0\\n    -smallObjectCulling 0\\n    -smallObjectThreshold -1 \\n    -interactiveDisableShadows 0\\n    -interactiveBackFaceCull 0\\n    -sortTransparent 1\\n    -controllers 1\\n    -nurbsCurves 1\\n    -nurbsSurfaces 1\\n    -polymeshes 1\\n    -subdivSurfaces 1\\n    -planes 1\\n    -lights 1\\n    -cameras 1\\n    -controlVertices 1\\n    -hulls 1\\n    -grid 0\\n    -imagePlane 1\\n    -joints 1\\n    -ikHandles 1\\n    -deformers 1\\n    -dynamics 1\\n    -particleInstancers 1\\n    -fluids 1\\n    -hairSystems 1\\n    -follicles 1\\n    -nCloths 1\\n    -nParticles 1\\n    -nRigids 1\\n    -dynamicConstraints 1\\n    -locators 1\\n    -manipulators 1\\n    -pluginShapes 1\\n    -dimensions 1\\n    -handles 1\\n    -pivots 1\\n    -textures 1\\n    -strokes 1\\n    -motionTrails 1\\n    -clipGhosts 1\\n    -greasePencils 1\\n    -shadows 0\\n    -captureSequenceNumber -1\\n    -width 1054\\n    -height 663\\n    -sceneRenderFilter 0\\n    $editorName;\\nmodelEditor -e -viewSelected 0 $editorName\"\n"
		+ "\t\t\t\t$configName;\n\n            setNamedPanelLayout (localizedPanelLabel(\"Current Layout\"));\n        }\n\n        panelHistory -e -clear mainPanelHistory;\n        sceneUIReplacement -clear;\n\t}\n\n\ngrid -spacing 1 -size 1 -divisions 9 -displayAxes yes -displayGridLines yes -displayDivisionLines yes -displayPerspectiveLabels no -displayOrthographicLabels no -displayAxesBold yes -perspectiveLabelPosition axis -orthographicLabelPosition edge;\nviewManip -drawCompass 0 -compassAngle 0 -frontParameters \"\" -homeParameters \"\" -selectionLockParameters \"\";\n}\n");
	setAttr ".st" 3;
createNode script -n "sceneConfigurationScriptNode";
	rename -uid "629A043F-4633-5101-67B9-E4AD0C71C780";
	setAttr ".b" -type "string" "playbackOptions -min 1 -max 120 -ast 1 -aet 200 ";
	setAttr ".st" 6;
createNode polyExtrudeFace -n "polyExtrudeFace14";
	rename -uid "DDC3E3A1-422E-8397-36A7-519AAE61841F";
	setAttr ".ics" -type "componentList" 1 "f[1]";
	setAttr ".ix" -type "matrix" 0.15422656515727901 0 0 0 0 0.54325350605690292 0 0
		 0 0 0.54325350605690292 0 0 0.073940792833933899 1.9964014065162192 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.12004818 2.1405859 ;
	setAttr ".rs" 35092;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.077113282578639503 0.12004818077874194 2.0131436766314286 ;
	setAttr ".cbx" -type "double3" 0.077113282578639503 0.12004818077874194 2.2680281595446705 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak14";
	rename -uid "6680D0CF-4E06-713B-1649-119DBB0C8B8E";
	setAttr ".uopa" yes;
	setAttr -s 6 ".tk[2:7]" -type "float3"  0 -0.41512731 0 0 -0.41512731
		 0 0 -0.41512731 0.53081852 0 -0.41512731 0.53081852 0 0 0.34026819 0 0 0.34026819;
createNode polyTweak -n "polyTweak15";
	rename -uid "7E797E03-460F-30EB-B91D-24AAD988C33E";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[8:11]" -type "float3"  0 0.40151659 0 0 0.40151659
		 0 0 0.40151659 -0.5784561 0 0.40151659 -0.5784561;
createNode deleteComponent -n "deleteComponent6";
	rename -uid "246FC95C-47BF-C1FF-2386-71A747034413";
	setAttr ".dc" -type "componentList" 1 "e[6:7]";
createNode deleteComponent -n "deleteComponent7";
	rename -uid "EABBC141-42E7-554B-E640-AFB749053F9D";
	setAttr ".dc" -type "componentList" 1 "e[1]";
createNode deleteComponent -n "deleteComponent8";
	rename -uid "88FEA9A2-4172-48A7-204D-BDB6513073BC";
	setAttr ".dc" -type "componentList" 1 "vtx[2:3]";
createNode polyCube -n "polyCube13";
	rename -uid "1476323F-486E-F1B4-915A-07B411B144B4";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube14";
	rename -uid "F2DDAD18-4468-ED18-AB67-0B9719524DBB";
	setAttr ".cuv" 4;
createNode polyExtrudeFace -n "polyExtrudeFace15";
	rename -uid "5BBEEB65-49C8-D870-4798-79989B85DEE0";
	setAttr ".ics" -type "componentList" 1 "f[1]";
	setAttr ".ix" -type "matrix" 0.067692757377719751 0 0 0 0 0.067692757377719751 0 0
		 0 0 0.067692757377719751 0 0 1.5278921827203977 -4.2125055478497684 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 1.9755268 -4.6274843 ;
	setAttr ".rs" 59083;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.033846378688859875 1.9370888056114919 -4.6275355917321956 ;
	setAttr ".cbx" -type "double3" 0.033846378688859875 2.0139647740649762 -4.6274328172363601 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak16";
	rename -uid "0B56025E-4B8B-75D7-20D8-B9B8672B147B";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[2:5]" -type "float3"  0 6.68057013 -6.63108492 0
		 6.68057013 -6.63108492 0 5.54490995 -5.62956715 0 5.54491043 -5.62956667;
createNode polyExtrudeFace -n "polyExtrudeFace16";
	rename -uid "059DAF29-4554-679F-F2AB-5894C8F1815D";
	setAttr ".ics" -type "componentList" 1 "f[1]";
	setAttr ".ix" -type "matrix" 0.067692757377719751 0 0 0 0 0.067692757377719751 0 0
		 0 0 0.067692757377719751 0 0 1.5278921827203977 -4.2125055478497684 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 1.9642068 -5.03304 ;
	setAttr ".rs" 39047;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.033846376671458497 1.937088837889914 -5.0739020442041811 ;
	setAttr ".cbx" -type "double3" 0.033846376671458497 1.9913248502249561 -4.9921779213142905 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak17";
	rename -uid "D4C825AF-4AAA-7867-C73A-CDBC0306252B";
	setAttr ".uopa" yes;
	setAttr -s 6 ".tk";
	setAttr ".tk[2]" -type "float3" 0 -0.33445379 0.65032685 ;
	setAttr ".tk[3]" -type "float3" 0 -0.33445379 0.65032685 ;
	setAttr ".tk[8]" -type "float3" 0 -0.33445379 -6.5940104 ;
	setAttr ".tk[9]" -type "float3" 0 -0.33445379 -6.5940104 ;
	setAttr ".tk[10]" -type "float3" 0 -7.4505833e-09 -5.388248 ;
	setAttr ".tk[11]" -type "float3" 0 -7.4505833e-09 -5.388248 ;
createNode polyUnite -n "polyUnite1";
	rename -uid "25D52848-43BE-FB87-7774-4AB178092032";
	setAttr -s 3 ".ip";
	setAttr -s 3 ".im";
createNode groupId -n "groupId1";
	rename -uid "4BBC125A-4B75-06A0-AD4C-7B8F1BF5B3F6";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts1";
	rename -uid "99B00ED4-4F88-433B-E089-508F741D6EF8";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:13]";
createNode groupId -n "groupId2";
	rename -uid "A89B6F9C-4FB2-9BB5-3963-FE9A67581D01";
	setAttr ".ihi" 0;
createNode groupId -n "groupId3";
	rename -uid "BBBF14E1-4173-7E1B-7248-DDA1170AA2E0";
	setAttr ".ihi" 0;
createNode groupId -n "groupId4";
	rename -uid "0E6542A6-4E9B-A22E-C1E9-A884A6D5D0D6";
	setAttr ".ihi" 0;
createNode groupId -n "groupId5";
	rename -uid "BA822755-4156-536F-83F6-208C34815D19";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts2";
	rename -uid "9AD64240-4D39-DC82-220A-2C981163A05B";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId6";
	rename -uid "6C426DDA-4751-2F9A-16EB-A4890C569DA0";
	setAttr ".ihi" 0;
createNode groupId -n "groupId7";
	rename -uid "DCB83D09-44E6-4C0D-9BC3-26B72A4BC569";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts3";
	rename -uid "67E92CCA-4783-94A3-E5C8-BBA50C013751";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:25]";
createNode groupId -n "groupId8";
	rename -uid "A1B9D328-4AFC-490D-4BD2-508A38165AE5";
	setAttr ".ihi" 0;
createNode lambert -n "pixelMeasure";
	rename -uid "1509C85C-4AE6-644E-AA49-AF8AB62BCF8F";
createNode shadingEngine -n "lambert2SG";
	rename -uid "31443C7E-42E2-00C9-4998-8DB9D7821C21";
	setAttr ".ihi" 0;
	setAttr -s 2 ".dsm";
	setAttr ".ro" yes;
	setAttr -s 2 ".gn";
createNode materialInfo -n "materialInfo1";
	rename -uid "FEB8A6B7-4261-6E42-8287-24837E74A01F";
createNode lambert -n "layout";
	rename -uid "CC3C841B-4B9B-BEEE-0A03-E4AC6C49BD25";
createNode shadingEngine -n "lambert3SG";
	rename -uid "8CABFB85-4771-49E6-FD34-38B5039EF9B5";
	setAttr ".ihi" 0;
	setAttr -s 3 ".dsm";
	setAttr ".ro" yes;
createNode materialInfo -n "materialInfo2";
	rename -uid "C501BBFB-457E-832D-0F13-86ACB5D4D303";
createNode file -n "file1";
	rename -uid "06397A40-4976-3426-9591-FE8A5381354A";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Documents/UVminecraftTexture.png";
	setAttr ".ft" 0;
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "place2dTexture1";
	rename -uid "F4048DC4-4214-B707-EB51-128F8B38B757";
createNode polyUnite -n "polyUnite2";
	rename -uid "A35AB173-4A60-FF3C-F9A5-2280B9743077";
	setAttr -s 12 ".ip";
	setAttr -s 12 ".im";
createNode groupId -n "groupId9";
	rename -uid "E0EA70EE-4395-34A3-2F3C-5AB68971F0B4";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts4";
	rename -uid "9FCD4A07-495B-3012-50CA-54B3B0FA0DA6";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:11]";
createNode groupId -n "groupId10";
	rename -uid "DE354C09-4A85-A410-408B-BA9BFBEBE5AB";
	setAttr ".ihi" 0;
createNode groupId -n "groupId11";
	rename -uid "9DCA3776-445A-B326-B827-309B7C59F1A1";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts5";
	rename -uid "792779F0-40B7-2216-53F3-03A47A365F39";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:13]";
createNode groupId -n "groupId12";
	rename -uid "D303404C-4246-7051-75F1-53BFB9DA0F63";
	setAttr ".ihi" 0;
createNode groupId -n "groupId13";
	rename -uid "688BB283-4BA3-E4ED-2FEB-C798C3001765";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts6";
	rename -uid "FBDE123F-4B2A-E5EA-6E93-5C9AC0D34949";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId14";
	rename -uid "49A4D365-41A9-6524-14FC-35BDB3CD2ECF";
	setAttr ".ihi" 0;
createNode groupId -n "groupId15";
	rename -uid "BF159C64-46A7-940C-8583-AA9E5696548C";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts7";
	rename -uid "4BE688C8-4DC1-B404-B44B-D487C15ECCAB";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId16";
	rename -uid "58ACCF7C-41FD-0A0C-A5A7-8EAB4ECA917D";
	setAttr ".ihi" 0;
createNode groupId -n "groupId17";
	rename -uid "BA07F841-43D0-28D1-BBD9-E1AE75048289";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts8";
	rename -uid "E59324FD-47E2-FA55-568B-818282E5CB1B";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId18";
	rename -uid "6DA5254E-4396-98A6-1D73-D78213CB386E";
	setAttr ".ihi" 0;
createNode groupId -n "groupId19";
	rename -uid "7A22CBAE-4B75-9068-3E09-30926DADEC2F";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts9";
	rename -uid "86E87B80-4030-BD52-FACC-E6928652C6D4";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId20";
	rename -uid "55210884-4062-8D6F-813F-F683AB0420CA";
	setAttr ".ihi" 0;
createNode groupId -n "groupId21";
	rename -uid "18D0A673-44E4-59C9-E8E2-E99B948A88A2";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts10";
	rename -uid "6271D664-4024-3C10-AEDB-A3965FF69BB4";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId22";
	rename -uid "3E60DF62-4164-771B-497B-6C9245E168F5";
	setAttr ".ihi" 0;
createNode groupId -n "groupId23";
	rename -uid "993B392C-46AB-01B9-5899-B9ADAE55C74C";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts11";
	rename -uid "C41DBB7A-4ECD-7BBD-7660-36BB099373D3";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId24";
	rename -uid "B9D8081B-46B4-3C96-3476-28B37BF06712";
	setAttr ".ihi" 0;
createNode groupId -n "groupId25";
	rename -uid "21FCDEF1-4664-E673-252D-2B8A9187918F";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts12";
	rename -uid "8068F20D-4D01-8809-F2FF-0388C0458443";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:17]";
createNode groupId -n "groupId26";
	rename -uid "AA372192-4470-DFAD-FF25-66B6268907A1";
	setAttr ".ihi" 0;
createNode groupId -n "groupId27";
	rename -uid "DD7E322D-4C34-82B0-7588-7EA320661FD9";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts13";
	rename -uid "2AB5B9CD-4C00-47D2-0827-3E9043388038";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:6]";
createNode groupId -n "groupId28";
	rename -uid "597C7AE5-43CB-38B8-94B9-1CAC53C56D4D";
	setAttr ".ihi" 0;
createNode polyMapDel -n "polyMapDel1";
	rename -uid "457D03B2-4D4F-ECC0-9CDE-F19A0F0BFD71";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[0:138]";
createNode polyAutoProj -n "polyAutoProj1";
	rename -uid "1FF99754-4880-83D6-7153-B484D333D163";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[0:5]" "f[7:11]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 2.468988299369812 2.468988299369812 2.468988299369812 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV1";
	rename -uid "AB58C146-499D-79D5-44D3-FFBF14A170DD";
	setAttr ".uopa" yes;
	setAttr -s 46 ".uvtk[0:45]" -type "float2" -0.52543807 -0.39227453 -0.52562761
		 -0.44004393 -0.10411064 -0.4400439 -0.30342242 0.010926887 -0.47301012 -0.094397925
		 -0.35119411 -0.3205269 -0.37425572 -0.39227453 -0.43191004 -0.39227453 -0.45753413
		 -0.35896313 -0.51134491 -0.35896313 -0.41449261 -0.84665918 -0.42858583 -0.81334782
		 -0.48239651 -0.81334782 -0.50802064 -0.84665918 -0.56567502 -0.84665918 -0.58873659
		 -0.77491152 -0.46692052 -0.54878259 -0.63650829 -0.44345775 -0.83582002 -0.89442861
		 -0.41430309 -0.89442861 -0.17660175 -0.44345775 -0.29020488 -0.44345775 -0.29020488
		 -0.50111222 -0.17660175 -0.50111222 -0.29020488 -0.52673626 -0.17660175 -0.52673626
		 -0.29020488 -0.58054698 -0.17660175 -0.58054698 -0.29545242 -0.54878259 -0.40905553
		 -0.54878259 -0.40905553 -0.77491152 -0.29545242 -0.77491152 -0.29545242 -0.44345775
		 -0.40905553 -0.44345775 -0.40905553 -0.84665918 -0.29545242 -0.84665918 -0.12358484
		 -0.55706096 -0.12358484 -0.44345787 -0.17135419 -0.44345787 -0.17135419 -0.55706096
		 -0.090273418 -0.55706096 -0.090273418 -0.44345787 0.016076159 0.010926858 -0.097526968
		 0.010926858 -0.097526968 -0.44004393 0.016076159 -0.44004393;
createNode polyAutoProj -n "polyAutoProj2";
	rename -uid "FBEEA3C6-4822-461B-352C-FC9A6DD70B82";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[63]" "f[65:79]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.5601805448532104 1.5601805448532104 1.5601805448532104 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV2";
	rename -uid "3B331510-40CD-B950-AFE9-DCA7AAAE9B6B";
	setAttr ".uopa" yes;
	setAttr -s 92 ".uvtk[0:91]" -type "float2" 0.028629981 0.019287312 0.028643355
		 0.022650504 -0.0010334328 0.022650482 0.012999035 -0.0090999641 0.024938807 -0.0016845912
		 0.016362391 0.014235931 0.017986044 0.019287312 0.02204518 0.019287312 0.023849234
		 0.016942032 0.027637757 0.016942032 0.020818908 0.051278155 0.021811128 0.048932862
		 0.025599651 0.048932862 0.027403712 0.051278155 0.031462826 0.051278155 0.033086494
		 0.046226766 0.024510086 0.030306209 0.036449835 0.02289086 0.050482348 0.054641288
		 0.02080556 0.054641288 0.0040702671 0.02289086 0.012068454 0.02289086 0.012068454
		 0.026949953 0.0040702671 0.026949953 0.012068454 0.028754074 0.0040702671 0.028754074
		 0.012068454 0.03254256 0.0040702671 0.03254256 0.012437925 0.030306209 0.020436112
		 0.030306209 0.020436112 0.046226766 0.012437925 0.046226766 0.012437925 0.02289086
		 0.020436112 0.02289086 0.020436112 0.051278155 0.012437925 0.051278155 0.00033762306
		 0.030889053 0.00033762306 0.02289086 0.0037008189 0.02289086 0.0037008189 0.030889053
		 -0.0020076372 0.030889053 -0.0020076372 0.02289086 -0.009495154 -0.0090999641 -0.0014969632
		 -0.0090999641 -0.0014969632 0.022650504 -0.009495154 0.022650504 0.14188547 -0.5276683
		 0.2197452 -0.42966157 0.12576571 -0.39073166 -0.11049832 -0.41893324 0.20363629 -0.21084899
		 0.15395628 -0.25783321 0.081477076 -0.033650972 0.074754134 -0.094059259 -0.098405823
		 0.038839143 -0.098416671 -0.049759738 -0.11667164 -0.059167638 -0.19453137 0.038839143
		 -0.44691518 -0.069896013 -0.21065108 -0.098097548 -0.18246056 -0.23099601 -0.13278057
		 -0.27798024 -0.26166269 -0.39476997 -0.25493976 -0.45517826 -0.4348335 -0.43906948
		 -0.43482265 -0.5276683 -0.53840804 -0.45874631 -0.67060435 -0.45874631 -0.67060435
		 -0.59164476 -0.53840804 -0.59164476 -0.67060435 -0.75541872 -0.53840804 -0.75541872
		 -0.58511055 -0.35717145 -0.45291433 -0.35717145 -0.45291433 -0.13835891 -0.58511055
		 -0.13835891 -0.58511055 -0.45517826 -0.45291433 -0.45517826 -0.45291433 0.038839143
		 -0.58511055 0.038839143 -0.40072736 -0.53123629 -0.5329237 -0.53123629 -0.5329237
		 -0.76750034 -0.40072736 -0.76750034 -0.1253659 -0.53123629 -0.25756219 -0.53123629
		 -0.25756219 -0.70440716 -0.1253659 -0.70440716 -0.3952429 -0.71111906 -0.26304659
		 -0.71111906 -0.26304659 -0.53123629 -0.3952429 -0.53123629;
createNode polyAutoProj -n "polyAutoProj3";
	rename -uid "2AE49791-4C35-C306-1073-3F9C31591B89";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[81:83]" "f[85:86]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.56913280487060547 0.56913280487060547 0.56913280487060547 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV3";
	rename -uid "C921F5B8-4A4A-F081-DAAC-DB99A9E5C6B6";
	setAttr ".uopa" yes;
	setAttr -s 66 ".uvtk[46:111]" -type "float2" 0.09108264 0 0.09108264 0
		 0.09108267 0 0.09108267 0 0.09108264 0 0.09108264 0 0.09108267 0 0.09108264 0 0.09108267
		 0 0.09108267 0 0.09108267 0 0.09108267 0 0.09108264 0 0.09108267 0 0.09108267 0 0.09108267
		 0 0.09108264 0 0.09108264 0 0.09108264 0 0.09108264 0 0.09108267 0 0.09108267 0 0.09108267
		 0 0.09108267 0 0.09108267 0 0.09108267 0 0.09108267 0 0.09108264 0 0.09108264 0 0.09108267
		 0 0.09108267 0 0.09108264 0 0.09108264 0 0.09108267 0 0.09108264 0 0.09108267 0 0.09108267
		 0 0.09108264 0 0.09108267 0 0.09108264 0 0.09108264 0 0.09108267 0 0.09108264 0 0.09108264
		 0 0.09108264 0 0.09108264 0 -0.062838718 -0.14402471 0.19728227 -0.22877161 0.19728227
		 0.064642847 -0.24141249 0.064642847 -0.24141249 -0.40129203 -0.49689239 -0.66205668
		 -0.67546618 -0.40478951 -0.67546618 -0.87072432 -0.23677137 -0.87072432 -0.23677137
		 -0.57730979 0.026503613 -0.40478951 -0.099757873 -0.40478951 -0.099757873 -0.69820392
		 0.026503613 -0.69820392 -0.10513385 -0.58336318 -0.23139536 -0.58336318 -0.23139536
		 -0.84348422 -0.10513385 -0.84348422 -0.10513385 -0.40478951 -0.23139536 -0.40478951;
createNode polyAutoProj -n "polyAutoProj4";
	rename -uid "84EDF9B2-4D08-1DAF-2830-CAA62FDE7C40";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[13:21]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 4.9197559356689453 4.9197559356689453 4.9197559356689453 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweak -n "polyTweak18";
	rename -uid "AE831F55-4158-EC91-EEC4-0ABCE69F44C2";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk[60:67]" -type "float3"  -0.045594942 -0.064147793
		 0 0.045594942 -0.064147793 0 -0.045594942 -0.064147793 0 0.045594942 -0.064147793
		 0 -0.045594942 0 0 0.045594942 0 0 -0.045594942 0 0 0.045594942 0 0;
createNode polyTweakUV -n "polyTweakUV4";
	rename -uid "6C5ECE2D-4C43-D9D2-91C1-CBB79DF8095E";
	setAttr ".uopa" yes;
	setAttr -s 28 ".uvtk[112:139]" -type "float2" -0.31891263 -0.34404075 -0.31891263
		 0.34364039 -0.48840818 0.34364039 -0.48840818 -0.34404075 -0.49332392 -0.34404075
		 -0.49332392 0.34364039 -0.6628195 0.34364039 -0.6628195 -0.34404075 0.030064233 0.34364039
		 -0.13943131 0.34364039 -0.13943131 -0.34404075 0.030064233 -0.34404075 -0.1445014
		 0.34364039 -0.31399691 0.34364039 -0.31399691 -0.34404075 -0.1445014 -0.34404075
		 -0.58922988 -0.51669747 -0.58922988 -0.34720176 -0.7587254 -0.34720176 -0.7587254
		 -0.51669747 -0.41487557 -0.34720176 -0.58437109 -0.34720176 -0.56617385 -0.365399
		 -0.43307287 -0.365399 -0.58437109 -0.51669735 -0.56617385 -0.49849999 -0.41487557
		 -0.51669735 -0.43307287 -0.49849999;
createNode polyAutoProj -n "polyAutoProj5";
	rename -uid "90BF9128-489C-D4B3-83C7-92A7D2F61789";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[26:31]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 5.4790546894073486 5.4790546894073486 5.4790546894073486 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV5";
	rename -uid "C464248E-4F47-93CC-F4DB-E8A47610CDE8";
	setAttr ".uopa" yes;
	setAttr -s 52 ".uvtk[112:163]" -type "float2" 0.2299837 0 0.2299837 0 0.22998369
		 0 0.22998369 0 0.22998369 0 0.22998369 0 0.22998369 0 0.22998369 0 0.22998367 0 0.2299837
		 0 0.2299837 0 0.22998367 0 0.22998367 0 0.2299837 0 0.2299837 0 0.22998367 0 0.22998369
		 0 0.22998369 0 0.2299837 0 0.2299837 0 0.2299837 0 0.22998369 0 0.22998369 0 0.22998369
		 0 0.22998369 0 0.22998369 0 0.2299837 0 0.22998369 0 -0.19465765 -0.35274857 -0.19465765
		 0.5075891 -0.26453292 0.5075891 -0.26453292 -0.35274857 -0.26955912 -0.35274857 -0.26955912
		 0.5075891 -0.33943439 0.5075891 -0.33943439 -0.35274857 0.27519292 0.5075891 0.045956552
		 0.5075891 0.045956552 -0.35274857 0.27519292 -0.35274857 0.040969819 0.5075891 -0.18826649
		 0.5075891 -0.18826649 -0.35274857 0.040969819 -0.35274857 -0.34446058 0.2783528 -0.34446058
		 0.5075891 -0.41433585 0.5075891 -0.41433585 0.2783528 -0.41936201 0.27835256 -0.41936201
		 0.5075891 -0.48923713 0.5075891 -0.48923713 0.27835256;
createNode polyAutoProj -n "polyAutoProj6";
	rename -uid "E28A3078-46D1-DDBE-9B57-3A83B522210E";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[12]" "f[23:25]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.95222103595733643 0.95222103595733643 0.95222103595733643 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV6";
	rename -uid "E47971A1-4692-6C6B-8480-9B8F6A40E69D";
	setAttr ".uopa" yes;
	setAttr -s 16 ".uvtk[164:179]" -type "float2" -0.65226257 0.34065369 -0.65226257
		 -0.10535116 -0.54303265 -0.10535116 -0.42230469 0.34065369 -0.57409668 -0.55469793
		 -0.57409668 -0.108693 -0.8040545 -0.108693 -0.68332648 -0.55469793 -0.76813745 0.34065363
		 -0.76813745 -0.10535125 -0.65890765 -0.10535125 -0.65890765 0.34065363 0.029851992
		 -0.10535122 0.029851992 0.34065369 -0.41615283 0.34065369 -0.41615283 -0.10535122;
createNode polyAutoProj -n "polyAutoProj7";
	rename -uid "DA6AA58D-4639-ED0C-FD44-A8B8D43D5592";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "f[38]" "f[40]" "f[42:43]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.60915875434875488 0.60915875434875488 0.60915875434875488 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweak -n "polyTweak19";
	rename -uid "4380EB06-4040-E6F1-D8B2-B6BCD763C33F";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk";
	setAttr ".tk[28]" -type "float3" 0 0.10605633 0 ;
	setAttr ".tk[29]" -type "float3" 0 0.10605633 0 ;
	setAttr ".tk[32]" -type "float3" 0 0.10605633 0 ;
	setAttr ".tk[33]" -type "float3" 0 0.10605633 0 ;
createNode polyTweakUV -n "polyTweakUV7";
	rename -uid "EC528989-4B5B-7F0C-AC41-068314025433";
	setAttr ".uopa" yes;
	setAttr -s 16 ".uvtk[180:195]" -type "float2" 0.062993094 -0.300053 -0.23215273
		 -0.300053 -0.33831629 -0.78098512 0.062993094 -0.78098512 0.36354116 -0.300053 0.068395361
		 -0.300053 0.068395361 -0.78098512 0.46970481 -0.78098512 0.53869814 0.18439376 0.13738865
		 0.18439376 0.13738865 -0.29653841 0.53869814 -0.29653841 0.13181171 0.18439378 -0.26949769
		 0.18439378 -0.26949769 -0.29653835 0.13181171 -0.29653835;
createNode polyAutoProj -n "polyAutoProj8";
	rename -uid "C09C1B90-48C4-4490-82B6-76AFFCBFFEFB";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[44:49]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 6.5366983413696289 6.5366983413696289 6.5366983413696289 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV8";
	rename -uid "94391DDA-4547-BCBF-F477-85BA2B7A835D";
	setAttr ".uopa" yes;
	setAttr -s 22 ".uvtk[196:217]" -type "float2" 0.56602526 -0.84317076 0.32922673
		 0.13644704 0.28228813 0.089042716 0.5074482 -0.85604644 0.34304565 -0.88174403 0.26960713
		 0.12340831 0.2098947 0.11779441 0.28387988 -0.85092473 0.51216447 0.1650418 0.41649544
		 0.15008789 0.56606805 -0.80681634 0.66173708 -0.79186237 0.57218158 -0.84592807 0.66785061
		 -0.8309741 0.12717243 0.10486411 0.03150342 0.089910261 0.18662201 -0.90247482 0.28229094
		 -0.88752097 0.039272614 -0.0068817958 0.024318796 0.08878722 -0.034826033 0.079542361
		 -0.019872211 -0.016126659;
createNode polyAutoProj -n "polyAutoProj9";
	rename -uid "142874C7-4AE0-F834-FEFC-58B2B7837F56";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[50:55]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 3.1270203590393066 3.1270203590393066 3.1270203590393066 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV9";
	rename -uid "3C762E9C-47CA-487A-BF32-D9B5D0A02373";
	setAttr ".uopa" yes;
	setAttr -s 24 ".uvtk[218:241]" -type "float2" 0.90368843 -0.61915368 0.69513547
		 0.12320439 0.61013532 0.091724046 0.81773406 -0.64119327 0.54576588 -0.66868019 0.60156256
		 0.10039495 0.51293647 0.10479007 0.4561857 -0.65484416 0.12018178 0.065095462 -0.12571813
		 0.040243067 -0.050294146 -0.70603269 0.19560571 -0.6811803 0.37333047 0.090680458
		 0.12743054 0.065828122 0.20380862 -0.68988854 0.44970858 -0.6650362 0.05695305 -0.94748473
		 0.032100659 -0.70158488 -0.055189535 -0.71040702 -0.030337192 -0.95630687 0.15014638
		 -0.93806595 0.12529403 -0.69216609 0.038003784 -0.70098829 0.062856175 -0.94688815;
createNode polyAutoProj -n "polyAutoProj10";
	rename -uid "28ADBC80-45A8-74A4-90E3-52AF51E63B54";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[33:37]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 5.5356717109680176 5.5356717109680176 5.5356717109680176 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV10";
	rename -uid "5D860EC3-421A-A8B7-BEEA-678CBD178421";
	setAttr ".uopa" yes;
	setAttr -s 72 ".uvtk";
	setAttr ".uvtk[112]" -type "float2" 0.04321897 -0.058495671 ;
	setAttr ".uvtk[113]" -type "float2" -0.065230668 0.049953967 ;
	setAttr ".uvtk[114]" -type "float2" -0.091960698 0.023223938 ;
	setAttr ".uvtk[115]" -type "float2" 0.01648894 -0.085225694 ;
	setAttr ".uvtk[116]" -type "float2" 0.015713692 -0.086000971 ;
	setAttr ".uvtk[117]" -type "float2" -0.092735946 0.022448691 ;
	setAttr ".uvtk[118]" -type "float2" -0.11946595 -0.0042813104 ;
	setAttr ".uvtk[119]" -type "float2" -0.011016309 -0.11273097 ;
	setAttr ".uvtk[120]" -type "float2" -0.010195792 0.10498882 ;
	setAttr ".uvtk[121]" -type "float2" -0.036925852 0.07825876 ;
	setAttr ".uvtk[122]" -type "float2" 0.071523786 -0.030190853 ;
	setAttr ".uvtk[123]" -type "float2" 0.098253846 -0.0034607928 ;
	setAttr ".uvtk[124]" -type "float2" -0.037725389 0.077459224 ;
	setAttr ".uvtk[125]" -type "float2" -0.06445545 0.050729185 ;
	setAttr ".uvtk[126]" -type "float2" 0.043994188 -0.057720453 ;
	setAttr ".uvtk[127]" -type "float2" 0.070724249 -0.03099039 ;
	setAttr ".uvtk[128]" -type "float2" 0.027817607 -0.12835425 ;
	setAttr ".uvtk[129]" -type "float2" 0.0010875466 -0.10162418 ;
	setAttr ".uvtk[130]" -type "float2" -0.025642514 -0.12835425 ;
	setAttr ".uvtk[131]" -type "float2" 0.0010875466 -0.15508431 ;
	setAttr ".uvtk[132]" -type "float2" 0.028583825 -0.074127905 ;
	setAttr ".uvtk[133]" -type "float2" 0.0018537645 -0.10085797 ;
	setAttr ".uvtk[134]" -type "float2" 0.0075933342 -0.10085797 ;
	setAttr ".uvtk[135]" -type "float2" 0.028583825 -0.079867475 ;
	setAttr ".uvtk[136]" -type "float2" 0.028583765 -0.12758797 ;
	setAttr ".uvtk[137]" -type "float2" 0.028583825 -0.12184846 ;
	setAttr ".uvtk[138]" -type "float2" 0.055313826 -0.10085791 ;
	setAttr ".uvtk[139]" -type "float2" 0.049574316 -0.10085797 ;
	setAttr ".uvtk[140]" -type "float2" -0.12720084 -0.054018676 ;
	setAttr ".uvtk[141]" -type "float2" 0.0084773935 0.081659615 ;
	setAttr ".uvtk[142]" -type "float2" -0.0025421642 0.092679143 ;
	setAttr ".uvtk[143]" -type "float2" -0.1382204 -0.042999089 ;
	setAttr ".uvtk[144]" -type "float2" -0.13901305 -0.042206466 ;
	setAttr ".uvtk[145]" -type "float2" -0.0033348165 0.093471825 ;
	setAttr ".uvtk[146]" -type "float2" -0.014354372 0.10449135 ;
	setAttr ".uvtk[147]" -type "float2" -0.15003261 -0.031186879 ;
	setAttr ".uvtk[148]" -type "float2" 0.082574464 0.0075625181 ;
	setAttr ".uvtk[149]" -type "float2" 0.046423115 0.043713868 ;
	setAttr ".uvtk[150]" -type "float2" -0.089255132 -0.091964364 ;
	setAttr ".uvtk[151]" -type "float2" -0.053103786 -0.12811571 ;
	setAttr ".uvtk[152]" -type "float2" 0.045636661 0.044500351 ;
	setAttr ".uvtk[153]" -type "float2" 0.0094853081 0.0806517 ;
	setAttr ".uvtk[154]" -type "float2" -0.12619293 -0.055026591 ;
	setAttr ".uvtk[155]" -type "float2" -0.090041585 -0.09117794 ;
	setAttr ".uvtk[156]" -type "float2" -0.051298376 0.069132686 ;
	setAttr ".uvtk[157]" -type "float2" -0.015147025 0.10528404 ;
	setAttr ".uvtk[158]" -type "float2" -0.026166642 0.11630362 ;
	setAttr ".uvtk[159]" -type "float2" -0.062317993 0.080152273 ;
	setAttr ".uvtk[160]" -type "float2" -0.06311062 0.080944896 ;
	setAttr ".uvtk[161]" -type "float2" -0.026959265 0.11709625 ;
	setAttr ".uvtk[162]" -type "float2" -0.037978791 0.12811577 ;
	setAttr ".uvtk[163]" -type "float2" -0.07413014 0.091964424 ;
	setAttr ".uvtk[242]" -type "float2" 0.56351066 -0.58269399 ;
	setAttr ".uvtk[243]" -type "float2" 0.37180287 0.41332191 ;
	setAttr ".uvtk[244]" -type "float2" 0.21050121 0.38227543 ;
	setAttr ".uvtk[245]" -type "float2" 0.40220898 -0.6137405 ;
	setAttr ".uvtk[246]" -type "float2" 0.3958376 -0.61496681 ;
	setAttr ".uvtk[247]" -type "float2" 0.2041298 0.3810491 ;
	setAttr ".uvtk[248]" -type "float2" 0.04282802 0.35000262 ;
	setAttr ".uvtk[249]" -type "float2" 0.23453577 -0.64601332 ;
	setAttr ".uvtk[250]" -type "float2" 0.70733988 0.47790417 ;
	setAttr ".uvtk[251]" -type "float2" 0.54603803 0.44685772 ;
	setAttr ".uvtk[252]" -type "float2" 0.73774576 -0.5491581 ;
	setAttr ".uvtk[253]" -type "float2" 0.89904761 -0.51811171 ;
	setAttr ".uvtk[254]" -type "float2" 0.53947616 0.4455947 ;
	setAttr ".uvtk[255]" -type "float2" 0.37817436 0.41454822 ;
	setAttr ".uvtk[256]" -type "float2" 0.56988215 -0.58146757 ;
	setAttr ".uvtk[257]" -type "float2" 0.73118401 -0.55042112 ;
	setAttr ".uvtk[258]" -type "float2" 0.03645676 0.34877634 ;
	setAttr ".uvtk[259]" -type "float2" -0.1248451 0.31772983 ;
	setAttr ".uvtk[260]" -type "float2" -0.093798682 0.15642805 ;
	setAttr ".uvtk[261]" -type "float2" 0.067503199 0.18747456 ;
createNode polyAutoProj -n "polyAutoProj11";
	rename -uid "759717A7-46B0-5CEB-068B-CD881AADEDF6";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[56:58]" "f[60:61]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.57662627100944519 0.57662627100944519 0.57662627100944519 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV11";
	rename -uid "04B51BE4-47B8-AC91-277D-A592F24C2B37";
	setAttr ".uopa" yes;
	setAttr -s 38 ".uvtk[242:279]" -type "float2" -0.13662398 0 -0.13662398
		 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398
		 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398
		 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398 0 -0.13662398
		 0 0.1647841 0.040460419 0.1647841 0.50389278 -0.14223582 0.50389278 -0.14223582 0.31907007
		 -0.088760495 -0.42647251 -0.088760495 0.036959838 -0.39578041 -0.24164978 -0.39578041
		 -0.42647251 0.47775477 0.22528309 0.17073485 0.22528309 0.17073485 0.040460479 0.47775477
		 0.040460479 0.47775477 0.50389278 0.17073485 0.50389278 0.22364026 0.036959838 -0.083379686
		 0.036959838 -0.083379686 -0.27006015 0.22364026 -0.27006015;
createNode polyAutoProj -n "polyAutoProj12";
	rename -uid "8025E77D-47C3-119C-9A0C-AE81F6859FD6";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 5 "f[87:103]" "f[105:109]" "f[111:129]" "f[131:135]" "f[137:138]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.051353931427002 1.051353931427002 1.051353931427002 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV12";
	rename -uid "EF406C41-4356-2536-6D0E-E9AC4C138AC7";
	setAttr ".uopa" yes;
	setAttr -s 180 ".uvtk";
	setAttr ".uvtk[112]" -type "float2" -0.15690967 -0.098737434 ;
	setAttr ".uvtk[113]" -type "float2" 0.1079558 -0.098737434 ;
	setAttr ".uvtk[114]" -type "float2" 0.1079558 -0.033063829 ;
	setAttr ".uvtk[115]" -type "float2" -0.15690966 -0.033063814 ;
	setAttr ".uvtk[116]" -type "float2" -0.00050884462 0.045845836 ;
	setAttr ".uvtk[117]" -type "float2" -0.048734765 0.045845807 ;
	setAttr ".uvtk[118]" -type "float2" -0.048734765 0.033764422 ;
	setAttr ".uvtk[119]" -type "float2" -0.00050884462 0.033764452 ;
	setAttr ".uvtk[120]" -type "float2" -0.048615977 -0.075714372 ;
	setAttr ".uvtk[121]" -type "float2" -0.048615977 -0.087973617 ;
	setAttr ".uvtk[122]" -type "float2" -0.00045051769 -0.087973528 ;
	setAttr ".uvtk[123]" -type "float2" -0.00045051769 -0.075714283 ;
	setAttr ".uvtk[124]" -type "float2" 0.10791862 -0.0087539107 ;
	setAttr ".uvtk[125]" -type "float2" 0.10791862 0.056592599 ;
	setAttr ".uvtk[126]" -type "float2" -0.15720665 0.056592602 ;
	setAttr ".uvtk[127]" -type "float2" -0.15720665 -0.0087539703 ;
	setAttr ".uvtk[280]" -type "float2" 0.79632872 0.16281247 ;
	setAttr ".uvtk[281]" -type "float2" 0.79632872 0.18894321 ;
	setAttr ".uvtk[282]" -type "float2" 0.63561857 0.33604878 ;
	setAttr ".uvtk[283]" -type "float2" 0.61594486 0.3190949 ;
	setAttr ".uvtk[284]" -type "float2" 0.63561857 0.48801607 ;
	setAttr ".uvtk[285]" -type "float2" 0.61594486 0.50111288 ;
	setAttr ".uvtk[286]" -type "float2" 0.7943939 0.51374912 ;
	setAttr ".uvtk[287]" -type "float2" 0.79433894 0.53024477 ;
	setAttr ".uvtk[288]" -type "float2" 0.60980672 0.50411403 ;
	setAttr ".uvtk[289]" -type "float2" 0.60980672 0.53024477 ;
	setAttr ".uvtk[290]" -type "float2" 0.42942268 0.37396234 ;
	setAttr ".uvtk[291]" -type "float2" 0.44909635 0.3570084 ;
	setAttr ".uvtk[292]" -type "float2" 0.42942268 0.19194436 ;
	setAttr ".uvtk[293]" -type "float2" 0.44909635 0.20504117 ;
	setAttr ".uvtk[294]" -type "float2" 0.6078167 0.16281247 ;
	setAttr ".uvtk[295]" -type "float2" 0.60787165 0.17930815 ;
	setAttr ".uvtk[296]" -type "float2" 0.23376793 -0.22961429 ;
	setAttr ".uvtk[297]" -type "float2" 0.23376793 -0.16920629 ;
	setAttr ".uvtk[298]" -type "float2" 0.17700315 -0.16920629 ;
	setAttr ".uvtk[299]" -type "float2" 0.17700315 -0.22961429 ;
	setAttr ".uvtk[300]" -type "float2" 0.17054015 -0.22961429 ;
	setAttr ".uvtk[301]" -type "float2" 0.17054015 -0.16920629 ;
	setAttr ".uvtk[302]" -type "float2" 0.11377518 -0.16920629 ;
	setAttr ".uvtk[303]" -type "float2" 0.11377518 -0.22961429 ;
	setAttr ".uvtk[304]" -type "float2" 0.044084534 -0.22961429 ;
	setAttr ".uvtk[305]" -type "float2" 0.044084534 -0.16920629 ;
	setAttr ".uvtk[306]" -type "float2" -0.012680203 -0.16920629 ;
	setAttr ".uvtk[307]" -type "float2" -0.012680203 -0.22961429 ;
	setAttr ".uvtk[308]" -type "float2" 0.0027597398 -0.29342991 ;
	setAttr ".uvtk[309]" -type "float2" 0.0027597398 -0.23302194 ;
	setAttr ".uvtk[310]" -type "float2" -0.054005116 -0.23302194 ;
	setAttr ".uvtk[311]" -type "float2" -0.054005116 -0.29342991 ;
	setAttr ".uvtk[312]" -type "float2" 0.42345133 0.16281247 ;
	setAttr ".uvtk[313]" -type "float2" 0.42345133 0.18894321 ;
	setAttr ".uvtk[314]" -type "float2" 0.26274088 0.33604878 ;
	setAttr ".uvtk[315]" -type "float2" 0.24306725 0.3190949 ;
	setAttr ".uvtk[316]" -type "float2" 0.26274088 0.48801607 ;
	setAttr ".uvtk[317]" -type "float2" 0.24306725 0.50111288 ;
	setAttr ".uvtk[318]" -type "float2" 0.42151627 0.51374912 ;
	setAttr ".uvtk[319]" -type "float2" 0.42146131 0.53024477 ;
	setAttr ".uvtk[320]" -type "float2" 0.23709565 0.50411403 ;
	setAttr ".uvtk[321]" -type "float2" 0.23709565 0.53024477 ;
	setAttr ".uvtk[322]" -type "float2" 0.056711838 0.37396234 ;
	setAttr ".uvtk[323]" -type "float2" 0.076385528 0.3570084 ;
	setAttr ".uvtk[324]" -type "float2" 0.056711838 0.19194436 ;
	setAttr ".uvtk[325]" -type "float2" 0.076385528 0.20504117 ;
	setAttr ".uvtk[326]" -type "float2" 0.23510593 0.16281247 ;
	setAttr ".uvtk[327]" -type "float2" 0.23516089 0.17930815 ;
	setAttr ".uvtk[328]" -type "float2" 0.12676531 -0.29342991 ;
	setAttr ".uvtk[329]" -type "float2" 0.12676531 -0.23302194 ;
	setAttr ".uvtk[330]" -type "float2" 0.07000044 -0.23302194 ;
	setAttr ".uvtk[331]" -type "float2" 0.07000044 -0.29342991 ;
	setAttr ".uvtk[332]" -type "float2" 0.18876791 -0.29342991 ;
	setAttr ".uvtk[333]" -type "float2" 0.18876791 -0.23302194 ;
	setAttr ".uvtk[334]" -type "float2" 0.13200331 -0.23302194 ;
	setAttr ".uvtk[335]" -type "float2" 0.13200331 -0.29342991 ;
	setAttr ".uvtk[336]" -type "float2" 0.31277364 -0.17257544 ;
	setAttr ".uvtk[337]" -type "float2" 0.31277364 -0.11216748 ;
	setAttr ".uvtk[338]" -type "float2" 0.25600865 -0.11216748 ;
	setAttr ".uvtk[339]" -type "float2" 0.25600865 -0.17257544 ;
	setAttr ".uvtk[340]" -type "float2" 0.37477598 -0.17257544 ;
	setAttr ".uvtk[341]" -type "float2" 0.37477598 -0.11216748 ;
	setAttr ".uvtk[342]" -type "float2" 0.31801122 -0.11216748 ;
	setAttr ".uvtk[343]" -type "float2" 0.31801122 -0.17257544 ;
	setAttr ".uvtk[344]" -type "float2" 0.39650953 -0.13674256 ;
	setAttr ".uvtk[345]" -type "float2" 0.39650953 -0.11218774 ;
	setAttr ".uvtk[346]" -type "float2" 0.38001388 -0.1121676 ;
	setAttr ".uvtk[347]" -type "float2" 0.38001388 -0.13672259 ;
	setAttr ".uvtk[348]" -type "float2" 0.065997273 -0.13966802 ;
	setAttr ".uvtk[349]" -type "float2" 0.04144226 -0.13966802 ;
	setAttr ".uvtk[350]" -type "float2" 0.04144226 -0.16579881 ;
	setAttr ".uvtk[351]" -type "float2" 0.065997273 -0.16579881 ;
	setAttr ".uvtk[352]" -type "float2" 0.12449104 0.0074375309 ;
	setAttr ".uvtk[353]" -type "float2" 0.099936053 0.0074375309 ;
	setAttr ".uvtk[354]" -type "float2" 0.12449104 0.15940481 ;
	setAttr ".uvtk[355]" -type "float2" 0.099936053 0.15940481 ;
	setAttr ".uvtk[356]" -type "float2" 0.050740108 0.3739624 ;
	setAttr ".uvtk[357]" -type "float2" 0.02618508 0.3739624 ;
	setAttr ".uvtk[358]" -type "float2" 0.02618508 0.19194442 ;
	setAttr ".uvtk[359]" -type "float2" 0.050740108 0.19194442 ;
	setAttr ".uvtk[360]" -type "float2" -0.014914304 0.53024483 ;
	setAttr ".uvtk[361]" -type "float2" -0.039469332 0.53024483 ;
	setAttr ".uvtk[362]" -type "float2" 0.29699561 -0.031334281 ;
	setAttr ".uvtk[363]" -type "float2" 0.24023074 -0.031334281 ;
	setAttr ".uvtk[364]" -type "float2" 0.24023074 -0.091742277 ;
	setAttr ".uvtk[365]" -type "float2" 0.29699561 -0.091742277 ;
	setAttr ".uvtk[366]" -type "float2" 0.10731231 -0.16920629 ;
	setAttr ".uvtk[367]" -type "float2" 0.050547406 -0.16920629 ;
	setAttr ".uvtk[368]" -type "float2" 0.050547406 -0.22961429 ;
	setAttr ".uvtk[369]" -type "float2" 0.10731231 -0.22961429 ;
	setAttr ".uvtk[370]" -type "float2" 0.41824314 -0.13672259 ;
	setAttr ".uvtk[371]" -type "float2" 0.41824314 -0.1121676 ;
	setAttr ".uvtk[372]" -type "float2" 0.40174749 -0.11218774 ;
	setAttr ".uvtk[373]" -type "float2" 0.40174749 -0.13674256 ;
	setAttr ".uvtk[374]" -type "float2" 0.21277773 -0.13966802 ;
	setAttr ".uvtk[375]" -type "float2" 0.18822271 -0.13966802 ;
	setAttr ".uvtk[376]" -type "float2" 0.18822271 -0.16579881 ;
	setAttr ".uvtk[377]" -type "float2" 0.21277773 -0.16579881 ;
	setAttr ".uvtk[378]" -type "float2" 0.15428406 0.0074375309 ;
	setAttr ".uvtk[379]" -type "float2" 0.12972897 0.0074375309 ;
	setAttr ".uvtk[380]" -type "float2" 0.15428406 0.15940481 ;
	setAttr ".uvtk[381]" -type "float2" 0.12972897 0.15940481 ;
	setAttr ".uvtk[382]" -type "float2" -0.029450089 0.032254431 ;
	setAttr ".uvtk[383]" -type "float2" -0.054005116 0.032254431 ;
	setAttr ".uvtk[384]" -type "float2" -0.054005116 -0.14976349 ;
	setAttr ".uvtk[385]" -type "float2" -0.029450089 -0.14976349 ;
	setAttr ".uvtk[386]" -type "float2" 0.036204383 0.18853685 ;
	setAttr ".uvtk[387]" -type "float2" 0.011649355 0.18853685 ;
	setAttr ".uvtk[388]" -type "float2" 0.064762563 -0.23302183 ;
	setAttr ".uvtk[389]" -type "float2" 0.0079976767 -0.23302183 ;
	setAttr ".uvtk[390]" -type "float2" 0.0079976767 -0.29342985 ;
	setAttr ".uvtk[391]" -type "float2" 0.064762563 -0.29342985 ;
	setAttr ".uvtk[392]" -type "float2" 0.25077075 -0.23302183 ;
	setAttr ".uvtk[393]" -type "float2" 0.19400579 -0.23302183 ;
	setAttr ".uvtk[394]" -type "float2" 0.19400579 -0.29342985 ;
	setAttr ".uvtk[395]" -type "float2" 0.25077075 -0.29342985 ;
	setAttr ".uvtk[396]" -type "float2" 0.3075009 0.15046751 ;
	setAttr ".uvtk[397]" -type "float2" 0.28294575 0.15940481 ;
	setAttr ".uvtk[398]" -type "float2" 0.21801573 -0.018989325 ;
	setAttr ".uvtk[399]" -type "float2" 0.24257068 -0.027926564 ;
	setAttr ".uvtk[400]" -type "float2" 0.46525124 -0.0083078742 ;
	setAttr ".uvtk[401]" -type "float2" 0.48980632 0.00062942505 ;
	setAttr ".uvtk[402]" -type "float2" 0.43201676 0.15940481 ;
	setAttr ".uvtk[403]" -type "float2" 0.40746179 0.15046749 ;
	setAttr ".uvtk[404]" -type "float2" 0.6600523 0.10263991 ;
	setAttr ".uvtk[405]" -type "float2" 0.63939154 0.15940481 ;
	setAttr ".uvtk[406]" -type "float2" 0.5826267 0.13874409 ;
	setAttr ".uvtk[407]" -type "float2" 0.60328734 0.081979185 ;
	setAttr ".uvtk[408]" -type "float2" 0.72205514 0.081979185 ;
	setAttr ".uvtk[409]" -type "float2" 0.74271578 0.13874409 ;
	setAttr ".uvtk[410]" -type "float2" 0.68595088 0.15940481 ;
	setAttr ".uvtk[411]" -type "float2" 0.66529024 0.10263991 ;
	setAttr ".uvtk[412]" -type "float2" 0.79632866 0.021806661 ;
	setAttr ".uvtk[413]" -type "float2" 0.77566791 0.078571528 ;
	setAttr ".uvtk[414]" -type "float2" 0.71890306 0.05791083 ;
	setAttr ".uvtk[415]" -type "float2" 0.73956382 0.0011458993 ;
	setAttr ".uvtk[416]" -type "float2" 0.69230711 0.0011458993 ;
	setAttr ".uvtk[417]" -type "float2" 0.71296787 0.05791083 ;
	setAttr ".uvtk[418]" -type "float2" 0.65620291 0.078571528 ;
	setAttr ".uvtk[419]" -type "float2" 0.63554227 0.021806661 ;
	setAttr ".uvtk[420]" -type "float2" 0.33729374 0.15940481 ;
	setAttr ".uvtk[421]" -type "float2" 0.31273878 0.15046751 ;
	setAttr ".uvtk[422]" -type "float2" 0.37766889 -0.027926564 ;
	setAttr ".uvtk[423]" -type "float2" 0.40222389 -0.018989325 ;
	setAttr ".uvtk[424]" -type "float2" 0.49504423 0.00062942505 ;
	setAttr ".uvtk[425]" -type "float2" 0.51959926 -0.0083078742 ;
	setAttr ".uvtk[426]" -type "float2" 0.57738876 0.15046749 ;
	setAttr ".uvtk[427]" -type "float2" 0.55283374 0.15940481 ;
	setAttr ".uvtk[428]" -type "float2" 0.60911268 -0.08914125 ;
	setAttr ".uvtk[429]" -type "float2" 0.62977338 -0.032376289 ;
	setAttr ".uvtk[430]" -type "float2" 0.57300854 -0.011715591 ;
	setAttr ".uvtk[431]" -type "float2" 0.55234778 -0.068480492 ;
	setAttr ".uvtk[432]" -type "float2" 0.546579 -0.068480432 ;
	setAttr ".uvtk[433]" -type "float2" 0.5259183 -0.011715591 ;
	setAttr ".uvtk[434]" -type "float2" 0.46915334 -0.032376289 ;
	setAttr ".uvtk[435]" -type "float2" 0.48981404 -0.08914119 ;
	setAttr ".uvtk[436]" -type "float2" 0.44272375 -0.10875988 ;
	setAttr ".uvtk[437]" -type "float2" 0.46338448 -0.051994979 ;
	setAttr ".uvtk[438]" -type "float2" 0.40661958 -0.031334221 ;
	setAttr ".uvtk[439]" -type "float2" 0.38595885 -0.088099189 ;
	setAttr ".uvtk[440]" -type "float2" 0.38019004 -0.088099129 ;
	setAttr ".uvtk[441]" -type "float2" 0.35952944 -0.031334221 ;
	setAttr ".uvtk[442]" -type "float2" 0.30276451 -0.051994979 ;
	setAttr ".uvtk[443]" -type "float2" 0.32342523 -0.10875988 ;
createNode polyMapSew -n "polyMapSew1";
	rename -uid "A65ED0BA-460F-2A38-CE05-6BB41A9921CF";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[36:37]" "e[40]";
createNode polyTweakUV -n "polyTweakUV13";
	rename -uid "C048FD3C-4E0A-696A-E959-71B5AACDF4A7";
	setAttr ".uopa" yes;
	setAttr -s 14 ".uvtk[112:125]" -type "float2" -0.00014849007 0 -7.7784061e-06
		 0 -2.9414892e-05 -2.9802322e-08 -7.0527196e-05 2.9802322e-08 1.8328428e-05 -2.9802322e-08
		 0.00013737381 2.9802322e-08 6.6846609e-05 2.9802322e-08 -2.1904707e-05 -2.9802322e-08
		 2.9414892e-05 0 0.00014849007 0 0.011181788 -0.016988052 -0.00090844824 -0.016988052
		 -0.00090844824 -0.029472155 0.011181788 -0.029472155;
createNode polyMapSew -n "polyMapSew2";
	rename -uid "0CA77F02-4C9A-A8D4-1269-478D985ED6C4";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[39]";
createNode polyTweakUV -n "polyTweakUV14";
	rename -uid "3989B6C0-42C8-4F0C-37C6-94868F09097B";
	setAttr ".uopa" yes;
	setAttr -s 8 ".uvtk[124:131]" -type "float2" -0.15769383 0.033814318 -0.15769383
		 0.099221587 -0.16471606 0.092199355 -0.16471606 0.04083655 -0.22310096 0.099221587
		 -0.21607886 0.092199355 -0.22310096 0.033814318 -0.21607886 0.04083655;
createNode polyMapSew -n "polyMapSew3";
	rename -uid "949CD320-4110-A045-307D-FFAA7EF870EC";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[30]";
createNode polyTweakUV -n "polyTweakUV15";
	rename -uid "6EF1D276-4BDC-8F59-6582-73BB526EE52A";
	setAttr ".uopa" yes;
	setAttr -s 34 ".uvtk";
	setAttr ".uvtk[112]" -type "float2" 5.6514276e-05 -9.6678734e-05 ;
	setAttr ".uvtk[113]" -type "float2" -0.00031725346 -4.9293041e-05 ;
	setAttr ".uvtk[114]" -type "float2" -0.00031725346 -6.3835832e-06 ;
	setAttr ".uvtk[115]" -type "float2" 5.6514276e-05 9.3185976e-05 ;
	setAttr ".uvtk[116]" -type "float2" -0.00027138769 -3.1252312e-05 ;
	setAttr ".uvtk[117]" -type "float2" 0.00012507447 -9.8575758e-05 ;
	setAttr ".uvtk[118]" -type "float2" 5.6514276e-05 -1.2780758e-05 ;
	setAttr ".uvtk[119]" -type "float2" -0.00027138769 -1.2780758e-05 ;
	setAttr ".uvtk[120]" -type "float2" -0.00027138769 0.00014022415 ;
	setAttr ".uvtk[121]" -type "float2" 0.00012507447 0.00014523094 ;
	setAttr ".uvtk[122]" -type "float2" -5.5950833e-05 -6.2991778e-05 ;
	setAttr ".uvtk[123]" -type "float2" -5.5950833e-05 -1.9268264e-06 ;
	setAttr ".uvtk[124]" -type "float2" 9.0801848e-05 0.00014523094 ;
	setAttr ".uvtk[125]" -type "float2" 9.0801848e-05 0 ;
	setAttr ".uvtk[126]" -type "float2" -0.00017699486 0.00015023773 ;
	setAttr ".uvtk[127]" -type "float2" -0.00017699486 0.00014523094 ;
	setAttr ".uvtk[128]" -type "float2" -0.00017699486 -0.000165929 ;
	setAttr ".uvtk[129]" -type "float2" -0.00017699486 0 ;
	setAttr ".uvtk[154]" -type "float2" 0.079089925 -0.030167475 ;
	setAttr ".uvtk[155]" -type "float2" 0.079089925 -0.049031567 ;
	setAttr ".uvtk[156]" -type "float2" 0.083709881 -0.049031567 ;
	setAttr ".uvtk[157]" -type "float2" 0.088816121 -0.030167475 ;
	setAttr ".uvtk[158]" -type "float2" 0.088028781 -0.068077922 ;
	setAttr ".uvtk[159]" -type "float2" 0.088028781 0.012970712 ;
	setAttr ".uvtk[160]" -type "float2" 0.046240464 0.012970712 ;
	setAttr ".uvtk[161]" -type "float2" 0.068179332 -0.068077922 ;
	setAttr ".uvtk[162]" -type "float2" 0.066031396 0.0011191959 ;
	setAttr ".uvtk[163]" -type "float2" 0.066031396 -0.017559526 ;
	setAttr ".uvtk[164]" -type "float2" 0.070605963 -0.017559526 ;
	setAttr ".uvtk[165]" -type "float2" 0.070605963 0.0011191959 ;
	setAttr ".uvtk[166]" -type "float2" 0.11668402 -0.017731234 ;
	setAttr ".uvtk[167]" -type "float2" 0.11668402 0.0012201843 ;
	setAttr ".uvtk[168]" -type "float2" 0.097732604 0.0012201843 ;
	setAttr ".uvtk[169]" -type "float2" 0.097732604 -0.017731234 ;
createNode polyPinUV -n "polyPinUV1";
	rename -uid "ED6C9B09-4BD1-4735-6EA5-B580F379B9DF";
	setAttr ".uopa" yes;
	setAttr ".op" 3;
createNode polyPinUV -n "polyPinUV2";
	rename -uid "BEC8674B-42E7-CB47-4659-C984DA9B4F4D";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV3";
	rename -uid "707F4FE8-4284-2846-E9E8-EAA1A3D4FB12";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[112:119]";
	setAttr -s 8 ".pn[112:119]"  1 1 1 1 1 1 1 1;
createNode polyPinUV -n "polyPinUV4";
	rename -uid "8F2EBE5B-4B51-F048-E475-B087035294C9";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV5";
	rename -uid "48828F48-4EBC-7F53-7F98-5CBDE7E38802";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[112:123]" "map[154:169]";
	setAttr -s 28 ".pn";
	setAttr ".pn[112]" 1;
	setAttr ".pn[113]" 1;
	setAttr ".pn[114]" 1;
	setAttr ".pn[115]" 1;
	setAttr ".pn[116]" 1;
	setAttr ".pn[117]" 1;
	setAttr ".pn[118]" 1;
	setAttr ".pn[119]" 1;
	setAttr ".pn[120]" 1;
	setAttr ".pn[121]" 1;
	setAttr ".pn[122]" 1;
	setAttr ".pn[123]" 1;
	setAttr ".pn[154]" 1;
	setAttr ".pn[155]" 1;
	setAttr ".pn[156]" 1;
	setAttr ".pn[157]" 1;
	setAttr ".pn[158]" 1;
	setAttr ".pn[159]" 1;
	setAttr ".pn[160]" 1;
	setAttr ".pn[161]" 1;
	setAttr ".pn[162]" 1;
	setAttr ".pn[163]" 1;
	setAttr ".pn[164]" 1;
	setAttr ".pn[165]" 1;
	setAttr ".pn[166]" 1;
	setAttr ".pn[167]" 1;
	setAttr ".pn[168]" 1;
	setAttr ".pn[169]" 1;
createNode polyPinUV -n "polyPinUV6";
	rename -uid "391EDD13-4C43-E305-DE1F-EC9F0ECAF568";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[116:117]" "map[120:121]";
	setAttr -s 4 ".pn";
	setAttr ".pn[116]" 0;
	setAttr ".pn[117]" 0;
	setAttr ".pn[120]" 0;
	setAttr ".pn[121]" 0;
	setAttr ".op" 1;
createNode polyPinUV -n "polyPinUV7";
	rename -uid "4B6C071B-470B-A318-AEE1-6DA3E45C9F3C";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew4";
	rename -uid "1F6A2B39-4F83-C35A-E18E-95932878490B";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[53]" "e[55:56]";
createNode polyTweakUV -n "polyTweakUV16";
	rename -uid "AE096565-4B14-E540-00CA-4EBC1BFF94AC";
	setAttr ".uopa" yes;
	setAttr -s 76 ".uvtk";
	setAttr ".uvtk[0]" -type "float2" 0.00033662468 0.028879222 ;
	setAttr ".uvtk[1]" -type "float2" 0.00033564866 0.028633747 ;
	setAttr ".uvtk[2]" -type "float2" 0.002501728 0.028633747 ;
	setAttr ".uvtk[3]" -type "float2" 0.0014775172 0.030951187 ;
	setAttr ".uvtk[4]" -type "float2" 0.00060604513 0.030409936 ;
	setAttr ".uvtk[5]" -type "float2" 0.0012320206 0.029247914 ;
	setAttr ".uvtk[6]" -type "float2" 0.0011135153 0.028879222 ;
	setAttr ".uvtk[7]" -type "float2" 0.00081725046 0.028879222 ;
	setAttr ".uvtk[8]" -type "float2" 0.00068555772 0.029050399 ;
	setAttr ".uvtk[9]" -type "float2" 0.0004090406 0.029050399 ;
	setAttr ".uvtk[10]" -type "float2" 0.066757135 -0.048711669 ;
	setAttr ".uvtk[11]" -type "float2" 0.066690393 -0.04855394 ;
	setAttr ".uvtk[12]" -type "float2" 0.066435643 -0.04855394 ;
	setAttr ".uvtk[13]" -type "float2" 0.066314332 -0.048711669 ;
	setAttr ".uvtk[14]" -type "float2" 0.066041373 -0.048711669 ;
	setAttr ".uvtk[15]" -type "float2" 0.065932222 -0.048371967 ;
	setAttr ".uvtk[16]" -type "float2" 0.066508926 -0.047301389 ;
	setAttr ".uvtk[17]" -type "float2" 0.065706052 -0.046802748 ;
	setAttr ".uvtk[18]" -type "float2" 0.064762414 -0.048937809 ;
	setAttr ".uvtk[19]" -type "float2" 0.066758059 -0.048937809 ;
	setAttr ".uvtk[20]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[21]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[22]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[23]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[24]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[25]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[26]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[27]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[28]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[29]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[30]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[31]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[32]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[33]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[34]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[35]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[36]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[37]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[38]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[39]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[40]" -type "float2" -0.031211155 0.070934452 ;
	setAttr ".uvtk[41]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[42]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[43]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[44]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[45]" -type "float2" -0.031211155 0.070934445 ;
	setAttr ".uvtk[92]" -type "float2" 0.042503875 0.032824766 ;
	setAttr ".uvtk[93]" -type "float2" 0.04250386 0.032824766 ;
	setAttr ".uvtk[94]" -type "float2" 0.04250386 0.032824766 ;
	setAttr ".uvtk[95]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[96]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[97]" -type "float2" 0.042503875 0.032824758 ;
	setAttr ".uvtk[98]" -type "float2" 0.042503875 0.032824766 ;
	setAttr ".uvtk[99]" -type "float2" 0.042503875 0.032824766 ;
	setAttr ".uvtk[100]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[101]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[102]" -type "float2" 0.04250386 0.032824766 ;
	setAttr ".uvtk[103]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[104]" -type "float2" 0.042503845 0.032824758 ;
	setAttr ".uvtk[105]" -type "float2" 0.04250386 0.032824758 ;
	setAttr ".uvtk[106]" -type "float2" 0.042503875 0.032824766 ;
	setAttr ".uvtk[107]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[108]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[109]" -type "float2" 0.042503875 0.032824766 ;
	setAttr ".uvtk[110]" -type "float2" 0.042503875 0.032824766 ;
	setAttr ".uvtk[111]" -type "float2" 0.042503845 0.032824766 ;
	setAttr ".uvtk[154]" -type "float2" 6.7695975e-05 -0.0076710046 ;
	setAttr ".uvtk[155]" -type "float2" 8.2999468e-05 -0.0076189102 ;
	setAttr ".uvtk[156]" -type "float2" 4.0456653e-05 -0.0076536597 ;
	setAttr ".uvtk[157]" -type "float2" 0 -0.0076710046 ;
	setAttr ".uvtk[158]" -type "float2" 7.6591969e-06 -0.0077573122 ;
	setAttr ".uvtk[159]" -type "float2" -8.2999468e-05 -0.0076710046 ;
	setAttr ".uvtk[160]" -type "float2" 0 -0.0076710046 ;
	setAttr ".uvtk[161]" -type "float2" -4.0456653e-05 -0.0076997639 ;
	setAttr ".uvtk[162]" -type "float2" 0 -0.0075846971 ;
	setAttr ".uvtk[163]" -type "float2" 0 -0.0077230991 ;
createNode polyMapDel -n "polyMapDel2";
	rename -uid "13D198EB-4533-E003-4D37-B795F41C6694";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 4 "f[0:3]" "f[5]" "f[7:9]" "f[11]";
createNode polyAutoProj -n "polyAutoProj13";
	rename -uid "7C46E648-4676-DF29-4092-ECB1AFC78B17";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 4 "f[0:3]" "f[5]" "f[7:9]" "f[11]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 2.468988299369812 2.468988299369812 2.468988299369812 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweak -n "polyTweak20";
	rename -uid "604EB6F0-401A-D859-7311-39A17C227F39";
	setAttr ".uopa" yes;
	setAttr -s 40 ".tk";
	setAttr ".tk[0]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[1]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[2]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[3]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[4]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[5]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[6]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[7]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[8]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[9]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[10]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[11]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[12]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[13]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[14]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[15]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[16]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[17]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[18]" -type "float3" -0.047271408 0 0 ;
	setAttr ".tk[19]" -type "float3" 0.047271408 0 0 ;
	setAttr ".tk[84]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[85]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[86]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[87]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[88]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[89]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[90]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[91]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[92]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[93]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[94]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[95]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[96]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[97]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[98]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[99]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[100]" -type "float3" 0.027671078 0 0 ;
	setAttr ".tk[101]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[102]" -type "float3" -0.027671078 0 0 ;
	setAttr ".tk[103]" -type "float3" 0.027671078 0 0 ;
createNode polyTweakUV -n "polyTweakUV17";
	rename -uid "09C9FAA4-41BA-8CC2-746B-F8BCDE31322D";
	setAttr ".uopa" yes;
	setAttr -s 46 ".uvtk";
	setAttr ".uvtk[0]" -type "float2" -0.015524078 0.015524073 ;
	setAttr ".uvtk[1]" -type "float2" -0.015524078 0.015524073 ;
	setAttr ".uvtk[2]" -type "float2" -0.015524074 0.015524073 ;
	setAttr ".uvtk[3]" -type "float2" -0.015524074 0.015524073 ;
	setAttr ".uvtk[4]" -type "float2" -0.015524078 0.015524076 ;
	setAttr ".uvtk[5]" -type "float2" -0.015524074 0.015524073 ;
	setAttr ".uvtk[6]" -type "float2" -0.015524074 0.015524073 ;
	setAttr ".uvtk[7]" -type "float2" -0.015524078 0.015524073 ;
	setAttr ".uvtk[8]" -type "float2" -0.015524078 0.015524073 ;
	setAttr ".uvtk[9]" -type "float2" -0.015524078 0.015524073 ;
	setAttr ".uvtk[10]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[11]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[12]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[13]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[14]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[15]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[16]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[17]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[18]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[19]" -type "float2" -0.015524071 0.015524073 ;
	setAttr ".uvtk[402]" -type "float2" -0.30949458 0.0034241825 ;
	setAttr ".uvtk[403]" -type "float2" -0.51571482 0.0034241825 ;
	setAttr ".uvtk[404]" -type "float2" -0.51571482 -0.11999461 ;
	setAttr ".uvtk[405]" -type "float2" -0.30949458 -0.11999461 ;
	setAttr ".uvtk[406]" -type "float2" -0.51571482 -0.17484735 ;
	setAttr ".uvtk[407]" -type "float2" -0.30949458 -0.17484735 ;
	setAttr ".uvtk[408]" -type "float2" -0.51571482 -0.29003826 ;
	setAttr ".uvtk[409]" -type "float2" -0.30949458 -0.29003826 ;
	setAttr ".uvtk[410]" -type "float2" -0.097630516 -0.22204137 ;
	setAttr ".uvtk[411]" -type "float2" -0.3038508 -0.22204137 ;
	setAttr ".uvtk[412]" -type "float2" -0.3038508 -0.70610851 ;
	setAttr ".uvtk[413]" -type "float2" -0.097630516 -0.70610851 ;
	setAttr ".uvtk[414]" -type "float2" -0.097630516 0.0034241527 ;
	setAttr ".uvtk[415]" -type "float2" -0.3038508 0.0034241527 ;
	setAttr ".uvtk[416]" -type "float2" -0.3038508 -0.85969639 ;
	setAttr ".uvtk[417]" -type "float2" -0.097630516 -0.85969639 ;
	setAttr ".uvtk[418]" -type "float2" -0.59266728 -0.20279613 ;
	setAttr ".uvtk[419]" -type "float2" -0.59266728 0.0034241527 ;
	setAttr ".uvtk[420]" -type "float2" -0.69492567 0.0034241527 ;
	setAttr ".uvtk[421]" -type "float2" -0.69492567 -0.20279613 ;
	setAttr ".uvtk[422]" -type "float2" -0.52135861 -0.20279613 ;
	setAttr ".uvtk[423]" -type "float2" -0.52135861 0.0034241527 ;
	setAttr ".uvtk[424]" -type "float2" 0.11440714 0.0034241825 ;
	setAttr ".uvtk[425]" -type "float2" -0.091813117 0.0034241825 ;
	setAttr ".uvtk[426]" -type "float2" -0.091813117 -0.96195471 ;
	setAttr ".uvtk[427]" -type "float2" 0.11440714 -0.96195471 ;
createNode polyMapCut -n "polyMapCut1";
	rename -uid "96766F17-4D9D-3401-AB44-C9910A3F9E33";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "e[0:16]" "e[18]" "e[20:29]";
createNode polyTweakUV -n "polyTweakUV18";
	rename -uid "709A7860-48B2-F94C-C1CC-FC963732BF95";
	setAttr ".uopa" yes;
	setAttr -s 36 ".uvtk[402:437]" -type "float2" -0.027716938 0.072535276
		 -0.018878657 0.072535276 -0.018878657 0.076252922 -0.027827002 0.076369256 -0.018815525
		 0.08258979 -0.027840292 0.082575724 -0.018802246 0.086738184 -0.027840292 0.086738184
		 -0.021030556 0.01066053 0.011084228 0.026622087 -0.012249184 0.034525316 -0.020993354
		 0.034568951 0.049345359 0.01800964 0.042800941 0.033777416 -0.012316587 0.04518722
		 -0.020993354 0.04518722 -0.036773518 0.097755909 -0.021338539 0.10405055 -0.024633078
		 0.11176676 -0.040018752 0.1052222 -0.034559809 0.089678019 -0.01907553 0.096222579
		 0.11853716 0.11979717 0.096212536 0.11979717 0.096212536 0.010863811 0.11853716 0.010863811
		 -0.027827002 0.08258979 -0.036822837 0.097505994 -0.018802246 0.082575724 -0.021387843
		 0.10430047 -0.018815525 0.076369256 -0.027716938 0.076252922 -0.012316587 0.034568951
		 -0.021030556 0.034525316 -0.012249184 0.01066053 0.017628688 0.010854312;
createNode polyMapSew -n "polyMapSew5";
	rename -uid "6E98BFE6-45A4-F12B-0EED-1994B14D6698";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 5 "e[1]" "e[12]" "e[15:16]" "e[24]" "e[28]";
createNode polyTweakUV -n "polyTweakUV19";
	rename -uid "58456F6C-44C8-A975-D366-8E988E36C753";
	setAttr ".uopa" yes;
	setAttr -s 88 ".uvtk";
	setAttr ".uvtk[6]" -type "float2" 0.001817218 0 ;
	setAttr ".uvtk[14]" -type "float2" -0.0018172144 0 ;
	setAttr ".uvtk[20]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[21]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[22]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[23]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[24]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[25]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[26]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[27]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[28]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[29]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[30]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[31]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[32]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[33]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[34]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[35]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[36]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[37]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[38]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[39]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[40]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[41]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[42]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[43]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[44]" -type "float2" 0.054191299 -0.0091590863 ;
	setAttr ".uvtk[45]" -type "float2" 0.054191299 -0.0091590863 ;
	setAttr ".uvtk[46]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[47]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[48]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[49]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[50]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[51]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[52]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[53]" -type "float2" 0.054191299 -0.00915909 ;
	setAttr ".uvtk[54]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[55]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[56]" -type "float2" 0.054191299 -0.0091590863 ;
	setAttr ".uvtk[57]" -type "float2" 0.054191299 -0.0091590863 ;
	setAttr ".uvtk[58]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[59]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[60]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[61]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[62]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[63]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[64]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[65]" -type "float2" 0.054191299 -0.0091590937 ;
	setAttr ".uvtk[66]" -type "float2" 0.0074287029 0.0055960543 ;
	setAttr ".uvtk[67]" -type "float2" 0.021296123 -0.0091032088 ;
	setAttr ".uvtk[68]" -type "float2" 0.031678509 0.009921697 ;
	setAttr ".uvtk[69]" -type "float2" 0.0032337673 0.025444767 ;
	setAttr ".uvtk[70]" -type "float2" -0.013253199 -0.0047662207 ;
	setAttr ".uvtk[71]" -type "float2" 0.0098984167 -0.027952574 ;
	setAttr ".uvtk[72]" -type "float2" 0.0074920012 -0.0049048699 ;
	setAttr ".uvtk[73]" -type "float2" -0.0091197249 -0.03511592 ;
	setAttr ".uvtk[74]" -type "float2" 0.019325068 -0.050756451 ;
	setAttr ".uvtk[75]" -type "float2" 0.029786019 -0.03173162 ;
	setAttr ".uvtk[76]" -type "float2" 0.075371638 -0.032197852 ;
	setAttr ".uvtk[77]" -type "float2" 0.05924489 -0.032197852 ;
	setAttr ".uvtk[78]" -type "float2" 0.05924489 -0.06967441 ;
	setAttr ".uvtk[79]" -type "float2" 0.075371638 -0.06967441 ;
	setAttr ".uvtk[80]" -type "float2" 0.066772178 -0.027638081 ;
	setAttr ".uvtk[81]" -type "float2" 0.050768841 -0.027638081 ;
	setAttr ".uvtk[82]" -type "float2" 0.050768841 -0.060607947 ;
	setAttr ".uvtk[83]" -type "float2" 0.066772178 -0.060607947 ;
	setAttr ".uvtk[84]" -type "float2" 0.066772178 -0.0050040558 ;
	setAttr ".uvtk[85]" -type "float2" 0.050768841 -0.0050040558 ;
	setAttr ".uvtk[402]" -type "float2" -6.0625374e-05 0.00017095151 ;
	setAttr ".uvtk[403]" -type "float2" 5.1729381e-05 0.00017095151 ;
	setAttr ".uvtk[404]" -type "float2" -6.0617924e-05 0.00025643181 ;
	setAttr ".uvtk[405]" -type "float2" -5.6326389e-06 0.00025643181 ;
	setAttr ".uvtk[406]" -type "float2" -9.8824501e-05 0.00017095151 ;
	setAttr ".uvtk[407]" -type "float2" 5.6050718e-05 0.00017095151 ;
	setAttr ".uvtk[408]" -type "float2" -5.838275e-05 0 ;
	setAttr ".uvtk[409]" -type "float2" 6.0632825e-05 0 ;
	setAttr ".uvtk[410]" -type "float2" -2.3379922e-05 0.00026797957 ;
	setAttr ".uvtk[411]" -type "float2" 0.00024046334 -0.0079393014 ;
	setAttr ".uvtk[412]" -type "float2" 9.881705e-05 0 ;
	setAttr ".uvtk[413]" -type "float2" -4.1984022e-05 0 ;
	setAttr ".uvtk[414]" -type "float2" -0.00024047171 -0.0077777542 ;
	setAttr ".uvtk[415]" -type "float2" -0.00024059092 -0.0079393014 ;
	setAttr ".uvtk[416]" -type "float2" 3.3929944e-05 -0.00017095151 ;
	setAttr ".uvtk[417]" -type "float2" 1.3358891e-05 -0.00017095151 ;
	setAttr ".uvtk[418]" -type "float2" 3.8012862e-05 0 ;
	setAttr ".uvtk[419]" -type "float2" 9.2685223e-06 0 ;
	setAttr ".uvtk[424]" -type "float2" 6.5118074e-05 0.00026797957 ;
	setAttr ".uvtk[425]" -type "float2" 0.00024055275 -0.0077777542 ;
createNode polyPinUV -n "polyPinUV8";
	rename -uid "48B138AE-40DC-1760-B748-11BF2F903DDC";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 4 "map[402:410]" "map[412:413]" "map[416:419]" "map[424]";
	setAttr -s 16 ".pn";
	setAttr ".pn[402]" 1;
	setAttr ".pn[403]" 1;
	setAttr ".pn[404]" 1;
	setAttr ".pn[405]" 1;
	setAttr ".pn[406]" 1;
	setAttr ".pn[407]" 1;
	setAttr ".pn[408]" 1;
	setAttr ".pn[409]" 1;
	setAttr ".pn[410]" 1;
	setAttr ".pn[412]" 1;
	setAttr ".pn[413]" 1;
	setAttr ".pn[416]" 1;
	setAttr ".pn[417]" 1;
	setAttr ".pn[418]" 1;
	setAttr ".pn[419]" 1;
	setAttr ".pn[424]" 1;
createNode polyPinUV -n "polyPinUV9";
	rename -uid "3305BB98-4762-EA33-7873-57A299003016";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew6";
	rename -uid "16878147-4DC9-A52C-2B65-E7BD800A9870";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[168]";
createNode polyTweak -n "polyTweak21";
	rename -uid "2E782D23-4803-2D6A-712A-42BB40167DAF";
	setAttr ".uopa" yes;
	setAttr -s 2 ".tk[8:9]" -type "float3"  0 0 0.053224873 0 0 0.053224873;
createNode polyTweakUV -n "polyTweakUV20";
	rename -uid "156BCFBA-4650-4097-3BB0-C19234130E3F";
	setAttr ".uopa" yes;
	setAttr -s 28 ".uvtk";
	setAttr ".uvtk[20]" -type "float2" -0.023260206 -0.04397276 ;
	setAttr ".uvtk[21]" -type "float2" -0.0023884475 -0.041581661 ;
	setAttr ".uvtk[22]" -type "float2" -0.0089217126 -0.025808007 ;
	setAttr ".uvtk[23]" -type "float2" -0.040308416 -0.0011152029 ;
	setAttr ".uvtk[24]" -type "float2" 0.021668404 -0.01370129 ;
	setAttr ".uvtk[25]" -type "float2" 0.010196298 -0.013381332 ;
	setAttr ".uvtk[26]" -type "float2" 0.028200358 0.021826409 ;
	setAttr ".uvtk[27]" -type "float2" 0.020233303 0.01545497 ;
	setAttr ".uvtk[28]" -type "float2" 0.015455037 0.051777922 ;
	setAttr ".uvtk[29]" -type "float2" 0.0049388409 0.041264348 ;
	setAttr ".uvtk[30]" -type "float2" -0.11621828 0.022946851 ;
	setAttr ".uvtk[31]" -type "float2" -0.11694289 0.041343383 ;
	setAttr ".uvtk[32]" -type "float2" -0.15634359 0.050368048 ;
	setAttr ".uvtk[33]" -type "float2" -0.13075426 0.026492426 ;
	setAttr ".uvtk[34]" -type "float2" -0.13895603 0.0082706138 ;
	setAttr ".uvtk[35]" -type "float2" -0.13714243 -0.0016220931 ;
	setAttr ".uvtk[36]" -type "float2" -0.16258581 -0.0042842799 ;
	setAttr ".uvtk[37]" -type "float2" -0.16703679 -0.01203767 ;
	setAttr ".uvtk[38]" -type "float2" -0.18698688 0.0055049015 ;
	setAttr ".uvtk[39]" -type "float2" -0.19468382 -0.0050109313 ;
	setAttr ".uvtk[76]" -type "float2" 0.0078202225 0.011665769 ;
	setAttr ".uvtk[77]" -type "float2" 0.0079425313 0.011665769 ;
	setAttr ".uvtk[78]" -type "float2" 0.0079721548 0.022362586 ;
	setAttr ".uvtk[79]" -type "float2" 0.0077881557 0.022362586 ;
	setAttr ".uvtk[80]" -type "float2" 0.0078522898 0.0033329865 ;
	setAttr ".uvtk[81]" -type "float2" 0.0079128779 0.0033329865 ;
	setAttr ".uvtk[82]" -type "float2" 0.0078522898 7.742233e-05 ;
	setAttr ".uvtk[83]" -type "float2" 0.0079128779 7.742233e-05 ;
createNode polyFlipUV -n "polyFlipUV1";
	rename -uid "F137F970-497D-588F-FDAE-E4B7934DE1FB";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 4 "f[66]" "f[69]" "f[73]" "f[77]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".up" yes;
	setAttr ".pu" 0.38660776619999998;
	setAttr ".pv" 0.065100602810000005;
createNode polyTweakUV -n "polyTweakUV21";
	rename -uid "78C43003-4093-37A8-A6B6-27BDCE8B0A9C";
	setAttr ".uopa" yes;
	setAttr -s 10 ".uvtk[20:29]" -type "float2" -0.120991 0.018098511 -0.11786286
		 0.020583596 -0.11662031 0.017583985 -0.11752043 0.010043016 -0.11087889 0.020069443
		 -0.11237852 0.018483773 -0.1052232 0.016170405 -0.10715125 0.015955845 -0.10290947
		 0.010428993 -0.10573732 0.010428635;
createNode polyFlipUV -n "polyFlipUV2";
	rename -uid "CDC4597D-413B-68EA-0722-C79FA90496AB";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 4 "f[66]" "f[69]" "f[73]" "f[77]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".up" yes;
	setAttr ".pu" 0.27465753259999998;
	setAttr ".pv" 0.080413907770000001;
createNode polyTweakUV -n "polyTweakUV22";
	rename -uid "5A49C125-4229-DB1D-B3F8-60B803745F16";
	setAttr ".uopa" yes;
	setAttr -s 22 ".uvtk";
	setAttr ".uvtk[20]" -type "float2" 0.075918823 0 ;
	setAttr ".uvtk[21]" -type "float2" 0.075918853 0 ;
	setAttr ".uvtk[22]" -type "float2" 0.075918823 0 ;
	setAttr ".uvtk[23]" -type "float2" 0.075918823 0 ;
	setAttr ".uvtk[24]" -type "float2" 0.075918823 0 ;
	setAttr ".uvtk[25]" -type "float2" 0.075918853 0 ;
	setAttr ".uvtk[26]" -type "float2" 0.075918853 0 ;
	setAttr ".uvtk[27]" -type "float2" 0.075918853 0 ;
	setAttr ".uvtk[28]" -type "float2" 0.075918853 0 ;
	setAttr ".uvtk[29]" -type "float2" 0.075918853 0 ;
	setAttr ".uvtk[46]" -type "float2" -0.19008704 -0.031961408 ;
	setAttr ".uvtk[47]" -type "float2" -0.1743979 -0.047177803 ;
	setAttr ".uvtk[48]" -type "float2" -0.14921169 -0.021209173 ;
	setAttr ".uvtk[49]" -type "float2" -0.16490082 -0.0059927786 ;
	setAttr ".uvtk[50]" -type "float2" -0.20136799 -0.043592837 ;
	setAttr ".uvtk[51]" -type "float2" -0.18567885 -0.058809232 ;
	setAttr ".uvtk[52]" -type "float2" -0.12881561 -0.00017937232 ;
	setAttr ".uvtk[53]" -type "float2" -0.14450474 0.015037024 ;
	setAttr ".uvtk[62]" -type "float2" -0.12170239 -0.074047521 ;
	setAttr ".uvtk[63]" -type "float2" -0.10601337 -0.089342579 ;
	setAttr ".uvtk[64]" -type "float2" -0.085200936 -0.067994103 ;
	setAttr ".uvtk[65]" -type "float2" -0.10088997 -0.052699041 ;
createNode polyMapSew -n "polyMapSew7";
	rename -uid "F27BB501-4708-7CFD-8097-4ABD57C40B28";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "e[133]" "e[148]" "e[156]";
createNode polyTweakUV -n "polyTweakUV23";
	rename -uid "138F1A90-43C1-3F63-7111-16934C5E7AEB";
	setAttr ".uopa" yes;
	setAttr -s 24 ".uvtk[40:63]" -type "float2" -0.17936462 0.065592319 -0.19505364
		 0.050139658 -0.17951894 0.034367312 -0.16382992 0.049819969 -0.16037506 0.014930617
		 -0.14468604 0.03038327 -0.010731028 0.00022664835 -0.010731028 -4.2522486e-05 -0.012154205
		 -4.2518761e-05 -0.012154205 0.00022665208 -0.01112272 0.00022664835 -0.01112272 -4.2522486e-05
		 -0.0057815327 3.6651109e-05 -0.0057815327 0.00026649033 -0.1906718 0.056981903 -0.20636082
		 0.041530851 -0.17874637 0.013491064 -0.16305736 0.028942138 -0.095586881 0.056989271
		 -0.11127593 0.041536588 -0.091033697 0.020984728 -0.075344697 0.036437411 -0.0032799281
		 0.00011581912 -0.0032799281 0.00030632486;
createNode polyMapSew -n "polyMapSew8";
	rename -uid "9580F9F1-4A64-0E93-A97D-BAB1FD2B5D74";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "e[132]" "e[144]" "e[152]";
createNode polyTweakUV -n "polyTweakUV24";
	rename -uid "E0699B27-43EB-0E9B-4D87-BE9F63D05412";
	setAttr ".uopa" yes;
	setAttr -s 54 ".uvtk";
	setAttr ".uvtk[40]" -type "float2" -0.0091486797 2.9802322e-08 ;
	setAttr ".uvtk[41]" -type "float2" -0.0091486797 2.9802322e-08 ;
	setAttr ".uvtk[42]" -type "float2" -0.015360719 -2.8461218e-06 ;
	setAttr ".uvtk[43]" -type "float2" -0.015360719 -3.6209822e-06 ;
	setAttr ".uvtk[44]" -type "float2" -0.015453549 -2.8461218e-06 ;
	setAttr ".uvtk[45]" -type "float2" -0.015453609 -3.6358833e-06 ;
	setAttr ".uvtk[54]" -type "float2" 0 3.695488e-06 ;
	setAttr ".uvtk[55]" -type "float2" 0 2.8610229e-06 ;
	setAttr ".uvtk[56]" -type "float2" -0.020011676 -2.8759241e-06 ;
	setAttr ".uvtk[57]" -type "float2" -0.020011647 -3.6805868e-06 ;
	setAttr ".uvtk[78]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[79]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[80]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[81]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[82]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[83]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[84]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[85]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[86]" -type "float2" 0 -0.10947087 ;
	setAttr ".uvtk[87]" -type "float2" 0 -0.10947087 ;
	setAttr ".uvtk[88]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[89]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[90]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[91]" -type "float2" 0 -0.10947087 ;
	setAttr ".uvtk[92]" -type "float2" 0 -0.10947087 ;
	setAttr ".uvtk[93]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[94]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[95]" -type "float2" 0 -0.10947087 ;
	setAttr ".uvtk[120]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[121]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[122]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[123]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[124]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[125]" -type "float2" 0 -0.10947087 ;
	setAttr ".uvtk[126]" -type "float2" 0 -0.10947087 ;
	setAttr ".uvtk[127]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[128]" -type "float2" 0 -0.1094709 ;
	setAttr ".uvtk[129]" -type "float2" 0 -0.10947089 ;
	setAttr ".uvtk[130]" -type "float2" -0.11789583 -0.045413814 ;
	setAttr ".uvtk[131]" -type "float2" -0.11794285 -0.045413814 ;
	setAttr ".uvtk[132]" -type "float2" -0.11795978 -0.045490466 ;
	setAttr ".uvtk[133]" -type "float2" -0.11789583 -0.045490466 ;
	setAttr ".uvtk[134]" -type "float2" -0.13387305 -0.045430101 ;
	setAttr ".uvtk[135]" -type "float2" -0.13387305 -0.045430101 ;
	setAttr ".uvtk[136]" -type "float2" -0.13387305 -0.045430101 ;
	setAttr ".uvtk[137]" -type "float2" -0.13387305 -0.045430101 ;
	setAttr ".uvtk[138]" -type "float2" -0.11858216 -0.03001505 ;
	setAttr ".uvtk[139]" -type "float2" -0.11564007 -0.03001505 ;
	setAttr ".uvtk[140]" -type "float2" -0.11564007 -0.03001505 ;
	setAttr ".uvtk[141]" -type "float2" -0.11858216 -0.03001505 ;
	setAttr ".uvtk[142]" -type "float2" -0.11411662 -0.06038785 ;
	setAttr ".uvtk[143]" -type "float2" -0.11411662 -0.06038785 ;
	setAttr ".uvtk[144]" -type "float2" -0.11411662 -0.06038785 ;
	setAttr ".uvtk[145]" -type "float2" -0.11411662 -0.06038785 ;
createNode polyMapSew -n "polyMapSew9";
	rename -uid "CC40AB8A-4218-1FF7-F3B5-10AA7AC4A402";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[86:87]";
createNode polyTweakUV -n "polyTweakUV25";
	rename -uid "D71788F7-49E0-E968-8DD6-02B2ECDDE9FF";
	setAttr ".uopa" yes;
	setAttr -s 12 ".uvtk[130:141]" -type "float2" -6.0640916e-05 -0.00014233455
		 3.3504621e-05 -0.0001300709 6.7181245e-05 0.00045149162 -6.0640916e-05 0.00040090218
		 0.0001081918 -0.00011379883 0.00020409568 -0.00013419852 0.00020409568 0.00037071243
		 7.4336363e-05 0.00039111212 0.015209728 0.029996958 0.015209728 0.029996958 0.015209728
		 0.029996958 0.015209728 0.029996958;
createNode polyFlipUV -n "polyFlipUV3";
	rename -uid "F3A389A7-4BE6-5E9A-C9BB-009AE02F30A5";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[40]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".up" yes;
	setAttr ".pu" 0.46088171010000001;
	setAttr ".pv" 0.16362628339999999;
createNode polyTweakUV -n "polyTweakUV26";
	rename -uid "68F103D4-44E9-C22F-95EC-7ABC359A6B4F";
	setAttr ".uopa" yes;
	setAttr -s 4 ".uvtk[138:141]" -type "float2" -0.012735844 0.015262723
		 0.012735844 0.015262723 0.012735844 -0.015262723 -0.012735844 -0.015262723;
createNode polyFlipUV -n "polyFlipUV4";
	rename -uid "5C077260-4775-6757-EBC1-B6B900BA8F1D";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[40]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".up" yes;
	setAttr ".pu" 0.46088171010000001;
	setAttr ".pv" 0.16362628339999999;
createNode polyTweakUV -n "polyTweakUV27";
	rename -uid "A6A854CE-4CB6-607C-01BC-1285FFCE0750";
	setAttr ".uopa" yes;
	setAttr -s 34 ".uvtk[130:163]" -type "float2" -0.046995632 7.9184771e-05
		 -0.046995662 7.9184771e-05 -0.046995662 -7.9184771e-05 -0.046995632 -7.9184771e-05
		 -0.046995662 7.9184771e-05 -0.046995662 7.9184771e-05 -0.046995662 -7.9184771e-05
		 -0.046995662 -7.9184771e-05 -0.045479521 0.00057637552 -0.048268303 0.00057637552
		 -0.048268303 0.00026860693 -0.045479521 0.00026860693 0 0.038162705 0 0.038162705
		 0 0.038162705 0 0.038162705 0.050712332 0.0085340338 -0.044975117 -0.0042332588 -0.044407561
		 -0.010182662 0.048092589 0.0016322923 0 0.038162705 0 0.038162705 0 0.038162705 0
		 0.038162705 0 0.038162705 0 0.038162705 -0.044541936 0.0027102737 -0.044541936 -0.0059259771
		 0.042585392 -0.0059259771 0.042585392 0.0027102663 -0.060767964 0.010743505 -0.060767964
		 0.010743505 -0.060767964 0.010743505 -0.060767964 0.010743505;
createNode polyMapCut -n "polyMapCut2";
	rename -uid "91420F0C-41D5-F67A-D3D8-3289458A18BA";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[96:97]" "e[102:103]";
createNode polyTweakUV -n "polyTweakUV28";
	rename -uid "BE4BB4C6-407A-0431-4FC9-949DD595A424";
	setAttr ".uopa" yes;
	setAttr -s 11 ".uvtk";
	setAttr ".uvtk[146]" -type "float2" -0.25074002 -0.015194125 ;
	setAttr ".uvtk[147]" -type "float2" 0.24205519 -0.015194163 ;
	setAttr ".uvtk[148]" -type "float2" 0.24205519 0.015194207 ;
	setAttr ".uvtk[149]" -type "float2" -0.23358051 0.01519423 ;
	setAttr ".uvtk[150]" -type "float2" 0.19742018 -0.080461159 ;
	setAttr ".uvtk[151]" -type "float2" 0.19742018 -0.041541532 ;
	setAttr ".uvtk[152]" -type "float2" -0.18508214 -0.041541338 ;
	setAttr ".uvtk[156]" -type "float2" -2.9802322e-08 -1.4901161e-08 ;
	setAttr ".uvtk[157]" -type "float2" -2.9802322e-08 0 ;
	setAttr ".uvtk[159]" -type "float2" 0 -1.4901161e-08 ;
	setAttr ".uvtk[415]" -type "float2" -0.18508214 -0.080461077 ;
createNode polyMapSew -n "polyMapSew10";
	rename -uid "66467AA9-450B-6C2D-9296-6BAB69B7ED5C";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[100]" "e[104]";
createNode polyTweakUV -n "polyTweakUV29";
	rename -uid "B08CF71C-469C-8C42-CED7-2097FC87DAB7";
	setAttr ".uopa" yes;
	setAttr -s 12 ".uvtk";
	setAttr ".uvtk[142]" -type "float2" 0.042216167 -0.02558809 ;
	setAttr ".uvtk[143]" -type "float2" -0.043913573 -0.012820791 ;
	setAttr ".uvtk[144]" -type "float2" -0.041540518 -0.019778693 ;
	setAttr ".uvtk[145]" -type "float2" 0.041648552 -0.031593654 ;
	setAttr ".uvtk[146]" -type "float2" -5.0097704e-05 -7.4505806e-09 ;
	setAttr ".uvtk[147]" -type "float2" -0.00011366606 7.4505806e-09 ;
	setAttr ".uvtk[148]" -type "float2" 2.8192997e-05 2.2351742e-08 ;
	setAttr ".uvtk[149]" -type "float2" 0.00010144711 -2.2351742e-08 ;
	setAttr ".uvtk[150]" -type "float2" 0.00014191866 5.9604645e-08 ;
	setAttr ".uvtk[151]" -type "float2" -0.00010144711 -5.9604645e-08 ;
	setAttr ".uvtk[155]" -type "float2" 5.0097704e-05 0 ;
	setAttr ".uvtk[156]" -type "float2" -0.00014185905 7.4505806e-09 ;
createNode polyMapSew -n "polyMapSew11";
	rename -uid "BB3554A1-4841-AA69-0BD1-AF98732ECC47";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[101]";
createNode polyTweakUV -n "polyTweakUV30";
	rename -uid "4A7F803F-4A93-CC97-BD5E-6F9BAF8F86AD";
	setAttr ".uopa" yes;
	setAttr -s 17 ".uvtk";
	setAttr ".uvtk[142]" -type "float2" 0.00011366606 0.00014640353 ;
	setAttr ".uvtk[143]" -type "float2" 0 0.00014640353 ;
	setAttr ".uvtk[144]" -type "float2" -4.5984983e-05 0 ;
	setAttr ".uvtk[146]" -type "float2" 0 -0.00014640258 ;
	setAttr ".uvtk[147]" -type "float2" -0.00011372566 -0.00014640258 ;
	setAttr ".uvtk[148]" -type "float2" -0.00011372566 0.00029281451 ;
	setAttr ".uvtk[149]" -type "float2" 4.6014786e-05 0.00029281451 ;
	setAttr ".uvtk[150]" -type "float2" -0.18508814 -0.080238871 ;
	setAttr ".uvtk[151]" -type "float2" -0.20090733 -0.041544408 ;
	setAttr ".uvtk[152]" -type "float2" -0.20090733 -0.080238871 ;
	setAttr ".uvtk[153]" -type "float2" 0 0.00014640353 ;
	setAttr ".uvtk[154]" -type "float2" -0.00011372566 0.00014640353 ;
	setAttr ".uvtk[155]" -type "float2" 0.25465581 0.027484149 ;
	setAttr ".uvtk[156]" -type "float2" 0.26960963 0.042438 ;
	setAttr ".uvtk[157]" -type "float2" 0.26036474 0.05168286 ;
	setAttr ".uvtk[158]" -type "float2" 0.24541092 0.036729019 ;
	setAttr ".uvtk[409]" -type "float2" -0.18508814 -0.041544408 ;
createNode polyMapSew -n "polyMapSew12";
	rename -uid "1E89E1B7-4834-59E9-6A7E-CEBA6A29B979";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[96]";
createNode polyTweakUV -n "polyTweakUV31";
	rename -uid "1C8B837E-46C6-7569-BF02-A58A6806E62F";
	setAttr ".uopa" yes;
	setAttr -s 4 ".uvtk[154:157]" -type "float2" 0.0034363135 -0.0040452471
		 0.0034362539 0.0046429061 -0.0019350178 0.0046429061 -0.0019350178 -0.0040452471;
createNode polyMapSew -n "polyMapSew13";
	rename -uid "B19EEC05-4D57-7690-175C-06B6597413E9";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[95]";
createNode polyTweakUV -n "polyTweakUV32";
	rename -uid "88192965-4799-31DE-F1C3-D4BF1759AA69";
	setAttr ".uopa" yes;
	setAttr -s 36 ".uvtk";
	setAttr ".uvtk[142]" -type "float2" 0.00018479722 0.00013717177 ;
	setAttr ".uvtk[143]" -type "float2" 0 0.00013717177 ;
	setAttr ".uvtk[144]" -type "float2" -0.0011447858 -0.00015941243 ;
	setAttr ".uvtk[145]" -type "float2" 7.8045297e-05 -0.00011216085 ;
	setAttr ".uvtk[147]" -type "float2" 0.00018479722 0 ;
	setAttr ".uvtk[148]" -type "float2" 7.8045297e-05 -2.6464462e-05 ;
	setAttr ".uvtk[149]" -type "float2" -0.0011447858 -2.4467707e-05 ;
	setAttr ".uvtk[150]" -type "float2" -0.0071879495 -0.00020056944 ;
	setAttr ".uvtk[151]" -type "float2" -0.0071879495 2.6457012e-05 ;
	setAttr ".uvtk[153]" -type "float2" 0.00018479722 0 ;
	setAttr ".uvtk[154]" -type "float2" 0.0010395444 2.2485852e-05 ;
	setAttr ".uvtk[155]" -type "float2" 0.0010396636 -0.00010605138 ;
	setAttr ".uvtk[156]" -type "float2" -0.038742803 -0.029035386 ;
	setAttr ".uvtk[157]" -type "float2" 0.15656076 -0.015676813 ;
	setAttr ".uvtk[158]" -type "float2" 0.15427075 0.0083321594 ;
	setAttr ".uvtk[159]" -type "float2" -0.040078655 -0.0050262879 ;
	setAttr ".uvtk[160]" -type "float2" 0.078695767 -0.032257143 ;
	setAttr ".uvtk[161]" -type "float2" 0.036970474 -0.045615692 ;
	setAttr ".uvtk[162]" -type "float2" 0.038306333 -0.052247807 ;
	setAttr ".uvtk[163]" -type "float2" 0.07977163 -0.038889226 ;
	setAttr ".uvtk[164]" -type "float2" 0.038100306 -0.012473432 ;
	setAttr ".uvtk[165]" -type "float2" 0.038100306 -0.02678686 ;
	setAttr ".uvtk[166]" -type "float2" 0.079749122 -0.026786875 ;
	setAttr ".uvtk[167]" -type "float2" 0.079749182 -0.01247344 ;
	setAttr ".uvtk[168]" -type "float2" 0.15646124 -0.022574421 ;
	setAttr ".uvtk[169]" -type "float2" 0.15646124 0.041575857 ;
	setAttr ".uvtk[170]" -type "float2" -0.038311906 0.041575879 ;
	setAttr ".uvtk[171]" -type "float2" -0.038312085 -0.022574395 ;
	setAttr ".uvtk[172]" -type "float2" -0.14146593 0.095500655 ;
	setAttr ".uvtk[173]" -type "float2" -0.14146593 0.095500655 ;
	setAttr ".uvtk[174]" -type "float2" -0.14146593 0.095500663 ;
	setAttr ".uvtk[175]" -type "float2" -0.14146593 0.095500663 ;
	setAttr ".uvtk[176]" -type "float2" -0.14146593 0.095500655 ;
	setAttr ".uvtk[177]" -type "float2" -0.14146593 0.095500655 ;
	setAttr ".uvtk[178]" -type "float2" -0.14146593 0.095500655 ;
	setAttr ".uvtk[179]" -type "float2" -0.14146593 0.095500655 ;
createNode polyPinUV -n "polyPinUV10";
	rename -uid "2F65FC92-47E5-FA63-444C-DB8654EA86AB";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[142:149]" "map[152:153]";
	setAttr -s 10 ".pn";
	setAttr ".pn[142]" 1;
	setAttr ".pn[143]" 1;
	setAttr ".pn[144]" 1;
	setAttr ".pn[145]" 1;
	setAttr ".pn[146]" 1;
	setAttr ".pn[147]" 1;
	setAttr ".pn[148]" 1;
	setAttr ".pn[149]" 1;
	setAttr ".pn[152]" 1;
	setAttr ".pn[153]" 1;
createNode polyPinUV -n "polyPinUV11";
	rename -uid "700085F2-40D4-D88C-3BB5-84AF1F51474F";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV12";
	rename -uid "40A2BF9F-499B-ABDB-20EA-4798A137AEF7";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[144:155]";
	setAttr -s 12 ".pn[144:155]"  1 1 1 1 1 1 1 1 1 1 1 1;
createNode polyPinUV -n "polyPinUV13";
	rename -uid "E29E69E2-4E38-E29C-9FB2-A291F59E1943";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew14";
	rename -uid "F7D61A2C-4864-F3FF-EDB0-AC90BAC4C25C";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[112]" "e[116:117]";
createNode polyTweak -n "polyTweak22";
	rename -uid "FF04BC33-48AD-C348-8AF7-8182BDEDE2B0";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk";
	setAttr ".tk[60]" -type "float3" 0 -7.4505806e-09 0 ;
	setAttr ".tk[61]" -type "float3" 0 -7.4505806e-09 0 ;
	setAttr ".tk[66]" -type "float3" 0 -7.4505806e-09 0.051113423 ;
	setAttr ".tk[67]" -type "float3" 0 -7.4505806e-09 0.051113423 ;
	setAttr ".tk[68]" -type "float3" 0 -0.06239761 0.00593379 ;
	setAttr ".tk[69]" -type "float3" 0 -0.06239761 0.00593379 ;
	setAttr ".tk[74]" -type "float3" 0 -0.06239761 0.00593379 ;
	setAttr ".tk[75]" -type "float3" 0 -0.06239761 0.00593379 ;
createNode polyTweakUV -n "polyTweakUV33";
	rename -uid "D40DA6EC-4AD0-481D-275F-7CB097450F0D";
	setAttr ".uopa" yes;
	setAttr -s 18 ".uvtk[156:173]" -type "float2" -0.015611803 -0.00011040833
		 -0.016916949 -0.00011041579 -0.016966719 0.00021767855 -0.015396392 0.0002176711
		 -0.01684143 0.00021767855 -0.015947368 0.00021767855 -0.015964534 0.00010831792 -0.015590167
		 0.00010831792 -0.015861368 0.00021765992 -0.015578901 0.00021769718 0.047764648 -0.091461979
		 0.07261695 -0.13029286 0.087854758 -0.12147074 0.063002571 -0.082640037 0.19613032
		 -0.085077092 0.2209826 -0.046121571 0.20520942 -0.037299369 0.18035702 -0.076255076;
createNode polyMapSew -n "polyMapSew15";
	rename -uid "A1652157-4285-968B-35AD-829BFF02BBE9";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[107]" "e[109]";
createNode polyTweakUV -n "polyTweakUV34";
	rename -uid "A66B6EBE-46C3-3B0B-95C5-0496845ADC9A";
	setAttr ".uopa" yes;
	setAttr -s 15 ".uvtk";
	setAttr ".uvtk[156]" -type "float2" -0.10928261 -1.3120472e-05 ;
	setAttr ".uvtk[157]" -type "float2" -0.10926347 0 ;
	setAttr ".uvtk[158]" -type "float2" -0.10928266 0 ;
	setAttr ".uvtk[159]" -type "float2" -0.10928261 0 ;
	setAttr ".uvtk[160]" -type "float2" -0.10926347 8.9406967e-08 ;
	setAttr ".uvtk[161]" -type "float2" -0.10928255 5.2303076e-05 ;
	setAttr ".uvtk[162]" -type "float2" -0.10926365 9.3132257e-08 ;
	setAttr ".uvtk[163]" -type "float2" -0.10928272 -6.0271472e-05 ;
	setAttr ".uvtk[164]" -type "float2" -0.10926359 3.7252903e-09 ;
	setAttr ".uvtk[165]" -type "float2" -0.10928272 3.2583252e-05 ;
	setAttr ".uvtk[166]" -type "float2" -0.10927319 6.0271472e-05 ;
	setAttr ".uvtk[167]" -type "float2" -0.10927307 -3.2585114e-05 ;
	setAttr ".uvtk[168]" -type "float2" -0.10927313 -5.2303076e-05 ;
	setAttr ".uvtk[169]" -type "float2" -0.10927313 1.3127923e-05 ;
createNode deleteComponent -n "deleteComponent9";
	rename -uid "B6CB6DB5-4600-93EA-EF72-73ACDFF975BD";
	setAttr ".dc" -type "componentList" 5 "f[87:103]" "f[105:109]" "f[111:129]" "f[131:135]" "f[137:138]";
createNode polyCube -n "polyCube15";
	rename -uid "8A11E9D0-44B8-99CB-7BDF-229C19241B55";
	setAttr ".cuv" 4;
createNode polyUnite -n "polyUnite3";
	rename -uid "7C201119-4284-D1C4-9702-32960EA0C841";
	setAttr -s 3 ".ip";
	setAttr -s 3 ".im";
createNode groupId -n "groupId29";
	rename -uid "14D843D2-404E-6A3A-B7B9-67B549C226DB";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts14";
	rename -uid "E3A6B4C7-4CD4-4C68-282F-93BB4BBCB702";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:90]";
createNode groupId -n "groupId30";
	rename -uid "E0D2B688-4057-08AD-9F93-BBB7949B51A8";
	setAttr ".ihi" 0;
createNode groupId -n "groupId31";
	rename -uid "9DCD53AE-47AD-40BB-D30D-2E84C8710885";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts15";
	rename -uid "44F08354-4FF4-F4B6-43E1-2F8CECFDC4CE";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId32";
	rename -uid "EFED688F-4ACD-7B39-A1B1-2CA915835B6E";
	setAttr ".ihi" 0;
createNode groupId -n "groupId33";
	rename -uid "4E949D7D-4E4A-C305-DC5D-66868CE35D2E";
	setAttr ".ihi" 0;
createNode groupId -n "groupId34";
	rename -uid "0731400F-4287-B935-1576-3997A7E88924";
	setAttr ".ihi" 0;
createNode polyMapDel -n "polyMapDel3";
	rename -uid "ABF9794B-468A-FD58-22F0-AFA88235F67A";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "f[91:93]" "f[95:99]" "f[101:102]";
createNode polyMapDel -n "polyMapDel4";
	rename -uid "60E3664A-4922-805E-D289-E090C785A816";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[94]" "f[100]";
createNode polyAutoProj -n "polyAutoProj14";
	rename -uid "7C1D9BCE-4D90-1535-0BBA-4ABE65EEB176";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "f[91:93]" "f[95:99]" "f[101:102]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.0362100601196289 1.0362100601196289 1.0362100601196289 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV35";
	rename -uid "0A940E47-4807-2854-D361-03A5ED4812FF";
	setAttr ".uopa" yes;
	setAttr -s 56 ".uvtk";
	setAttr ".uvtk[170]" -type "float2" -0.33435091 -0.21995758 ;
	setAttr ".uvtk[171]" -type "float2" 0.045213591 -0.21995755 ;
	setAttr ".uvtk[172]" -type "float2" 0.045213532 -0.15786462 ;
	setAttr ".uvtk[173]" -type "float2" -0.33435103 -0.15786444 ;
	setAttr ".uvtk[174]" -type "float2" -0.14644213 -0.093956374 ;
	setAttr ".uvtk[175]" -type "float2" -0.14248945 -0.093956344 ;
	setAttr ".uvtk[176]" -type "float2" -0.14248945 -0.094420932 ;
	setAttr ".uvtk[177]" -type "float2" -0.14644213 -0.094420932 ;
	setAttr ".uvtk[178]" -type "float2" -0.14264327 -0.22237751 ;
	setAttr ".uvtk[179]" -type "float2" -0.14264327 -0.22237752 ;
	setAttr ".uvtk[180]" -type "float2" -0.14649421 -0.22237752 ;
	setAttr ".uvtk[181]" -type "float2" -0.14649421 -0.22237751 ;
	setAttr ".uvtk[182]" -type "float2" 0.045193337 -0.15840611 ;
	setAttr ".uvtk[183]" -type "float2" 0.045193516 -0.096313126 ;
	setAttr ".uvtk[184]" -type "float2" -0.33437121 -0.096313156 ;
	setAttr ".uvtk[185]" -type "float2" -0.33437133 -0.15840605 ;
	setAttr ".uvtk[186]" -type "float2" -0.17879476 0.063744217 ;
	setAttr ".uvtk[187]" -type "float2" -0.17879476 0.063744217 ;
	setAttr ".uvtk[188]" -type "float2" -0.17879476 0.063744217 ;
	setAttr ".uvtk[189]" -type "float2" -0.17879476 0.063744217 ;
	setAttr ".uvtk[232]" -type "float2" 0.80983925 0.14956288 ;
	setAttr ".uvtk[233]" -type "float2" 0.80983925 0.59801233 ;
	setAttr ".uvtk[234]" -type "float2" 0.58544701 0.58554375 ;
	setAttr ".uvtk[235]" -type "float2" 0.5876444 0.3046627 ;
	setAttr ".uvtk[236]" -type "float2" 0.57858479 0.14956288 ;
	setAttr ".uvtk[237]" -type "float2" 0.57858479 0.59801233 ;
	setAttr ".uvtk[238]" -type "float2" 0.35638982 0.44291255 ;
	setAttr ".uvtk[239]" -type "float2" 0.3541925 0.16203156 ;
	setAttr ".uvtk[240]" -type "float2" 0.34749907 0.14956288 ;
	setAttr ".uvtk[241]" -type "float2" 0.34749907 0.59801233 ;
	setAttr ".uvtk[242]" -type "float2" 0.1231069 0.58554375 ;
	setAttr ".uvtk[243]" -type "float2" 0.12530422 0.3046627 ;
	setAttr ".uvtk[244]" -type "float2" 0.17221069 -0.30234107 ;
	setAttr ".uvtk[245]" -type "float2" 0.17221069 0.14610837 ;
	setAttr ".uvtk[246]" -type "float2" -0.049984187 -0.0089913756 ;
	setAttr ".uvtk[247]" -type "float2" -0.052181572 -0.28987244 ;
	setAttr ".uvtk[248]" -type "float2" 0.32678372 -0.0089913756 ;
	setAttr ".uvtk[249]" -type "float2" 0.25839299 -0.0089913756 ;
	setAttr ".uvtk[250]" -type "float2" 0.25919271 -0.28987244 ;
	setAttr ".uvtk[251]" -type "float2" 0.32758346 -0.28987244 ;
	setAttr ".uvtk[252]" -type "float2" 0.24591136 0.14610837 ;
	setAttr ".uvtk[253]" -type "float2" 0.17752057 0.14610837 ;
	setAttr ".uvtk[254]" -type "float2" 0.40208378 -0.0089913756 ;
	setAttr ".uvtk[255]" -type "float2" 0.33369306 -0.0089913756 ;
	setAttr ".uvtk[256]" -type "float2" 0.33289331 -0.28987244 ;
	setAttr ".uvtk[257]" -type "float2" 0.4012841 -0.28987244 ;
	setAttr ".uvtk[258]" -type "float2" 0.4829562 0.14610837 ;
	setAttr ".uvtk[259]" -type "float2" 0.41456547 0.14610837 ;
	setAttr ".uvtk[260]" -type "float2" 0.63832891 0.12121622 ;
	setAttr ".uvtk[261]" -type "float2" 0.56993818 0.14610837 ;
	setAttr ".uvtk[262]" -type "float2" 0.48826605 -0.078283735 ;
	setAttr ".uvtk[263]" -type "float2" 0.55665672 -0.103176 ;
	setAttr ".uvtk[264]" -type "float2" 0.71202946 0.14610837 ;
	setAttr ".uvtk[265]" -type "float2" 0.64363885 0.12121622 ;
	setAttr ".uvtk[266]" -type "float2" 0.72531092 -0.103176 ;
	setAttr ".uvtk[267]" -type "float2" 0.79370153 -0.078283735 ;
createNode polyMapSew -n "polyMapSew16";
	rename -uid "1FAF0EF6-4C5C-041B-5223-E4BC46392AC9";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[76:77]" "e[80]";
createNode polyTweakUV -n "polyTweakUV36";
	rename -uid "A5CF220C-45C2-1393-7B5A-5EAE36424155";
	setAttr ".uopa" yes;
	setAttr -s 14 ".uvtk[170:183]" -type "float2" 2.8282404e-05 2.9802322e-08
		 -0.00056859851 -2.9802322e-08 -0.00056859851 0.00025646182 2.8371811e-05 0.00025640221
		 -0.00058457255 2.9802322e-08 -3.8444996e-05 -2.9802322e-08 -4.8547983e-05 0.00025640221
		 -0.00059464574 0.00025646182 -0.00054851174 0 4.8518181e-05 0 0.25467378 -0.1880206
		 0.22347289 -0.15697409 0.1924265 -0.18817483 0.22362733 -0.2192214;
createNode polyPinUV -n "polyPinUV14";
	rename -uid "52674597-4C3B-E059-FC5D-9984E9C40811";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[180:183]";
	setAttr -s 4 ".pn[180:183]"  1 1 1 1;
createNode polyPinUV -n "polyPinUV15";
	rename -uid "1E60E93D-4C7B-A06A-2F4B-2AB9D3DC5118";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew17";
	rename -uid "20712758-44F1-7442-804E-E282312D17DC";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[79]";
createNode polyTweakUV -n "polyTweakUV37";
	rename -uid "8502A105-4425-C170-A276-F8B125C725E8";
	setAttr ".uopa" yes;
	setAttr -s 51 ".uvtk";
	setAttr ".uvtk[96]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[97]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[98]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[99]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[100]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[101]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[102]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[103]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[104]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[105]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[106]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[107]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[108]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[109]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[110]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[111]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[112]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[113]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[114]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[115]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[116]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[117]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[118]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[119]" -type "float2" -0.05535059 -0.11784319 ;
	setAttr ".uvtk[170]" -type "float2" 0 -3.2603741e-05 ;
	setAttr ".uvtk[171]" -type "float2" 0.00023399503 -2.9802322e-08 ;
	setAttr ".uvtk[172]" -type "float2" 0.00023399503 0 ;
	setAttr ".uvtk[173]" -type "float2" 0 -0.00010102987 ;
	setAttr ".uvtk[174]" -type "float2" 0.00025062473 0 ;
	setAttr ".uvtk[177]" -type "float2" 0.00025062473 0 ;
	setAttr ".uvtk[178]" -type "float2" 0.00025062473 0 ;
	setAttr ".uvtk[180]" -type "float2" 0.00024228008 0.00010104477 ;
	setAttr ".uvtk[181]" -type "float2" 0.00024228008 3.2603741e-05 ;
	setAttr ".uvtk[182]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[183]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[184]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[185]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[186]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[187]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[188]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[189]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[190]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[191]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[192]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[193]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[194]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[195]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[196]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[197]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[198]" -type "float2" -0.037495553 -0.17497928 ;
	setAttr ".uvtk[199]" -type "float2" -0.037495553 -0.17497928 ;
createNode polyMapCut -n "polyMapCut3";
	rename -uid "6031EA50-4C0F-80A6-30ED-748A541B580A";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[118:129]";
createNode polyTweakUV -n "polyTweakUV38";
	rename -uid "A01387D3-48C7-75BA-E54A-20AA578FE45A";
	setAttr ".uopa" yes;
	setAttr -s 20 ".uvtk";
	setAttr ".uvtk[182]" -type "float2" -0.026401874 -0.035302632 ;
	setAttr ".uvtk[183]" -type "float2" -0.0031423545 -0.0026008114 ;
	setAttr ".uvtk[184]" -type "float2" -0.024807036 0.012808424 ;
	setAttr ".uvtk[185]" -type "float2" -0.03279249 -0.00023343359 ;
	setAttr ".uvtk[186]" -type "float2" -0.021114009 -0.021691807 ;
	setAttr ".uvtk[187]" -type "float2" -0.04413984 0.011010115 ;
	setAttr ".uvtk[188]" -type "float2" -0.050671112 -0.023904214 ;
	setAttr ".uvtk[189]" -type "float2" -0.042778768 -0.036946218 ;
	setAttr ".uvtk[190]" -type "float2" 0.010970511 0.0087047862 ;
	setAttr ".uvtk[191]" -type "float2" -0.010765364 -0.0067603299 ;
	setAttr ".uvtk[192]" -type "float2" -0.0028070719 -0.019582657 ;
	setAttr ".uvtk[193]" -type "float2" 0.018857634 -0.004337131 ;
	setAttr ".uvtk[194]" -type "float2" -0.012478312 0.028580349 ;
	setAttr ".uvtk[195]" -type "float2" -0.034143049 0.012899606 ;
	setAttr ".uvtk[196]" -type "float2" 0.00099853054 -0.0045690187 ;
	setAttr ".uvtk[197]" -type "float2" -0.020666178 -0.019923357 ;
	setAttr ".uvtk[198]" -type "float2" -0.0053118728 -0.041588046 ;
	setAttr ".uvtk[199]" -type "float2" 0.016352842 -0.026233701 ;
	setAttr ".uvtk[260]" -type "float2" -0.0106942 -0.0065407446 ;
	setAttr ".uvtk[261]" -type "float2" 0.010899318 0.0089204004 ;
createNode polyPinUV -n "polyPinUV16";
	rename -uid "8CDEC581-4CA3-8518-824E-B59932817557";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[190:195]" "map[260:261]";
	setAttr -s 8 ".pn";
	setAttr ".pn[190]" 1;
	setAttr ".pn[191]" 1;
	setAttr ".pn[192]" 1;
	setAttr ".pn[193]" 1;
	setAttr ".pn[194]" 1;
	setAttr ".pn[195]" 1;
	setAttr ".pn[260]" 1;
	setAttr ".pn[261]" 1;
createNode polyPinUV -n "polyPinUV17";
	rename -uid "E6628409-4F5C-2CE2-99C2-558163E6C318";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV18";
	rename -uid "FACA4B65-4A3D-C8E1-5B0E-3BB2E02A445B";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "map[182:185]" "map[190:195]" "map[260:261]";
	setAttr -s 12 ".pn";
	setAttr ".pn[182]" 1;
	setAttr ".pn[183]" 1;
	setAttr ".pn[184]" 1;
	setAttr ".pn[185]" 1;
	setAttr ".pn[190]" 1;
	setAttr ".pn[191]" 1;
	setAttr ".pn[192]" 1;
	setAttr ".pn[193]" 1;
	setAttr ".pn[194]" 1;
	setAttr ".pn[195]" 1;
	setAttr ".pn[260]" 1;
	setAttr ".pn[261]" 1;
createNode polyPinUV -n "polyPinUV19";
	rename -uid "7422721E-43F1-2075-EF95-698B356F0E70";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew18";
	rename -uid "2366D3DE-4EFB-E5C4-AE6A-199F93E80271";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[119:120]" "e[124:125]";
createNode polyTweak -n "polyTweak23";
	rename -uid "439F10ED-4DCA-FA41-FF74-169E7D575110";
	setAttr ".uopa" yes;
	setAttr -s 2 ".tk[78:79]" -type "float3"  0 0 -0.037270866 0 0 -0.037270866;
createNode polyTweakUV -n "polyTweakUV39";
	rename -uid "499E3FA4-48F0-40AD-3211-7D9AE3B34E6C";
	setAttr ".uopa" yes;
	setAttr -s 48 ".uvtk";
	setAttr ".uvtk[182]" -type "float2" 0 0.0076452577 ;
	setAttr ".uvtk[183]" -type "float2" -4.0233135e-05 0.0076452876 ;
	setAttr ".uvtk[184]" -type "float2" -7.9274178e-06 0.0076234424 ;
	setAttr ".uvtk[185]" -type "float2" 2.1159649e-06 0.0076654041 ;
	setAttr ".uvtk[186]" -type "float2" 4.0203333e-05 0.0078614159 ;
	setAttr ".uvtk[187]" -type "float2" 0 0.0078614159 ;
	setAttr ".uvtk[188]" -type "float2" -2.0861626e-06 0.0077916188 ;
	setAttr ".uvtk[189]" -type "float2" 1.8894672e-05 0.0078584654 ;
	setAttr ".uvtk[190]" -type "float2" 0 0.0077611608 ;
	setAttr ".uvtk[191]" -type "float2" 2.9802322e-08 0.0077082003 ;
	setAttr ".uvtk[192]" -type "float2" 0 0.0075823152 ;
	setAttr ".uvtk[193]" -type "float2" 0 0.0079616709 ;
	setAttr ".uvtk[218]" -type "float2" -0.2962268 -0.3117294 ;
	setAttr ".uvtk[219]" -type "float2" -0.2572071 -0.26408163 ;
	setAttr ".uvtk[220]" -type "float2" -0.28104883 -0.2497834 ;
	setAttr ".uvtk[221]" -type "float2" -0.30614954 -0.27962697 ;
	setAttr ".uvtk[222]" -type "float2" -0.15111929 -0.28573114 ;
	setAttr ".uvtk[223]" -type "float2" -0.19810474 -0.23808336 ;
	setAttr ".uvtk[224]" -type "float2" -0.20523345 -0.27828768 ;
	setAttr ".uvtk[225]" -type "float2" -0.17496097 -0.30813131 ;
	setAttr ".uvtk[226]" -type "float2" -0.29059434 -0.3118591 ;
	setAttr ".uvtk[227]" -type "float2" -0.25157106 -0.26421133 ;
	setAttr ".uvtk[228]" -type "float2" -0.2754128 -0.24984114 ;
	setAttr ".uvtk[229]" -type "float2" -0.30051577 -0.27968469 ;
	setAttr ".uvtk[230]" -type "float2" -0.15111929 -0.28573111 ;
	setAttr ".uvtk[231]" -type "float2" -0.19810462 -0.23808339 ;
	setAttr ".uvtk[232]" -type "float2" -0.2052334 -0.27828768 ;
	setAttr ".uvtk[233]" -type "float2" -0.17496097 -0.30813125 ;
	setAttr ".uvtk[234]" -type "float2" -0.18644047 -0.27382812 ;
	setAttr ".uvtk[235]" -type "float2" -0.18644053 -0.27382812 ;
	setAttr ".uvtk[236]" -type "float2" -0.18635553 -0.27382812 ;
	setAttr ".uvtk[237]" -type "float2" -0.18635553 -0.27382812 ;
	setAttr ".uvtk[238]" -type "float2" -0.19503313 -0.27382812 ;
	setAttr ".uvtk[239]" -type "float2" -0.19503313 -0.27382812 ;
	setAttr ".uvtk[240]" -type "float2" -0.18615913 -0.27382812 ;
	setAttr ".uvtk[241]" -type "float2" -0.18615913 -0.27382812 ;
	setAttr ".uvtk[242]" -type "float2" -0.18624407 -0.27382812 ;
	setAttr ".uvtk[243]" -type "float2" -0.18624413 -0.27382812 ;
	setAttr ".uvtk[244]" -type "float2" -0.17756653 -0.27382812 ;
	setAttr ".uvtk[245]" -type "float2" -0.17756647 -0.27382812 ;
	setAttr ".uvtk[246]" -type "float2" -0.182096 -0.2759155 ;
	setAttr ".uvtk[247]" -type "float2" -0.18162966 -0.27327073 ;
	setAttr ".uvtk[248]" -type "float2" -0.19030732 -0.27174079 ;
	setAttr ".uvtk[249]" -type "float2" -0.19077355 -0.27438557 ;
	setAttr ".uvtk[250]" -type "float2" -0.19077355 -0.27327052 ;
	setAttr ".uvtk[251]" -type "float2" -0.19030738 -0.27591529 ;
	setAttr ".uvtk[252]" -type "float2" -0.18162972 -0.27438572 ;
	setAttr ".uvtk[253]" -type "float2" -0.18209589 -0.27174094 ;
createNode polyPinUV -n "polyPinUV20";
	rename -uid "37E0D5E7-4ED8-20F0-CE1D-E69D2573CE01";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[222:225]" "map[230:253]";
	setAttr -s 28 ".pn";
	setAttr ".pn[222]" 1;
	setAttr ".pn[223]" 1;
	setAttr ".pn[224]" 1;
	setAttr ".pn[225]" 1;
	setAttr ".pn[230]" 1;
	setAttr ".pn[231]" 1;
	setAttr ".pn[232]" 1;
	setAttr ".pn[233]" 1;
	setAttr ".pn[234]" 1;
	setAttr ".pn[235]" 1;
	setAttr ".pn[236]" 1;
	setAttr ".pn[237]" 1;
	setAttr ".pn[238]" 1;
	setAttr ".pn[239]" 1;
	setAttr ".pn[240]" 1;
	setAttr ".pn[241]" 1;
	setAttr ".pn[242]" 1;
	setAttr ".pn[243]" 1;
	setAttr ".pn[244]" 1;
	setAttr ".pn[245]" 1;
	setAttr ".pn[246]" 1;
	setAttr ".pn[247]" 1;
	setAttr ".pn[248]" 1;
	setAttr ".pn[249]" 1;
	setAttr ".pn[250]" 1;
	setAttr ".pn[251]" 1;
	setAttr ".pn[252]" 1;
	setAttr ".pn[253]" 1;
createNode polyPinUV -n "polyPinUV21";
	rename -uid "E6765207-4A48-2700-B3BB-0B8A0B3D51C7";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[234:253]";
	setAttr -s 20 ".pn[234:253]"  0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0;
	setAttr ".op" 1;
createNode polyMapCut -n "polyMapCut4";
	rename -uid "67BBD011-4628-9B61-75BD-F1A01084E03E";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[197:206]" "e[209:218]";
	setAttr ".pin" yes;
createNode polyTweak -n "polyTweak24";
	rename -uid "9CB076DE-4CFC-3209-5E1C-D2BA9DE8E99B";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk";
	setAttr ".tk[132]" -type "float3" 0.05241707 -0.099284008 0 ;
	setAttr ".tk[133]" -type "float3" 0.05241707 -0.099284008 0 ;
	setAttr ".tk[134]" -type "float3" 0.05241707 -0.099284008 0 ;
	setAttr ".tk[135]" -type "float3" 0.05241707 -0.099284008 0 ;
	setAttr ".tk[140]" -type "float3" -0.042926852 -0.098400451 0 ;
	setAttr ".tk[141]" -type "float3" -0.042926852 -0.098400451 0 ;
	setAttr ".tk[142]" -type "float3" -0.042926852 -0.098400451 0 ;
	setAttr ".tk[143]" -type "float3" -0.042926852 -0.098400451 0 ;
createNode polyTweakUV -n "polyTweakUV40";
	rename -uid "76E48045-41C2-A5E1-A5B6-AFB8D44A4068";
	setAttr ".uopa" yes;
	setAttr -s 36 ".uvtk";
	setAttr ".uvtk[218]" -type "float2" -0.015473559 0 ;
	setAttr ".uvtk[219]" -type "float2" -0.015473559 0 ;
	setAttr ".uvtk[220]" -type "float2" -0.015473559 0 ;
	setAttr ".uvtk[221]" -type "float2" -0.015473559 0 ;
	setAttr ".uvtk[222]" -type "float2" -0.14632535 0.01289972 ;
	setAttr ".uvtk[223]" -type "float2" -0.13782293 0.01289972 ;
	setAttr ".uvtk[224]" -type "float2" -0.14080501 0.02127772 ;
	setAttr ".uvtk[225]" -type "float2" -0.14632535 0.02127772 ;
	setAttr ".uvtk[230]" -type "float2" -0.1191572 -0.035053756 ;
	setAttr ".uvtk[231]" -type "float2" -0.11100394 -0.035053756 ;
	setAttr ".uvtk[232]" -type "float2" -0.11386354 -0.026573082 ;
	setAttr ".uvtk[233]" -type "float2" -0.1191572 -0.026573082 ;
	setAttr ".uvtk[234]" -type "float2" -0.16254346 -0.031323191 ;
	setAttr ".uvtk[235]" -type "float2" -0.16981012 -0.039060976 ;
	setAttr ".uvtk[236]" -type "float2" -0.14474921 -0.068904549 ;
	setAttr ".uvtk[237]" -type "float2" -0.13748275 -0.061166815 ;
	setAttr ".uvtk[238]" -type "float2" -0.17638183 -0.014843852 ;
	setAttr ".uvtk[239]" -type "float2" -0.18364836 -0.022581547 ;
	setAttr ".uvtk[240]" -type "float2" -0.08476536 -0.031220984 ;
	setAttr ".uvtk[241]" -type "float2" -0.092070661 -0.039090406 ;
	setAttr ".uvtk[242]" -type "float2" -0.066931531 -0.069024414 ;
	setAttr ".uvtk[243]" -type "float2" -0.059665002 -0.061064553 ;
	setAttr ".uvtk[244]" -type "float2" -0.098474734 -0.014760622 ;
	setAttr ".uvtk[245]" -type "float2" -0.10574112 -0.022611031 ;
	setAttr ".uvtk[246]" -type "float2" -0.1087271 -0.015452814 ;
	setAttr ".uvtk[247]" -type "float2" -0.11645982 -0.023185564 ;
	setAttr ".uvtk[248]" -type "float2" -0.10078073 -0.04855717 ;
	setAttr ".uvtk[249]" -type "float2" -0.093047984 -0.040824421 ;
	setAttr ".uvtk[250]" -type "float2" -0.02206427 -0.015391264 ;
	setAttr ".uvtk[251]" -type "float2" -0.029797021 -0.023162875 ;
	setAttr ".uvtk[252]" -type "float2" -0.014172389 -0.048534129 ;
	setAttr ".uvtk[253]" -type "float2" -0.0064395834 -0.040762518 ;
	setAttr ".uvtk[254]" -type "float2" -0.092031881 -0.039180856 ;
	setAttr ".uvtk[255]" -type "float2" -0.08480414 -0.03123999 ;
	setAttr ".uvtk[256]" -type "float2" -0.16981012 -0.039060976 ;
	setAttr ".uvtk[257]" -type "float2" -0.16254346 -0.031323191 ;
createNode polyPinUV -n "polyPinUV22";
	rename -uid "D93B34AD-4DCF-AAEE-979A-9FB17375C2F7";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV23";
	rename -uid "5AD606A8-4B8B-79ED-7B26-3D82884F34BE";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "map[218:221]" "map[226:229]" "map[234:257]";
	setAttr -s 32 ".pn";
	setAttr ".pn[218]" 1;
	setAttr ".pn[219]" 1;
	setAttr ".pn[220]" 1;
	setAttr ".pn[221]" 1;
	setAttr ".pn[226]" 1;
	setAttr ".pn[227]" 1;
	setAttr ".pn[228]" 1;
	setAttr ".pn[229]" 1;
	setAttr ".pn[234]" 1;
	setAttr ".pn[235]" 1;
	setAttr ".pn[236]" 1;
	setAttr ".pn[237]" 1;
	setAttr ".pn[238]" 1;
	setAttr ".pn[239]" 1;
	setAttr ".pn[240]" 1;
	setAttr ".pn[241]" 1;
	setAttr ".pn[242]" 1;
	setAttr ".pn[243]" 1;
	setAttr ".pn[244]" 1;
	setAttr ".pn[245]" 1;
	setAttr ".pn[246]" 1;
	setAttr ".pn[247]" 1;
	setAttr ".pn[248]" 1;
	setAttr ".pn[249]" 1;
	setAttr ".pn[250]" 1;
	setAttr ".pn[251]" 1;
	setAttr ".pn[252]" 1;
	setAttr ".pn[253]" 1;
	setAttr ".pn[254]" 1;
	setAttr ".pn[255]" 1;
	setAttr ".pn[256]" 1;
	setAttr ".pn[257]" 1;
createNode polyPinUV -n "polyPinUV24";
	rename -uid "9FB4ADA4-482F-B222-FABD-F5A4F2134B3F";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew19";
	rename -uid "987D3EEA-4A00-D97B-E100-F0BC3007D41E";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 4 "e[198:199]" "e[203:204]" "e[210:211]" "e[215:216]";
createNode polyTweakUV -n "polyTweakUV41";
	rename -uid "BA895FBC-4CDC-D75E-C78A-D59371EDB456";
	setAttr ".uopa" yes;
	setAttr -s 44 ".uvtk";
	setAttr ".uvtk[96]" -type "float2" 0.0033648342 -0.098274194 ;
	setAttr ".uvtk[97]" -type "float2" 0.055557154 -0.098274194 ;
	setAttr ".uvtk[98]" -type "float2" 0.055557154 -0.094035231 ;
	setAttr ".uvtk[99]" -type "float2" 0.0033648342 -0.094035231 ;
	setAttr ".uvtk[100]" -type "float2" 0.19107139 -0.024673916 ;
	setAttr ".uvtk[101]" -type "float2" -0.13257584 -0.024674036 ;
	setAttr ".uvtk[102]" -type "float2" -0.13257584 -0.050808202 ;
	setAttr ".uvtk[103]" -type "float2" 0.19107139 -0.050808202 ;
	setAttr ".uvtk[104]" -type "float2" -0.13227466 0.046105154 ;
	setAttr ".uvtk[105]" -type "float2" -0.13227466 -0.044964992 ;
	setAttr ".uvtk[106]" -type "float2" 0.19088544 -0.044964992 ;
	setAttr ".uvtk[107]" -type "float2" 0.19088545 0.046105154 ;
	setAttr ".uvtk[108]" -type "float2" 0.05520102 0.025005171 ;
	setAttr ".uvtk[109]" -type "float2" 0.05520102 0.043341823 ;
	setAttr ".uvtk[110]" -type "float2" 0.0038410993 0.043341823 ;
	setAttr ".uvtk[111]" -type "float2" 0.0038410993 0.025005171 ;
	setAttr ".uvtk[112]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[113]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[114]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[115]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[116]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[117]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[118]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[119]" -type "float2" 0.092663392 -0.009567026 ;
	setAttr ".uvtk[219]" -type "float2" -6.9499016e-05 0 ;
	setAttr ".uvtk[220]" -type "float2" -0.00012367964 1.347065e-05 ;
	setAttr ".uvtk[221]" -type "float2" 2.592802e-05 -2.0265579e-06 ;
	setAttr ".uvtk[222]" -type "float2" 0.00012367964 0 ;
	setAttr ".uvtk[224]" -type "float2" -2.5987625e-05 -4.6879053e-05 ;
	setAttr ".uvtk[225]" -type "float2" -5.9306622e-05 -2.9683113e-05 ;
	setAttr ".uvtk[227]" -type "float2" 8.5847365e-05 0 ;
	setAttr ".uvtk[228]" -type "float2" 0.00010277508 3.1381845e-05 ;
	setAttr ".uvtk[229]" -type "float2" 7.2717667e-06 3.3289194e-05 ;
	setAttr ".uvtk[230]" -type "float2" 0.00016685008 0 ;
	setAttr ".uvtk[232]" -type "float2" -7.212162e-06 -7.4237585e-05 ;
	setAttr ".uvtk[233]" -type "float2" 0.00012977599 -4.9889088e-05 ;
	setAttr ".uvtk[234]" -type "float2" -5.9604645e-08 -4.6789646e-06 ;
	setAttr ".uvtk[235]" -type "float2" 0 -2.3305416e-05 ;
	setAttr ".uvtk[236]" -type "float2" 5.9604645e-08 1.2218952e-06 ;
	setAttr ".uvtk[237]" -type "float2" -5.9604645e-08 -2.7507544e-05 ;
	setAttr ".uvtk[238]" -type "float2" 0 2.3305416e-05 ;
	setAttr ".uvtk[239]" -type "float2" 0 4.6879053e-05 ;
	setAttr ".uvtk[240]" -type "float2" 0.00012637852 -3.3259392e-05 ;
	setAttr ".uvtk[241]" -type "float2" 0.00012631892 7.4267387e-05 ;
createNode polyMapSew -n "polyMapSew20";
	rename -uid "0EBE9B00-4222-B4DD-931A-D1880903A3A6";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[64:65]" "e[68]";
createNode polyTweakUV -n "polyTweakUV42";
	rename -uid "0CE2D550-4037-3D13-D79D-D98857336A9E";
	setAttr ".uopa" yes;
	setAttr -s 18 ".uvtk[96:113]" -type "float2" 0.0001821965 2.9802322e-08
		 -0.00017806888 -2.9802322e-08 -3.0994415e-06 0.00026124559 0.00016283989 0.00026124559
		 8.1986189e-05 -2.9802322e-08 7.5280666e-05 2.9802322e-08 0.00029405951 0 7.8886747e-05
		 0 0.00017806888 -5.9604645e-08 -0.00029407442 0 -0.1886216 -0.026447609 -0.22477295
		 0.028435705 -0.24033038 0.01741614 -0.20417903 -0.03746707 -0.0010481135 -0.053923815
		 -0.037199441 -0.10829047 -0.022140088 -0.11931 0.014011264 -0.064943343;
createNode polyMapSew -n "polyMapSew21";
	rename -uid "073B27C3-4C8D-ED77-695D-ADBF9F6F6EDD";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[58]";
createNode polyTweakUV -n "polyTweakUV43";
	rename -uid "17299208-4365-FD8D-F518-B68570AE5E22";
	setAttr ".uopa" yes;
	setAttr -s 10 ".uvtk";
	setAttr ".uvtk[96]" -type "float2" 5.4091215e-06 0 ;
	setAttr ".uvtk[99]" -type "float2" 5.4091215e-06 0 ;
	setAttr ".uvtk[100]" -type "float2" 0 5.8919191e-05 ;
	setAttr ".uvtk[101]" -type "float2" -5.4091215e-06 5.9604645e-08 ;
	setAttr ".uvtk[102]" -type "float2" 5.4091215e-06 0 ;
	setAttr ".uvtk[104]" -type "float2" 0 7.9631805e-05 ;
	setAttr ".uvtk[105]" -type "float2" -5.4091215e-06 0 ;
	setAttr ".uvtk[106]" -type "float2" 0 -7.9572201e-05 ;
	setAttr ".uvtk[107]" -type "float2" 0 -5.8948994e-05 ;
	setAttr ".uvtk[108]" -type "float2" 2.9802322e-08 0 ;
createNode polyMapSew -n "polyMapSew22";
	rename -uid "CA685387-4B77-8EE8-4B02-EAA0F93FDF86";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[60]";
createNode polyTweakUV -n "polyTweakUV44";
	rename -uid "1021D7E6-432D-E9D3-7698-3CBD41894ECE";
	setAttr ".uopa" yes;
	setAttr -s 16 ".uvtk";
	setAttr ".uvtk[40]" -type "float2" -0.0028647371 0 ;
	setAttr ".uvtk[41]" -type "float2" -0.0028647371 0 ;
	setAttr ".uvtk[96]" -type "float2" 0 7.3183386e-05 ;
	setAttr ".uvtk[97]" -type "float2" -0.00027430026 7.3183386e-05 ;
	setAttr ".uvtk[98]" -type "float2" -0.00031670896 -0.00034591602 ;
	setAttr ".uvtk[99]" -type "float2" 0 -0.00020536827 ;
	setAttr ".uvtk[100]" -type "float2" -0.00027430026 0 ;
	setAttr ".uvtk[102]" -type "float2" 0 -3.5017729e-05 ;
	setAttr ".uvtk[103]" -type "float2" -0.00031670896 0 ;
	setAttr ".uvtk[104]" -type "float2" -0.00027430026 0 ;
	setAttr ".uvtk[108]" -type "float2" -2.3644563e-05 -0.00048646377 ;
	setAttr ".uvtk[109]" -type "float2" -2.3644563e-05 3.5017729e-05 ;
	setAttr ".uvtk[211]" -type "float2" 0.0015267055 0 ;
	setAttr ".uvtk[214]" -type "float2" 0.0015267055 0 ;
	setAttr ".uvtk[219]" -type "float2" 0.0015267055 0 ;
	setAttr ".uvtk[222]" -type "float2" 0.0015267055 0 ;
createNode polyPinUV -n "polyPinUV25";
	rename -uid "81094FDA-4AE0-5341-72C8-90B2B6480515";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[96:101]" "map[104:105]";
	setAttr -s 8 ".pn";
	setAttr ".pn[96]" 1;
	setAttr ".pn[97]" 1;
	setAttr ".pn[98]" 1;
	setAttr ".pn[99]" 1;
	setAttr ".pn[100]" 1;
	setAttr ".pn[101]" 1;
	setAttr ".pn[104]" 1;
	setAttr ".pn[105]" 1;
createNode polyPinUV -n "polyPinUV26";
	rename -uid "76B17CB6-4024-C27D-DF22-BB8408B9ADD3";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV27";
	rename -uid "601F11E1-414F-5DCA-F993-5DB213471848";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[100:101]" "map[104:107]";
	setAttr -s 6 ".pn";
	setAttr ".pn[100]" 1;
	setAttr ".pn[101]" 1;
	setAttr ".pn[104]" 1;
	setAttr ".pn[105]" 1;
	setAttr ".pn[106]" 1;
	setAttr ".pn[107]" 1;
createNode polyPinUV -n "polyPinUV28";
	rename -uid "83F99B29-4420-FD05-4FCE-A7A8C3C9DEA2";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode file -n "file2";
	rename -uid "B830A6E3-4368-53F2-40A1-5CAF026B5858";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/dc15aModel//dc15aLayout.png";
	setAttr ".ft" 0;
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "place2dTexture2";
	rename -uid "B80F7BE8-4000-A47F-03CC-5BBE09C188F3";
createNode polyFlipUV -n "polyFlipUV5";
	rename -uid "6BA3FD4D-4B66-F568-D093-5BA96F6215DC";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[44:49]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".up" yes;
	setAttr ".pu" 0.59371271729999997;
	setAttr ".pv" 0.12509365380000001;
createNode polyTweakUV -n "polyTweakUV45";
	rename -uid "006861F8-437F-FC5C-D51F-9A986E6A1976";
	setAttr ".uopa" yes;
	setAttr -s 15 ".uvtk";
	setAttr ".uvtk[132]" -type "float2" -0.007531628 0 ;
	setAttr ".uvtk[133]" -type "float2" -0.0075315982 0 ;
	setAttr ".uvtk[134]" -type "float2" -0.0075315982 0 ;
	setAttr ".uvtk[135]" -type "float2" -0.007531628 0 ;
	setAttr ".uvtk[136]" -type "float2" -0.0075315982 0 ;
	setAttr ".uvtk[137]" -type "float2" -0.007531628 0 ;
	setAttr ".uvtk[138]" -type "float2" -0.007531628 0 ;
	setAttr ".uvtk[139]" -type "float2" -0.0075315982 0 ;
	setAttr ".uvtk[140]" -type "float2" -0.0075315982 0 ;
	setAttr ".uvtk[141]" -type "float2" -0.0075315982 0 ;
	setAttr ".uvtk[142]" -type "float2" -0.0075315982 0 ;
	setAttr ".uvtk[143]" -type "float2" -0.007531628 0 ;
	setAttr ".uvtk[144]" -type "float2" -0.007531628 0 ;
	setAttr ".uvtk[145]" -type "float2" -0.007531628 0 ;
createNode nodeGraphEditorInfo -n "hyperShadePrimaryNodeEditorSavedTabsInfo";
	rename -uid "BBD40B2D-4C8C-451F-2A49-8D93BE4F89BD";
	setAttr ".tgi[0].tn" -type "string" "Untitled_1";
	setAttr ".tgi[0].vl" -type "double2" -122.02380467501918 -168.45237425868504 ;
	setAttr ".tgi[0].vh" -type "double2" 126.78570924770285 164.88094582917228 ;
	setAttr -s 9 ".tgi[0].ni";
	setAttr ".tgi[0].ni[0].x" -141.42857360839844;
	setAttr ".tgi[0].ni[0].y" 351.42855834960938;
	setAttr ".tgi[0].ni[0].nvs" 3042;
	setAttr ".tgi[0].ni[1].x" -245.71427917480469;
	setAttr ".tgi[0].ni[1].y" 142.85714721679688;
	setAttr ".tgi[0].ni[1].nvs" 1923;
	setAttr ".tgi[0].ni[2].x" 368.57144165039063;
	setAttr ".tgi[0].ni[2].y" 118.57142639160156;
	setAttr ".tgi[0].ni[2].nvs" 1923;
	setAttr ".tgi[0].ni[3].x" 368.57144165039063;
	setAttr ".tgi[0].ni[3].y" 120;
	setAttr ".tgi[0].ni[3].nvs" 1923;
	setAttr ".tgi[0].ni[4].x" -245.71427917480469;
	setAttr ".tgi[0].ni[4].y" 141.42857360839844;
	setAttr ".tgi[0].ni[4].nvs" 1923;
	setAttr ".tgi[0].ni[5].x" 61.428569793701172;
	setAttr ".tgi[0].ni[5].y" 141.42857360839844;
	setAttr ".tgi[0].ni[5].nvs" 1923;
	setAttr ".tgi[0].ni[6].x" 61.428569793701172;
	setAttr ".tgi[0].ni[6].y" 142.85714721679688;
	setAttr ".tgi[0].ni[6].nvs" 1923;
	setAttr ".tgi[0].ni[7].x" -552.85711669921875;
	setAttr ".tgi[0].ni[7].y" 118.57142639160156;
	setAttr ".tgi[0].ni[7].nvs" 1923;
	setAttr ".tgi[0].ni[8].x" -552.85711669921875;
	setAttr ".tgi[0].ni[8].y" 120;
	setAttr ".tgi[0].ni[8].nvs" 1923;
select -ne :time1;
	setAttr ".o" 1;
	setAttr ".unw" 1;
select -ne :hardwareRenderingGlobals;
	setAttr ".otfna" -type "stringArray" 22 "NURBS Curves" "NURBS Surfaces" "Polygons" "Subdiv Surface" "Particles" "Particle Instance" "Fluids" "Strokes" "Image Planes" "UI" "Lights" "Cameras" "Locators" "Joints" "IK Handles" "Deformers" "Motion Trails" "Components" "Hair Systems" "Follicles" "Misc. UI" "Ornaments"  ;
	setAttr ".otfva" -type "Int32Array" 22 0 1 1 1 1 1
		 1 1 1 0 0 0 0 0 0 0 0 0
		 0 0 0 0 ;
	setAttr ".fprt" yes;
select -ne :renderPartition;
	setAttr -s 4 ".st";
select -ne :renderGlobalsList1;
select -ne :defaultShaderList1;
	setAttr -s 7 ".s";
select -ne :postProcessList1;
	setAttr -s 2 ".p";
select -ne :defaultRenderUtilityList1;
	setAttr -s 2 ".u";
select -ne :defaultRenderingList1;
select -ne :defaultTextureList1;
	setAttr -s 2 ".tx";
select -ne :initialShadingGroup;
	setAttr -s 32 ".dsm";
	setAttr ".ro" yes;
	setAttr -s 32 ".gn";
select -ne :initialParticleSE;
	setAttr ".ro" yes;
select -ne :defaultRenderGlobals;
	addAttr -ci true -h true -sn "dss" -ln "defaultSurfaceShader" -dt "string";
	setAttr ".dss" -type "string" "lambert1";
select -ne :defaultResolution;
	setAttr ".pa" 1;
select -ne :hardwareRenderGlobals;
	setAttr ".ctrs" 256;
	setAttr ".btrs" 512;
connectAttr "layer1.di" "imagePlane1.do";
connectAttr ":defaultColorMgtGlobals.cme" "imagePlaneShape1.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "imagePlaneShape1.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "imagePlaneShape1.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "imagePlaneShape1.ws";
connectAttr ":sideShape.msg" "imagePlaneShape1.ltc";
connectAttr "polyTweakUV45.out" "group3Shape.i";
connectAttr "polyTweakUV45.uvtk[0]" "group3Shape.uvst[0].uvtw";
connectAttr "groupId33.id" "pCubeShape25.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape25.iog.og[0].gco";
connectAttr "groupId34.id" "pCubeShape25.ciog.cog[0].cgid";
connectAttr "groupId31.id" "pCubeShape22.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape22.iog.og[0].gco";
connectAttr "groupParts15.og" "pCubeShape22.i";
connectAttr "groupId32.id" "pCubeShape22.ciog.cog[0].cgid";
connectAttr "groupParts14.og" "group2Shape.i";
connectAttr "groupId29.id" "group2Shape.iog.og[3].gid";
connectAttr "lambert2SG.mwc" "group2Shape.iog.og[3].gco";
connectAttr "polyTweakUV34.uvtk[0]" "group2Shape.uvst[0].uvtw";
connectAttr "groupId30.id" "group2Shape.ciog.cog[0].cgid";
connectAttr "groupId9.id" "pCubeShape1.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape1.iog.og[1].gco";
connectAttr "groupParts4.og" "pCubeShape1.i";
connectAttr "groupId10.id" "pCubeShape1.ciog.cog[1].cgid";
connectAttr "groupId11.id" "pCubeShape2.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape2.iog.og[1].gco";
connectAttr "groupParts5.og" "pCubeShape2.i";
connectAttr "groupId12.id" "pCubeShape2.ciog.cog[1].cgid";
connectAttr "groupId13.id" "pCubeShape3.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape3.iog.og[1].gco";
connectAttr "groupParts6.og" "pCubeShape3.i";
connectAttr "groupId14.id" "pCubeShape3.ciog.cog[1].cgid";
connectAttr "groupId15.id" "pCubeShape4.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape4.iog.og[1].gco";
connectAttr "groupParts7.og" "pCubeShape4.i";
connectAttr "groupId16.id" "pCubeShape4.ciog.cog[1].cgid";
connectAttr "groupId17.id" "pCubeShape5.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape5.iog.og[1].gco";
connectAttr "groupParts8.og" "pCubeShape5.i";
connectAttr "groupId18.id" "pCubeShape5.ciog.cog[1].cgid";
connectAttr "groupId19.id" "pCubeShape6.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape6.iog.og[1].gco";
connectAttr "groupParts9.og" "pCubeShape6.i";
connectAttr "groupId20.id" "pCubeShape6.ciog.cog[1].cgid";
connectAttr "groupId21.id" "pCubeShape7.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape7.iog.og[1].gco";
connectAttr "groupParts10.og" "pCubeShape7.i";
connectAttr "groupId22.id" "pCubeShape7.ciog.cog[1].cgid";
connectAttr "groupId23.id" "pCubeShape8.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape8.iog.og[1].gco";
connectAttr "groupParts11.og" "pCubeShape8.i";
connectAttr "groupId24.id" "pCubeShape8.ciog.cog[1].cgid";
connectAttr "groupId25.id" "pCubeShape15.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape15.iog.og[1].gco";
connectAttr "groupParts12.og" "pCubeShape15.i";
connectAttr "groupId26.id" "pCubeShape15.ciog.cog[1].cgid";
connectAttr "groupId27.id" "pCubeShape16.iog.og[1].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape16.iog.og[1].gco";
connectAttr "groupParts13.og" "pCubeShape16.i";
connectAttr "groupId28.id" "pCubeShape16.ciog.cog[1].cgid";
connectAttr "groupId5.id" "pCubeShape17.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape17.iog.og[0].gco";
connectAttr "groupParts2.og" "pCubeShape17.i";
connectAttr "groupId6.id" "pCubeShape17.ciog.cog[0].cgid";
connectAttr "groupId3.id" "pCubeShape20.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape20.iog.og[0].gco";
connectAttr "groupId4.id" "pCubeShape20.ciog.cog[0].cgid";
connectAttr "groupId1.id" "pCubeShape21.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape21.iog.og[0].gco";
connectAttr "groupParts1.og" "pCubeShape21.i";
connectAttr "groupId2.id" "pCubeShape21.ciog.cog[0].cgid";
connectAttr "groupParts3.og" "pCube22Shape.i";
connectAttr "groupId7.id" "pCube22Shape.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCube22Shape.iog.og[0].gco";
connectAttr "groupId8.id" "pCube23Shape.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCube23Shape.iog.og[0].gco";
relationship "link" ":lightLinker1" ":initialShadingGroup.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" ":initialParticleSE.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" "lambert2SG.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" "lambert3SG.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" ":initialShadingGroup.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" ":initialParticleSE.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" "lambert2SG.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" "lambert3SG.message" ":defaultLightSet.message";
connectAttr "layerManager.dli[0]" "defaultLayer.id";
connectAttr "renderLayerManager.rlmi[0]" "defaultRenderLayer.rlid";
connectAttr "layerManager.dli[1]" "layer1.id";
connectAttr "polyTweak1.out" "polyExtrudeFace1.ip";
connectAttr "pCubeShape1.wm" "polyExtrudeFace1.mp";
connectAttr "polyCube1.out" "polyTweak1.ip";
connectAttr "polyExtrudeFace1.out" "polyTweak2.ip";
connectAttr "polyTweak2.out" "deleteComponent1.ig";
connectAttr "deleteComponent1.og" "polyExtrudeFace2.ip";
connectAttr "pCubeShape1.wm" "polyExtrudeFace2.mp";
connectAttr "polyTweak3.out" "polyExtrudeFace3.ip";
connectAttr "pCubeShape1.wm" "polyExtrudeFace3.mp";
connectAttr "polyExtrudeFace2.out" "polyTweak3.ip";
connectAttr "polyTweak4.out" "polyExtrudeFace4.ip";
connectAttr "pCubeShape1.wm" "polyExtrudeFace4.mp";
connectAttr "polyExtrudeFace3.out" "polyTweak4.ip";
connectAttr "polyExtrudeFace4.out" "polyTweak5.ip";
connectAttr "polyTweak5.out" "deleteComponent2.ig";
connectAttr "deleteComponent2.og" "deleteComponent3.ig";
connectAttr "deleteComponent3.og" "deleteComponent4.ig";
connectAttr "deleteComponent4.og" "deleteComponent5.ig";
connectAttr "polyCube2.out" "polyExtrudeFace5.ip";
connectAttr "pCubeShape2.wm" "polyExtrudeFace5.mp";
connectAttr "polyTweak6.out" "polyExtrudeFace6.ip";
connectAttr "pCubeShape2.wm" "polyExtrudeFace6.mp";
connectAttr "polyExtrudeFace5.out" "polyTweak6.ip";
connectAttr "polyTweak11.out" "polyExtrudeFace11.ip";
connectAttr "pCubeShape15.wm" "polyExtrudeFace11.mp";
connectAttr "polyCube11.out" "polyTweak11.ip";
connectAttr "polyTweak12.out" "polyExtrudeFace12.ip";
connectAttr "pCubeShape15.wm" "polyExtrudeFace12.mp";
connectAttr "polyExtrudeFace11.out" "polyTweak12.ip";
connectAttr "polyTweak13.out" "polyExtrudeFace13.ip";
connectAttr "pCubeShape15.wm" "polyExtrudeFace13.mp";
connectAttr "polyExtrudeFace12.out" "polyTweak13.ip";
connectAttr "polyTweak14.out" "polyExtrudeFace14.ip";
connectAttr "pCubeShape16.wm" "polyExtrudeFace14.mp";
connectAttr "polyCube12.out" "polyTweak14.ip";
connectAttr "polyExtrudeFace14.out" "polyTweak15.ip";
connectAttr "polyTweak15.out" "deleteComponent6.ig";
connectAttr "deleteComponent6.og" "deleteComponent7.ig";
connectAttr "deleteComponent7.og" "deleteComponent8.ig";
connectAttr "polyTweak16.out" "polyExtrudeFace15.ip";
connectAttr "pCubeShape21.wm" "polyExtrudeFace15.mp";
connectAttr "polyCube14.out" "polyTweak16.ip";
connectAttr "polyTweak17.out" "polyExtrudeFace16.ip";
connectAttr "pCubeShape21.wm" "polyExtrudeFace16.mp";
connectAttr "polyExtrudeFace15.out" "polyTweak17.ip";
connectAttr "pCubeShape21.o" "polyUnite1.ip[0]";
connectAttr "pCubeShape20.o" "polyUnite1.ip[1]";
connectAttr "pCubeShape17.o" "polyUnite1.ip[2]";
connectAttr "pCubeShape21.wm" "polyUnite1.im[0]";
connectAttr "pCubeShape20.wm" "polyUnite1.im[1]";
connectAttr "pCubeShape17.wm" "polyUnite1.im[2]";
connectAttr "polyExtrudeFace16.out" "groupParts1.ig";
connectAttr "groupId1.id" "groupParts1.gi";
connectAttr "polyCube13.out" "groupParts2.ig";
connectAttr "groupId5.id" "groupParts2.gi";
connectAttr "polyUnite1.out" "groupParts3.ig";
connectAttr "groupId7.id" "groupParts3.gi";
connectAttr "file1.oc" "pixelMeasure.c";
connectAttr "pixelMeasure.oc" "lambert2SG.ss";
connectAttr "group2Shape.iog.og[3]" "lambert2SG.dsm" -na;
connectAttr "group2Shape.ciog.cog[0]" "lambert2SG.dsm" -na;
connectAttr "groupId29.msg" "lambert2SG.gn" -na;
connectAttr "groupId30.msg" "lambert2SG.gn" -na;
connectAttr "lambert2SG.msg" "materialInfo1.sg";
connectAttr "pixelMeasure.msg" "materialInfo1.m";
connectAttr "file1.msg" "materialInfo1.t" -na;
connectAttr "file2.oc" "layout.c";
connectAttr "file2.ot" "layout.it";
connectAttr "layout.oc" "lambert3SG.ss";
connectAttr "group3Shape.iog" "lambert3SG.dsm" -na;
connectAttr "group5Shape.iog" "lambert3SG.dsm" -na;
connectAttr "lambert3SG.msg" "materialInfo2.sg";
connectAttr "layout.msg" "materialInfo2.m";
connectAttr "file2.msg" "materialInfo2.t" -na;
connectAttr ":defaultColorMgtGlobals.cme" "file1.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "file1.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "file1.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "file1.ws";
connectAttr "place2dTexture1.c" "file1.c";
connectAttr "place2dTexture1.tf" "file1.tf";
connectAttr "place2dTexture1.rf" "file1.rf";
connectAttr "place2dTexture1.mu" "file1.mu";
connectAttr "place2dTexture1.mv" "file1.mv";
connectAttr "place2dTexture1.s" "file1.s";
connectAttr "place2dTexture1.wu" "file1.wu";
connectAttr "place2dTexture1.wv" "file1.wv";
connectAttr "place2dTexture1.re" "file1.re";
connectAttr "place2dTexture1.of" "file1.of";
connectAttr "place2dTexture1.r" "file1.ro";
connectAttr "place2dTexture1.n" "file1.n";
connectAttr "place2dTexture1.vt1" "file1.vt1";
connectAttr "place2dTexture1.vt2" "file1.vt2";
connectAttr "place2dTexture1.vt3" "file1.vt3";
connectAttr "place2dTexture1.vc1" "file1.vc1";
connectAttr "place2dTexture1.o" "file1.uv";
connectAttr "place2dTexture1.ofs" "file1.fs";
connectAttr "pCubeShape1.o" "polyUnite2.ip[0]";
connectAttr "pCubeShape2.o" "polyUnite2.ip[1]";
connectAttr "pCubeShape3.o" "polyUnite2.ip[2]";
connectAttr "pCubeShape4.o" "polyUnite2.ip[3]";
connectAttr "pCubeShape5.o" "polyUnite2.ip[4]";
connectAttr "pCubeShape6.o" "polyUnite2.ip[5]";
connectAttr "pCubeShape7.o" "polyUnite2.ip[6]";
connectAttr "pCubeShape8.o" "polyUnite2.ip[7]";
connectAttr "pCubeShape15.o" "polyUnite2.ip[8]";
connectAttr "pCubeShape16.o" "polyUnite2.ip[9]";
connectAttr "pCube22Shape.o" "polyUnite2.ip[10]";
connectAttr "pCube23Shape.o" "polyUnite2.ip[11]";
connectAttr "pCubeShape1.wm" "polyUnite2.im[0]";
connectAttr "pCubeShape2.wm" "polyUnite2.im[1]";
connectAttr "pCubeShape3.wm" "polyUnite2.im[2]";
connectAttr "pCubeShape4.wm" "polyUnite2.im[3]";
connectAttr "pCubeShape5.wm" "polyUnite2.im[4]";
connectAttr "pCubeShape6.wm" "polyUnite2.im[5]";
connectAttr "pCubeShape7.wm" "polyUnite2.im[6]";
connectAttr "pCubeShape8.wm" "polyUnite2.im[7]";
connectAttr "pCubeShape15.wm" "polyUnite2.im[8]";
connectAttr "pCubeShape16.wm" "polyUnite2.im[9]";
connectAttr "pCube22Shape.wm" "polyUnite2.im[10]";
connectAttr "pCube23Shape.wm" "polyUnite2.im[11]";
connectAttr "deleteComponent5.og" "groupParts4.ig";
connectAttr "groupId9.id" "groupParts4.gi";
connectAttr "polyExtrudeFace6.out" "groupParts5.ig";
connectAttr "groupId11.id" "groupParts5.gi";
connectAttr "polyCube3.out" "groupParts6.ig";
connectAttr "groupId13.id" "groupParts6.gi";
connectAttr "polyCube4.out" "groupParts7.ig";
connectAttr "groupId15.id" "groupParts7.gi";
connectAttr "polyCube5.out" "groupParts8.ig";
connectAttr "groupId17.id" "groupParts8.gi";
connectAttr "polyCube6.out" "groupParts9.ig";
connectAttr "groupId19.id" "groupParts9.gi";
connectAttr "polyCube7.out" "groupParts10.ig";
connectAttr "groupId21.id" "groupParts10.gi";
connectAttr "polyCube8.out" "groupParts11.ig";
connectAttr "groupId23.id" "groupParts11.gi";
connectAttr "polyExtrudeFace13.out" "groupParts12.ig";
connectAttr "groupId25.id" "groupParts12.gi";
connectAttr "deleteComponent8.og" "groupParts13.ig";
connectAttr "groupId27.id" "groupParts13.gi";
connectAttr "polyUnite2.out" "polyMapDel1.ip";
connectAttr "polyMapDel1.out" "polyAutoProj1.ip";
connectAttr "group2Shape.wm" "polyAutoProj1.mp";
connectAttr "polyAutoProj1.out" "polyTweakUV1.ip";
connectAttr "polyTweakUV1.out" "polyAutoProj2.ip";
connectAttr "group2Shape.wm" "polyAutoProj2.mp";
connectAttr "polyAutoProj2.out" "polyTweakUV2.ip";
connectAttr "polyTweakUV2.out" "polyAutoProj3.ip";
connectAttr "group2Shape.wm" "polyAutoProj3.mp";
connectAttr "polyAutoProj3.out" "polyTweakUV3.ip";
connectAttr "polyTweak18.out" "polyAutoProj4.ip";
connectAttr "group2Shape.wm" "polyAutoProj4.mp";
connectAttr "polyTweakUV3.out" "polyTweak18.ip";
connectAttr "polyAutoProj4.out" "polyTweakUV4.ip";
connectAttr "polyTweakUV4.out" "polyAutoProj5.ip";
connectAttr "group2Shape.wm" "polyAutoProj5.mp";
connectAttr "polyAutoProj5.out" "polyTweakUV5.ip";
connectAttr "polyTweakUV5.out" "polyAutoProj6.ip";
connectAttr "group2Shape.wm" "polyAutoProj6.mp";
connectAttr "polyAutoProj6.out" "polyTweakUV6.ip";
connectAttr "polyTweak19.out" "polyAutoProj7.ip";
connectAttr "group2Shape.wm" "polyAutoProj7.mp";
connectAttr "polyTweakUV6.out" "polyTweak19.ip";
connectAttr "polyAutoProj7.out" "polyTweakUV7.ip";
connectAttr "polyTweakUV7.out" "polyAutoProj8.ip";
connectAttr "group2Shape.wm" "polyAutoProj8.mp";
connectAttr "polyAutoProj8.out" "polyTweakUV8.ip";
connectAttr "polyTweakUV8.out" "polyAutoProj9.ip";
connectAttr "group2Shape.wm" "polyAutoProj9.mp";
connectAttr "polyAutoProj9.out" "polyTweakUV9.ip";
connectAttr "polyTweakUV9.out" "polyAutoProj10.ip";
connectAttr "group2Shape.wm" "polyAutoProj10.mp";
connectAttr "polyAutoProj10.out" "polyTweakUV10.ip";
connectAttr "polyTweakUV10.out" "polyAutoProj11.ip";
connectAttr "group2Shape.wm" "polyAutoProj11.mp";
connectAttr "polyAutoProj11.out" "polyTweakUV11.ip";
connectAttr "polyTweakUV11.out" "polyAutoProj12.ip";
connectAttr "group2Shape.wm" "polyAutoProj12.mp";
connectAttr "polyAutoProj12.out" "polyTweakUV12.ip";
connectAttr "polyTweakUV12.out" "polyMapSew1.ip";
connectAttr "polyMapSew1.out" "polyTweakUV13.ip";
connectAttr "polyTweakUV13.out" "polyMapSew2.ip";
connectAttr "polyMapSew2.out" "polyTweakUV14.ip";
connectAttr "polyTweakUV14.out" "polyMapSew3.ip";
connectAttr "polyMapSew3.out" "polyTweakUV15.ip";
connectAttr "polyTweakUV15.out" "polyPinUV1.ip";
connectAttr "polyPinUV1.out" "polyPinUV2.ip";
connectAttr "polyPinUV2.out" "polyPinUV3.ip";
connectAttr "polyPinUV3.out" "polyPinUV4.ip";
connectAttr "polyPinUV4.out" "polyPinUV5.ip";
connectAttr "polyPinUV5.out" "polyPinUV6.ip";
connectAttr "polyPinUV6.out" "polyPinUV7.ip";
connectAttr "polyPinUV7.out" "polyMapSew4.ip";
connectAttr "polyMapSew4.out" "polyTweakUV16.ip";
connectAttr "polyTweakUV16.out" "polyMapDel2.ip";
connectAttr "polyTweak20.out" "polyAutoProj13.ip";
connectAttr "group2Shape.wm" "polyAutoProj13.mp";
connectAttr "polyMapDel2.out" "polyTweak20.ip";
connectAttr "polyAutoProj13.out" "polyTweakUV17.ip";
connectAttr "polyTweakUV17.out" "polyMapCut1.ip";
connectAttr "polyMapCut1.out" "polyTweakUV18.ip";
connectAttr "polyTweakUV18.out" "polyMapSew5.ip";
connectAttr "polyMapSew5.out" "polyTweakUV19.ip";
connectAttr "polyTweakUV19.out" "polyPinUV8.ip";
connectAttr "polyPinUV8.out" "polyPinUV9.ip";
connectAttr "polyTweak21.out" "polyMapSew6.ip";
connectAttr "polyPinUV9.out" "polyTweak21.ip";
connectAttr "polyMapSew6.out" "polyTweakUV20.ip";
connectAttr "polyTweakUV20.out" "polyFlipUV1.ip";
connectAttr "group2Shape.wm" "polyFlipUV1.mp";
connectAttr "polyFlipUV1.out" "polyTweakUV21.ip";
connectAttr "polyTweakUV21.out" "polyFlipUV2.ip";
connectAttr "group2Shape.wm" "polyFlipUV2.mp";
connectAttr "polyFlipUV2.out" "polyTweakUV22.ip";
connectAttr "polyTweakUV22.out" "polyMapSew7.ip";
connectAttr "polyMapSew7.out" "polyTweakUV23.ip";
connectAttr "polyTweakUV23.out" "polyMapSew8.ip";
connectAttr "polyMapSew8.out" "polyTweakUV24.ip";
connectAttr "polyTweakUV24.out" "polyMapSew9.ip";
connectAttr "polyMapSew9.out" "polyTweakUV25.ip";
connectAttr "polyTweakUV25.out" "polyFlipUV3.ip";
connectAttr "group2Shape.wm" "polyFlipUV3.mp";
connectAttr "polyFlipUV3.out" "polyTweakUV26.ip";
connectAttr "polyTweakUV26.out" "polyFlipUV4.ip";
connectAttr "group2Shape.wm" "polyFlipUV4.mp";
connectAttr "polyFlipUV4.out" "polyTweakUV27.ip";
connectAttr "polyTweakUV27.out" "polyMapCut2.ip";
connectAttr "polyMapCut2.out" "polyTweakUV28.ip";
connectAttr "polyTweakUV28.out" "polyMapSew10.ip";
connectAttr "polyMapSew10.out" "polyTweakUV29.ip";
connectAttr "polyTweakUV29.out" "polyMapSew11.ip";
connectAttr "polyMapSew11.out" "polyTweakUV30.ip";
connectAttr "polyTweakUV30.out" "polyMapSew12.ip";
connectAttr "polyMapSew12.out" "polyTweakUV31.ip";
connectAttr "polyTweakUV31.out" "polyMapSew13.ip";
connectAttr "polyMapSew13.out" "polyTweakUV32.ip";
connectAttr "polyTweakUV32.out" "polyPinUV10.ip";
connectAttr "polyPinUV10.out" "polyPinUV11.ip";
connectAttr "polyPinUV11.out" "polyPinUV12.ip";
connectAttr "polyPinUV12.out" "polyPinUV13.ip";
connectAttr "polyTweak22.out" "polyMapSew14.ip";
connectAttr "polyPinUV13.out" "polyTweak22.ip";
connectAttr "polyMapSew14.out" "polyTweakUV33.ip";
connectAttr "polyTweakUV33.out" "polyMapSew15.ip";
connectAttr "polyMapSew15.out" "polyTweakUV34.ip";
connectAttr "polyTweakUV34.out" "deleteComponent9.ig";
connectAttr "group2Shape.o" "polyUnite3.ip[0]";
connectAttr "pCubeShape22.o" "polyUnite3.ip[1]";
connectAttr "pCubeShape25.o" "polyUnite3.ip[2]";
connectAttr "group2Shape.wm" "polyUnite3.im[0]";
connectAttr "pCubeShape22.wm" "polyUnite3.im[1]";
connectAttr "pCubeShape25.wm" "polyUnite3.im[2]";
connectAttr "deleteComponent9.og" "groupParts14.ig";
connectAttr "groupId29.id" "groupParts14.gi";
connectAttr "polyCube15.out" "groupParts15.ig";
connectAttr "groupId31.id" "groupParts15.gi";
connectAttr "polyUnite3.out" "polyMapDel3.ip";
connectAttr "polyMapDel3.out" "polyMapDel4.ip";
connectAttr "polyMapDel4.out" "polyAutoProj14.ip";
connectAttr "group3Shape.wm" "polyAutoProj14.mp";
connectAttr "polyAutoProj14.out" "polyTweakUV35.ip";
connectAttr "polyTweakUV35.out" "polyMapSew16.ip";
connectAttr "polyMapSew16.out" "polyTweakUV36.ip";
connectAttr "polyTweakUV36.out" "polyPinUV14.ip";
connectAttr "polyPinUV14.out" "polyPinUV15.ip";
connectAttr "polyPinUV15.out" "polyMapSew17.ip";
connectAttr "polyMapSew17.out" "polyTweakUV37.ip";
connectAttr "polyTweakUV37.out" "polyMapCut3.ip";
connectAttr "polyMapCut3.out" "polyTweakUV38.ip";
connectAttr "polyTweakUV38.out" "polyPinUV16.ip";
connectAttr "polyPinUV16.out" "polyPinUV17.ip";
connectAttr "polyPinUV17.out" "polyPinUV18.ip";
connectAttr "polyPinUV18.out" "polyPinUV19.ip";
connectAttr "polyTweak23.out" "polyMapSew18.ip";
connectAttr "polyPinUV19.out" "polyTweak23.ip";
connectAttr "polyMapSew18.out" "polyTweakUV39.ip";
connectAttr "polyTweakUV39.out" "polyPinUV20.ip";
connectAttr "polyPinUV20.out" "polyPinUV21.ip";
connectAttr "polyTweak24.out" "polyMapCut4.ip";
connectAttr "polyPinUV21.out" "polyTweak24.ip";
connectAttr "polyMapCut4.out" "polyTweakUV40.ip";
connectAttr "polyTweakUV40.out" "polyPinUV22.ip";
connectAttr "polyPinUV22.out" "polyPinUV23.ip";
connectAttr "polyPinUV23.out" "polyPinUV24.ip";
connectAttr "polyPinUV24.out" "polyMapSew19.ip";
connectAttr "polyMapSew19.out" "polyTweakUV41.ip";
connectAttr "polyTweakUV41.out" "polyMapSew20.ip";
connectAttr "polyMapSew20.out" "polyTweakUV42.ip";
connectAttr "polyTweakUV42.out" "polyMapSew21.ip";
connectAttr "polyMapSew21.out" "polyTweakUV43.ip";
connectAttr "polyTweakUV43.out" "polyMapSew22.ip";
connectAttr "polyMapSew22.out" "polyTweakUV44.ip";
connectAttr "polyTweakUV44.out" "polyPinUV25.ip";
connectAttr "polyPinUV25.out" "polyPinUV26.ip";
connectAttr "polyPinUV26.out" "polyPinUV27.ip";
connectAttr "polyPinUV27.out" "polyPinUV28.ip";
connectAttr ":defaultColorMgtGlobals.cme" "file2.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "file2.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "file2.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "file2.ws";
connectAttr "place2dTexture2.c" "file2.c";
connectAttr "place2dTexture2.tf" "file2.tf";
connectAttr "place2dTexture2.rf" "file2.rf";
connectAttr "place2dTexture2.mu" "file2.mu";
connectAttr "place2dTexture2.mv" "file2.mv";
connectAttr "place2dTexture2.s" "file2.s";
connectAttr "place2dTexture2.wu" "file2.wu";
connectAttr "place2dTexture2.wv" "file2.wv";
connectAttr "place2dTexture2.re" "file2.re";
connectAttr "place2dTexture2.of" "file2.of";
connectAttr "place2dTexture2.r" "file2.ro";
connectAttr "place2dTexture2.n" "file2.n";
connectAttr "place2dTexture2.vt1" "file2.vt1";
connectAttr "place2dTexture2.vt2" "file2.vt2";
connectAttr "place2dTexture2.vt3" "file2.vt3";
connectAttr "place2dTexture2.vc1" "file2.vc1";
connectAttr "place2dTexture2.o" "file2.uv";
connectAttr "place2dTexture2.ofs" "file2.fs";
connectAttr "polyPinUV28.out" "polyFlipUV5.ip";
connectAttr "group3Shape.wm" "polyFlipUV5.mp";
connectAttr "polyFlipUV5.out" "polyTweakUV45.ip";
connectAttr "imagePlaneShape1.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[0].dn"
		;
connectAttr "file1.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[1].dn"
		;
connectAttr "lambert3SG.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[2].dn"
		;
connectAttr "lambert2SG.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[3].dn"
		;
connectAttr "file2.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[4].dn"
		;
connectAttr "layout.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[5].dn"
		;
connectAttr "pixelMeasure.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[6].dn"
		;
connectAttr "place2dTexture2.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[7].dn"
		;
connectAttr "place2dTexture1.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[8].dn"
		;
connectAttr "lambert2SG.pa" ":renderPartition.st" -na;
connectAttr "lambert3SG.pa" ":renderPartition.st" -na;
connectAttr "pixelMeasure.msg" ":defaultShaderList1.s" -na;
connectAttr "layout.msg" ":defaultShaderList1.s" -na;
connectAttr "place2dTexture1.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "place2dTexture2.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "defaultRenderLayer.msg" ":defaultRenderingList1.r" -na;
connectAttr "file1.msg" ":defaultTextureList1.tx" -na;
connectAttr "file2.msg" ":defaultTextureList1.tx" -na;
connectAttr "pCubeShape21.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape21.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape20.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape20.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape17.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape17.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCube22Shape.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCube23Shape.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape1.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape1.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape2.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape2.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape3.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape3.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape4.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape4.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape5.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape5.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape6.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape6.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape7.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape7.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape8.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape8.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape15.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape15.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape16.iog.og[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape16.ciog.cog[1]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape22.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape22.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape25.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape25.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "groupId1.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId2.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId3.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId4.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId5.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId6.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId7.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId8.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId9.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId10.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId11.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId12.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId13.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId14.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId15.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId16.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId17.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId18.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId19.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId20.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId21.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId22.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId23.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId24.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId25.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId26.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId27.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId28.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId31.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId32.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId33.msg" ":initialShadingGroup.gn" -na;
connectAttr "groupId34.msg" ":initialShadingGroup.gn" -na;
// End of v09.ma
