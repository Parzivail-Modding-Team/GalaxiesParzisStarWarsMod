//Maya ASCII 2020 scene
//Name: v07.ma
//Last modified: Sat, Jul 03, 2021 06:26:04 PM
//Codeset: 1252
requires maya "2020";
requires "stereoCamera" "10.0";
currentUnit -l centimeter -a degree -t film;
fileInfo "application" "maya";
fileInfo "product" "Maya 2020";
fileInfo "version" "2020";
fileInfo "cutIdentifier" "201911140446-42a737a01c";
fileInfo "osv" "Microsoft Windows 10 Technical Preview  (Build 19041)\n";
fileInfo "UUID" "35B4E616-49B6-75B8-E376-048A27058817";
createNode transform -s -n "persp";
	rename -uid "6FF4356D-4E81-E8E8-84D4-8BB63018006C";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 31.799737115997694 5.9714449525002991 -3.1543602805105628 ;
	setAttr ".r" -type "double3" -20.071220634593697 104.20000000101537 0 ;
createNode camera -s -n "perspShape" -p "persp";
	rename -uid "FE6C554C-4C59-CFFD-2FD1-E4843D6FD262";
	setAttr -k off ".v" no;
	setAttr ".fl" 34.999999999999993;
	setAttr ".coi" 29.833948265564068;
	setAttr ".imn" -type "string" "persp";
	setAttr ".den" -type "string" "persp_depth";
	setAttr ".man" -type "string" "persp_mask";
	setAttr ".hc" -type "string" "viewSet -p %camera";
createNode transform -s -n "top";
	rename -uid "81053054-4818-2AFD-C2FB-35B37CB09869";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 0 1000.1 0 ;
	setAttr ".r" -type "double3" -90 0 0 ;
createNode camera -s -n "topShape" -p "top";
	rename -uid "C0AF50FE-40AD-DACC-B113-72BC6A4C8F86";
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
	rename -uid "580ABE1B-4241-34EF-4FB7-FC8769BA2C95";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 0 0 1000.1 ;
createNode camera -s -n "frontShape" -p "front";
	rename -uid "6CC71171-4691-955B-B81A-05AF4589C688";
	setAttr -k off ".v" no;
	setAttr ".rnd" no;
	setAttr ".coi" 1000.1;
	setAttr ".ow" 30;
	setAttr ".imn" -type "string" "front";
	setAttr ".den" -type "string" "front_depth";
	setAttr ".man" -type "string" "front_mask";
	setAttr ".hc" -type "string" "viewSet -f %camera";
	setAttr ".o" yes;
createNode transform -s -n "side";
	rename -uid "E1447BB5-487D-9CE1-DF89-5B89EDB0CA53";
	setAttr ".t" -type "double3" 1000.1 -0.25265176022650049 4.568902619143679 ;
	setAttr ".r" -type "double3" 0 90 0 ;
createNode camera -s -n "sideShape" -p "side";
	rename -uid "BB40B813-4B83-7A7A-6CB8-80AB9669E8CE";
	setAttr -k off ".v";
	setAttr ".rnd" no;
	setAttr ".coi" 1000.1;
	setAttr ".ow" 4.4554963067759923;
	setAttr ".imn" -type "string" "side";
	setAttr ".den" -type "string" "side_depth";
	setAttr ".man" -type "string" "side_mask";
	setAttr ".hc" -type "string" "viewSet -s %camera";
	setAttr ".o" yes;
createNode transform -n "imagePlane1";
	rename -uid "540646E7-4296-7333-0DBF-72819F66811A";
	setAttr ".t" -type "double3" -11.442391895026994 0 0 ;
	setAttr ".r" -type "double3" 0 90 0 ;
createNode imagePlane -n "imagePlaneShape1" -p "imagePlane1";
	rename -uid "658911B7-4796-7695-CBD8-C484BE396E58";
	setAttr -k off ".v";
	setAttr ".fc" 102;
	setAttr ".imn" -type "string" "C:/Users/Liam - Moose/Downloads/jawaBlaster.jpg";
	setAttr ".cov" -type "short2" 1280 720 ;
	setAttr ".dlc" no;
	setAttr ".w" 12.8;
	setAttr ".h" 7.2;
	setAttr ".cs" -type "string" "sRGB";
createNode transform -n "group1";
	rename -uid "06A5516E-4BDA-BD1C-223B-A781D51BC9E4";
createNode transform -n "pCube11" -p "group1";
	rename -uid "35BB3748-4701-8C6C-90E6-AC9277CEDBCD";
	setAttr ".t" -type "double3" 0 -1.0473741318167444 5.3847194738194331 ;
	setAttr ".s" -type "double3" 1.2975516483388274 1.2975516483388274 1.2975516483388274 ;
createNode transform -n "transform1" -p "pCube11";
	rename -uid "983F0621-49FA-B89F-2F5F-2FA0C6E9C33C";
	setAttr ".v" no;
createNode mesh -n "pCubeShape11" -p "transform1";
	rename -uid "AF10CAEA-4EF8-A4DB-C930-469E3FC8A377";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.125 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[0:7]" -type "float3"  0.1853624 -0.064062916 -0.12171954 
		-0.1853624 -0.064062916 -0.12171954 0.1853624 -0.096094355 0.17937614 -0.1853624 
		-0.096094355 0.17937614 0.23995575 0 -0.14734472 -0.23995575 0 -0.14734472 0.23995575 
		0.13453212 -0.11531325 -0.23995575 0.13453212 -0.11531325;
createNode transform -n "pCube10" -p "group1";
	rename -uid "2D42C985-4EE6-CDEF-B948-60BC8BCA9673";
	setAttr ".t" -type "double3" 0.80332265077372855 0.25998833572176094 3.3134744327247834 ;
	setAttr ".s" -type "double3" 0.27078301448363512 0.27078301448363512 0.27078301448363512 ;
createNode transform -n "transform7" -p "pCube10";
	rename -uid "99416C3D-4185-0F06-7054-D4BEBEDDF111";
	setAttr ".v" no;
createNode mesh -n "pCubeShape10" -p "transform7";
	rename -uid "BB2E4341-462E-9E20-9C13-97B850398064";
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
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 -0.32547238 0 0 -0.32547238 
		0 0 -0.32547238 0 0 -0.32547238 0 0 -0.32547238 0 0 -0.32547238 0 0 -0.32547238 0 
		0 -0.32547238 0;
createNode transform -n "pCube9" -p "group1";
	rename -uid "B7088CC1-4F92-5C8C-F3CF-619073A96B88";
	setAttr ".t" -type "double3" 0.16109102198656444 0.329620880242317 3.3116809146151338 ;
	setAttr ".s" -type "double3" 0.26611048727800474 0.26611048727800474 0.12462041221387463 ;
createNode transform -n "transform6" -p "pCube9";
	rename -uid "F632481D-4F78-FD6D-4756-C29FE5B97817";
	setAttr ".v" no;
createNode mesh -n "pCubeShape9" -p "transform6";
	rename -uid "17AB21D4-4FC8-DEE2-3645-FB9512C4D31D";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.75 0.375 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt";
	setAttr ".pt[1]" -type "float3" 1.4371635 -0.33118725 0 ;
	setAttr ".pt[3]" -type "float3" 1.4371635 -0.79911464 0 ;
	setAttr ".pt[5]" -type "float3" 1.4371635 -0.79911464 0 ;
	setAttr ".pt[7]" -type "float3" 1.4371635 -0.33118725 0 ;
createNode transform -n "pPlane1" -p "group1";
	rename -uid "FC0D3969-4C94-C777-043D-DBA6D188F5E6";
	setAttr ".t" -type "double3" 0 -0.89864972370332064 3.280633937948561 ;
	setAttr ".r" -type "double3" -90 0 0 ;
	setAttr ".s" -type "double3" 0.40722195421547619 0.40722195421547619 0.40722195421547619 ;
createNode transform -n "transform2" -p "pPlane1";
	rename -uid "C5C4D5B2-46A5-7528-22BC-0FAC65117492";
	setAttr ".v" no;
createNode mesh -n "pPlaneShape1" -p "transform2";
	rename -uid "B2F0D6FB-46CD-D4D0-DC41-01B6FF2B3D6E";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 1 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 2 ".pt[8:9]" -type "float3"  0 0.77230746 0.37185177 0 
		0.77230746 0.37185177;
createNode transform -n "pCube8" -p "group1";
	rename -uid "CD7233FF-4295-672E-E0F8-62908F0B5642";
	setAttr ".t" -type "double3" 0 -0.73201512935594693 2.6538469048894946 ;
	setAttr ".s" -type "double3" 0.19450609138838876 0.11596842116996413 0.11596842116996413 ;
createNode transform -n "transform5" -p "pCube8";
	rename -uid "8FABE024-4EF6-89E0-8A79-818F50F5D071";
	setAttr ".v" no;
createNode mesh -n "pCubeShape8" -p "transform5";
	rename -uid "C89732B6-4CBA-FB0F-E341-03951A11B80C";
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
	setAttr -s 16 ".pt[0:15]" -type "float3"  0.27809542 0 0 -0.27809542 
		0 0 0.27809542 0 0 -0.27809542 0 0 0.27809542 0 0 -0.27809542 0 0 0.27809542 0 0 
		-0.27809542 0 0 0.27809542 0 0 -0.27809542 0 0 -0.27809542 0 0 0.27809542 0 0 0.27809542 
		-2.2272758 -0.96655333 -0.27809542 -2.2272758 -0.96655333 -0.27809542 -1.1556618 
		-0.42024079 0.27809542 -1.1556618 -0.42024079;
createNode transform -n "pCube7" -p "group1";
	rename -uid "10B1A848-4909-579F-B79B-FCA77D21F3F2";
	setAttr ".t" -type "double3" 0 -0.91730019073228619 1.3175766375972835 ;
	setAttr ".s" -type "double3" 0.71112535706528324 0.71112535706528324 0.71112535706528324 ;
createNode transform -n "transform9" -p "pCube7";
	rename -uid "8A16E222-4C49-CA77-6D70-16B9CEFEA2D8";
	setAttr ".v" no;
createNode mesh -n "pCubeShape7" -p "transform9";
	rename -uid "37C65F44-454C-6319-1694-E5B5ECF9F081";
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
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 -0.29316539 0.53942436 
		0 -0.29316539 0.53942436 0 -0.082086295 0.35179842 0 -0.082086295 0.35179842 0 -5.5511151e-17 
		-1.102302 0 -5.5511151e-17 -1.102302 0 0.24625891 -0.85604298 0 0.24625891 -0.85604298;
createNode transform -n "pCube6" -p "group1";
	rename -uid "662120F8-4D50-A34F-E4FC-27A797A18038";
	setAttr ".t" -type "double3" 0 0.34165629608017645 1.7503626219620598 ;
	setAttr ".s" -type "double3" 1.1588753798553892 0.76927556615516268 0.36787245141017783 ;
createNode transform -n "transform3" -p "pCube6";
	rename -uid "61C86AB2-49C5-70C9-75D9-22B78533CA7F";
	setAttr ".v" no;
createNode mesh -n "pCubeShape6" -p "transform3";
	rename -uid "AF36B211-4722-79CE-F329-EC95385C5CC4";
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
createNode transform -n "pCube3" -p "group1";
	rename -uid "2FB2CFA0-4668-7AEB-D13C-8CBA6CBAE5CB";
	setAttr ".t" -type "double3" 0 0.3176805966988725 3.3515302951731054 ;
	setAttr ".s" -type "double3" 0.12396773963820876 0.43141976813671251 0.51291016685811353 ;
createNode transform -n "transform10" -p "pCube3";
	rename -uid "9A443C32-4DA6-BDB8-2ECB-E9B5EA4E870A";
	setAttr ".v" no;
createNode mesh -n "pCubeShape3" -p "transform10";
	rename -uid "91BEC46F-4B95-3C0C-9B35-C1AEBF3E1230";
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
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 0.040637128 0 0 0.040637128 
		0 0 -0.03047785 -0.068361536 0 -0.03047785 -0.068361536 0 -0.03047785 -0.02563557 
		0 -0.03047785 -0.02563557 0 0.040637128 0 0 0.040637128 0;
createNode transform -n "pCube2" -p "group1";
	rename -uid "078C0B2E-45D9-5A58-08F1-04B86435D844";
	setAttr ".t" -type "double3" 0 0.34076323564694461 1.164274388460395 ;
	setAttr ".s" -type "double3" 0.48581293715670559 0.48581293715670559 0.61266409909321617 ;
createNode transform -n "transform4" -p "pCube2";
	rename -uid "84E7B240-443F-2B89-8C0D-EE9D041191DB";
	setAttr ".v" no;
createNode mesh -n "pCubeShape2" -p "transform4";
	rename -uid "A3FA1DC4-49E8-E35F-E65A-30BB434A61A8";
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
	setAttr -s 6 ".pt";
	setAttr ".pt[0]" -type "float3" 0 5.5511151e-17 0.38889247 ;
	setAttr ".pt[1]" -type "float3" 0 5.5511151e-17 0.38889247 ;
	setAttr ".pt[20]" -type "float3" 0 0 -4.4264193 ;
	setAttr ".pt[21]" -type "float3" 0 0 -4.4264193 ;
	setAttr ".pt[22]" -type "float3" 0 0 -4.4264193 ;
	setAttr ".pt[23]" -type "float3" 0 0 -4.4264193 ;
createNode transform -n "pCube1" -p "group1";
	rename -uid "D465A8B0-4AE6-1C20-888C-40BCCECD7443";
	setAttr ".t" -type "double3" 0 -0.2536632955277186 1.0367108599828501 ;
	setAttr ".s" -type "double3" 0.73256486860125469 0.73256486860125469 0.73256486860125469 ;
createNode transform -n "transform8" -p "pCube1";
	rename -uid "E0D56256-4420-1B98-602E-6CBB9C0C3240";
	setAttr ".v" no;
createNode mesh -n "pCubeShape1" -p "transform8";
	rename -uid "EAC98682-4EB2-58A8-9512-76859045F597";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.125 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt[8:11]" -type "float3"  0 0.62409115 -0.19290072 
		0 0.62409115 -0.19290072 0 -0.01134714 -0.2836774 0 -0.01134714 -0.2836774;
createNode transform -n "pCube12";
	rename -uid "4BB57FD2-49C9-8DAE-853C-D99B2E37F5A9";
	setAttr ".rp" -type "double3" 0.067891961523064592 -0.31779058331626797 0.24249865227790668 ;
	setAttr ".sp" -type "double3" 0.067891961523064592 -0.31779058331626797 0.24249865227790668 ;
createNode mesh -n "pCube12Shape" -p "pCube12";
	rename -uid "B78CE72E-4A74-64F7-722F-D997D4B7FEE3";
	setAttr -k off ".v";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.58099222183227539 0.38289915025234222 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
createNode lightLinker -s -n "lightLinker1";
	rename -uid "C89F324B-4D93-6025-93AE-048232B1DF0F";
	setAttr -s 4 ".lnk";
	setAttr -s 4 ".slnk";
createNode shapeEditorManager -n "shapeEditorManager";
	rename -uid "60661EFC-407A-DAA1-6697-D6AB6F17919B";
createNode poseInterpolatorManager -n "poseInterpolatorManager";
	rename -uid "D9BD7934-406F-D921-BBE9-68ACDF80AC5C";
createNode displayLayerManager -n "layerManager";
	rename -uid "C40D2487-4D66-A034-C899-2EB2F37D088A";
	setAttr -s 2 ".dli[1]"  1;
	setAttr -s 2 ".dli";
createNode displayLayer -n "defaultLayer";
	rename -uid "1C8012F2-4B08-2885-66E9-BBBD6553F1E9";
createNode renderLayerManager -n "renderLayerManager";
	rename -uid "784FB0F8-4CE4-99C2-AF6E-F1BA489825EC";
createNode renderLayer -n "defaultRenderLayer";
	rename -uid "6AD49A2E-42E4-0FE6-4B1F-56A8580D387F";
	setAttr ".g" yes;
createNode polyCube -n "polyCube1";
	rename -uid "460E1140-4583-4D70-2234-5199A920A1AF";
	setAttr ".cuv" 4;
createNode displayLayer -n "layer1";
	rename -uid "28E8B5BE-468A-8D56-DE99-7887F4AC1359";
	setAttr ".dt" 1;
	setAttr ".do" 1;
createNode polyExtrudeFace -n "polyExtrudeFace1";
	rename -uid "EC8E6E27-4CF0-5912-92AD-E4883A82F51D";
	setAttr ".ics" -type "componentList" 1 "f[0]";
	setAttr ".ix" -type "matrix" 0.73256486860125469 0 0 0 0 0.73256486860125469 0 0
		 0 0 0.73256486860125469 0 0 -0.2536632955277186 1.0367108599828501 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -0.26894087 3.2936811 ;
	setAttr ".rs" 52049;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.43625944639210901 -0.72031534352426785 3.2936811078956505 ;
	setAttr ".cbx" -type "double3" 0.43625944639210901 0.18243358879176752 3.2936811078956505 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak1";
	rename -uid "66D7DA71-4142-ACEA-77CE-5EB9500E7BF9";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk[0:7]" -type "float3"  -0.09552329 -0.13701122 2.58091521
		 0.09552329 -0.13701122 2.58091521 -0.09552329 0.09530136 2.58091521 0.09552329 0.09530136
		 2.58091521 -0.09552329 0.24922507 -2.46870184 0.09552329 0.24922507 -2.46870184 -0.09552329
		 0.016912377 -2.46870184 0.09552329 0.016912377 -2.46870184;
createNode polyExtrudeFace -n "polyExtrudeFace2";
	rename -uid "42BFE531-46C8-275E-E07F-A5A62814AC91";
	setAttr ".ics" -type "componentList" 1 "f[0]";
	setAttr ".ix" -type "matrix" 0.73256486860125469 0 0 0 0 0.73256486860125469 0 0
		 0 0 0.73256486860125469 0 0 -0.2536632955277186 1.0367108599828501 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -0.69224232 4.462286 ;
	setAttr ".rs" 43542;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.43625944639210901 -1.0572545595917542 4.2334268188030038 ;
	setAttr ".cbx" -type "double3" 0.43625944639210901 -0.32723007814741634 4.691145026416832 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak2";
	rename -uid "641D24C7-476F-DE64-CFAD-79AF69F661B9";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[8:11]" -type "float3"  0 -0.45994464 1.28281593 0
		 -0.45994464 1.28281593 0 -0.69572496 1.90763199 0 -0.69572496 1.90763199;
createNode polyCube -n "polyCube2";
	rename -uid "3A134573-4658-C519-0EC8-37A10DAD603C";
	setAttr ".cuv" 4;
createNode polyExtrudeFace -n "polyExtrudeFace3";
	rename -uid "863E3B93-4570-1E47-63DA-EE89A8EF1C44";
	setAttr ".ics" -type "componentList" 1 "f[2]";
	setAttr ".ix" -type "matrix" 0.48581293715670559 0 0 0 0 0.48581293715670559 0 0
		 0 0 0.61266409909321617 0 0 0.34076323564694461 1.164274388460395 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.34076324 -1.5784991 ;
	setAttr ".rs" 61064;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.2429064685783528 0.09785676706859181 -1.578499060294505 ;
	setAttr ".cbx" -type "double3" 0.2429064685783528 0.5836697042252974 -1.578499060294505 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak3";
	rename -uid "A748291A-4B76-0D61-E03B-7C9987D5696C";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk[0:7]" -type "float3"  0 0 2.63537121 0 0 2.63537121
		 0 0 2.63537121 0 0 2.63537121 -7.4505806e-09 7.4505806e-09 -3.97679782 7.4505806e-09
		 7.4505806e-09 -3.97679782 -7.4505806e-09 -7.4505806e-09 -3.97679782 7.4505806e-09
		 -7.4505806e-09 -3.97679782;
createNode polyExtrudeFace -n "polyExtrudeFace4";
	rename -uid "F925F1FF-4C3E-A4A3-7F57-2B9639C23F7F";
	setAttr ".ics" -type "componentList" 1 "f[2]";
	setAttr ".ix" -type "matrix" 0.48581293715670559 0 0 0 0 0.48581293715670559 0 0
		 0 0 0.61266409909321617 0 0 0.34076323564694461 1.164274388460395 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.34076324 -1.5784991 ;
	setAttr ".rs" 44418;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.38125733164288272 -0.040494095995938117 -1.578499060294505 ;
	setAttr ".cbx" -type "double3" 0.38125733164288272 0.72202059624653492 -1.578499060294505 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak4";
	rename -uid "A68282A0-4C66-DF57-5700-DC8731105D36";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[8:11]" -type "float3"  -0.2847822 0.28478223 0 0.2847822
		 0.28478223 0 0.2847822 -0.2847822 0 -0.2847822 -0.2847822 0;
createNode polyExtrudeFace -n "polyExtrudeFace5";
	rename -uid "973B01B1-4156-36EB-3928-F8BBCA359FE7";
	setAttr ".ics" -type "componentList" 1 "f[2]";
	setAttr ".ix" -type "matrix" 0.48581293715670559 0 0 0 0 0.48581293715670559 0 0
		 0 0 0.61266409909321617 0 0 0.34076323564694461 1.164274388460395 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.34076327 -3.0693386 ;
	setAttr ".rs" 47473;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.38125730268617514 -0.04049406703923053 -3.0693384449874346 ;
	setAttr ".cbx" -type "double3" 0.38125730268617514 0.72202062520324239 -3.0693384449874346 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak5";
	rename -uid "365D0BE8-4897-8325-1997-5EA4905BD6D0";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[12:15]" -type "float3"  0 0 -2.43337178 0 0 -2.43337178
		 0 0 -2.43337178 0 0 -2.43337178;
createNode polyExtrudeFace -n "polyExtrudeFace6";
	rename -uid "6FEBDBD2-4B7E-7F77-881C-88B33CD9EF7E";
	setAttr ".ics" -type "componentList" 1 "f[2]";
	setAttr ".ix" -type "matrix" 0.48581293715670559 0 0 0 0 0.48581293715670559 0 0
		 0 0 0.61266409909321617 0 0 0.34076323564694461 1.164274388460395 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.3407633 -3.0693395 ;
	setAttr ".rs" 51628;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.80293023496941696 -0.46216711514930248 -3.0693396135514659 ;
	setAttr ".cbx" -type "double3" 0.80293023496941696 1.143693702270022 -3.0693396135514659 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak6";
	rename -uid "79C18E80-45BC-C42E-46DA-4D9B9E8271E2";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[16:19]" -type "float3"  -0.86797392 0.86797422 -1.0376881e-06
		 0.86797392 0.86797422 -1.0376881e-06 0.86797392 -0.8679741 -1.0376881e-06 -0.86797392
		 -0.8679741 -1.0376881e-06;
createNode polyCube -n "polyCube3";
	rename -uid "8E9EF17F-42CE-803A-86DC-148F34ACD075";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube6";
	rename -uid "F7F27857-435B-EFF6-97A0-6E9300AE150A";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube7";
	rename -uid "9C46F564-49FB-CBBE-970C-1B92A6296EDE";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube8";
	rename -uid "D3E44688-4004-C5BC-8A61-35BB03263FC8";
	setAttr ".cuv" 4;
createNode polyExtrudeFace -n "polyExtrudeFace9";
	rename -uid "DD94D2D8-4C41-26A7-0FB1-50A12D46A3E2";
	setAttr ".ics" -type "componentList" 1 "f[3]";
	setAttr ".ix" -type "matrix" 0.19450609138838876 0 0 0 0 0.11596842116996413 0 0
		 0 0 0.11596842116996413 0 0 -0.73201512935594693 2.6538469048894946 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -0.92401958 2.7050183 ;
	setAttr ".rs" 53646;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.097253045694194379 -0.94107672959128952 2.6616544507312327 ;
	setAttr ".cbx" -type "double3" 0.097253045694194379 -0.90696246247732026 2.7483820912670991 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak8";
	rename -uid "F8C72D8B-4650-1A85-552E-2481EFF7A07C";
	setAttr ".uopa" yes;
	setAttr -s 6 ".tk";
	setAttr ".tk[0]" -type "float3" 0 -1.0085773 0.31518042 ;
	setAttr ".tk[1]" -type "float3" 0 -1.0085773 0.31518042 ;
	setAttr ".tk[4]" -type "float3" 0 1.0547119e-15 -0.71440887 ;
	setAttr ".tk[5]" -type "float3" 0 1.0547119e-15 -0.71440887 ;
	setAttr ".tk[6]" -type "float3" 0 -1.3027459 0.56732476 ;
	setAttr ".tk[7]" -type "float3" 0 -1.3027459 0.56732476 ;
createNode polyExtrudeFace -n "polyExtrudeFace10";
	rename -uid "CB676A83-441F-D841-DA8A-61A43092161F";
	setAttr ".ics" -type "componentList" 1 "f[3]";
	setAttr ".ix" -type "matrix" 0.19450609138838876 0 0 0 0 0.11596842116996413 0 0
		 0 0 0.11596842116996413 0 0 -0.73201512935594693 2.6538469048894946 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -1.1676929 2.8268549 ;
	setAttr ".rs" 41299;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.097253045694194379 -1.1676928901016523 2.783491128870061 ;
	setAttr ".cbx" -type "double3" 0.097253045694194379 -1.1676928348035998 2.8702188247039793 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak9";
	rename -uid "396C6A94-4896-300B-7F82-04A335274BD3";
	setAttr ".uopa" yes;
	setAttr -s 4 ".tk[8:11]" -type "float3"  0 -1.95411873 1.050601363
		 0 -1.95411873 1.050601363 0 -2.24828768 1.050601363 0 -2.24828768 1.050601363;
createNode polyPlane -n "polyPlane1";
	rename -uid "3B0CC552-465D-8C0E-05CA-4483E06E0663";
	setAttr ".sw" 1;
	setAttr ".sh" 1;
	setAttr ".cuv" 2;
createNode polyExtrudeEdge -n "polyExtrudeEdge1";
	rename -uid "21D1BC79-44D7-F17F-6119-07984957AF33";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[3]";
	setAttr ".ix" -type "matrix" 0.40722195421547619 0 0 0 0 0 -0.40722195421547619 0
		 0 0.40722195421547619 0 0 0 -0.89864972370332064 3.280633937948561 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -1.1022607 3.2806339 ;
	setAttr ".rs" 41422;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.20361097710773809 -1.1022607008110588 3.280633937948561 ;
	setAttr ".cbx" -type "double3" 0.20361097710773809 -1.1022607008110588 3.280633937948561 ;
createNode polyExtrudeEdge -n "polyExtrudeEdge2";
	rename -uid "0C40FA8C-4990-FFA7-6E82-F8A4F5B97F2E";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[6]";
	setAttr ".ix" -type "matrix" 0.40722195421547619 0 0 0 0 0 -0.40722195421547619 0
		 0 0.40722195421547619 0 0 0 -0.89864972370332064 3.280633937948561 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -1.5138294 3.0360224 ;
	setAttr ".rs" 42464;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.20361096497157813 -1.5138293420788389 3.0360224250177588 ;
	setAttr ".cbx" -type "double3" 0.20361096497157813 -1.5138293420788389 3.0360224250177588 ;
createNode polyTweak -n "polyTweak10";
	rename -uid "D77DEB22-4217-AA6E-7D52-3CBAD47007DD";
	setAttr ".uopa" yes;
	setAttr -s 2 ".tk[4:5]" -type "float3"  0 0.60068351 -1.010674 0 0.60068351
		 -1.010674;
createNode polyExtrudeEdge -n "polyExtrudeEdge3";
	rename -uid "ED76E559-48C3-1634-77AB-BFA024B38A3E";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[9]";
	setAttr ".ix" -type "matrix" 0.40722195421547619 0 0 0 0 0 -0.40722195421547619 0
		 0 0.40722195421547619 0 0 0 -0.89864972370332064 3.280633937948561 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 -1.5138294 2.3254833 ;
	setAttr ".rs" 40650;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.20361095283541816 -1.5138293420788389 2.3254833909080137 ;
	setAttr ".cbx" -type "double3" 0.20361095283541816 -1.5138293420788389 2.3254833909080137 ;
createNode polyTweak -n "polyTweak11";
	rename -uid "54AED3E9-4C8F-B1A7-B55A-FEA7F6993892";
	setAttr ".uopa" yes;
	setAttr -s 2 ".tk[6:7]" -type "float3"  0 1.74484444 0 0 1.74484444
		 0;
createNode script -n "uiConfigurationScriptNode";
	rename -uid "BD1F8324-4896-F6BA-C836-98BF4F016A06";
	setAttr ".b" -type "string" (
		"// Maya Mel UI Configuration File.\n//\n//  This script is machine generated.  Edit at your own risk.\n//\n//\n\nglobal string $gMainPane;\nif (`paneLayout -exists $gMainPane`) {\n\n\tglobal int $gUseScenePanelConfig;\n\tint    $useSceneConfig = $gUseScenePanelConfig;\n\tint    $nodeEditorPanelVisible = stringArrayContains(\"nodeEditorPanel1\", `getPanel -vis`);\n\tint    $nodeEditorWorkspaceControlOpen = (`workspaceControl -exists nodeEditorPanel1Window` && `workspaceControl -q -visible nodeEditorPanel1Window`);\n\tint    $menusOkayInPanels = `optionVar -q allowMenusInPanels`;\n\tint    $nVisPanes = `paneLayout -q -nvp $gMainPane`;\n\tint    $nPanes = 0;\n\tstring $editorName;\n\tstring $panelName;\n\tstring $itemFilterName;\n\tstring $panelConfig;\n\n\t//\n\t//  get current state of the UI\n\t//\n\tsceneUIReplacement -update $gMainPane;\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Top View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Top View\")) -mbv $menusOkayInPanels  $panelName;\n"
		+ "\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"top\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 0\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n"
		+ "            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n"
		+ "            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Side View\")) `;\n"
		+ "\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Side View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"side\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 0\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n"
		+ "            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n"
		+ "            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n"
		+ "\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Front View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Front View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"side\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 1\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n"
		+ "            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n"
		+ "            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 0\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n"
		+ "            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Persp View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Persp View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"persp\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 1\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 1\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n"
		+ "            -activeComponentsXray 0\n            -displayTextures 1\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n"
		+ "            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 0\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n"
		+ "            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1054\n            -height 667\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"outlinerPanel\" (localizedPanelLabel(\"ToggledOutliner\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\toutlinerPanel -edit -l (localizedPanelLabel(\"ToggledOutliner\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        outlinerEditor -e \n            -showShapes 1\n            -showAssignedMaterials 0\n            -showTimeEditor 1\n            -showReferenceNodes 1\n            -showReferenceMembers 1\n            -showAttributes 0\n            -showConnected 0\n            -showAnimCurvesOnly 0\n            -showMuteInfo 0\n            -organizeByLayer 1\n"
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
		+ "\t\t\t\t\t\"$panelName = `modelPanel -unParent -l (localizedPanelLabel(\\\"Persp View\\\")) -mbv $menusOkayInPanels `;\\n$editorName = $panelName;\\nmodelEditor -e \\n    -cam `findStartUpCamera persp` \\n    -useInteractiveMode 0\\n    -displayLights \\\"default\\\" \\n    -displayAppearance \\\"smoothShaded\\\" \\n    -activeOnly 0\\n    -ignorePanZoom 0\\n    -wireframeOnShaded 1\\n    -headsUpDisplay 1\\n    -holdOuts 1\\n    -selectionHiliteDisplay 1\\n    -useDefaultMaterial 0\\n    -bufferMode \\\"double\\\" \\n    -twoSidedLighting 1\\n    -backfaceCulling 0\\n    -xray 0\\n    -jointXray 0\\n    -activeComponentsXray 0\\n    -displayTextures 1\\n    -smoothWireframe 0\\n    -lineWidth 1\\n    -textureAnisotropic 0\\n    -textureHilight 1\\n    -textureSampling 2\\n    -textureDisplay \\\"modulate\\\" \\n    -textureMaxSize 32768\\n    -fogging 0\\n    -fogSource \\\"fragment\\\" \\n    -fogMode \\\"linear\\\" \\n    -fogStart 0\\n    -fogEnd 100\\n    -fogDensity 0.1\\n    -fogColor 0.5 0.5 0.5 1 \\n    -depthOfFieldPreview 1\\n    -maxConstantTransparency 1\\n    -rendererName \\\"vp2Renderer\\\" \\n    -objectFilterShowInHUD 1\\n    -isFiltered 0\\n    -colorResolution 256 256 \\n    -bumpResolution 512 512 \\n    -textureCompression 0\\n    -transparencyAlgorithm \\\"frontAndBackCull\\\" \\n    -transpInShadows 0\\n    -cullingOverride \\\"none\\\" \\n    -lowQualityLighting 0\\n    -maximumNumHardwareLights 1\\n    -occlusionCulling 0\\n    -shadingModel 0\\n    -useBaseRenderer 0\\n    -useReducedRenderer 0\\n    -smallObjectCulling 0\\n    -smallObjectThreshold -1 \\n    -interactiveDisableShadows 0\\n    -interactiveBackFaceCull 0\\n    -sortTransparent 1\\n    -controllers 1\\n    -nurbsCurves 1\\n    -nurbsSurfaces 1\\n    -polymeshes 1\\n    -subdivSurfaces 1\\n    -planes 1\\n    -lights 1\\n    -cameras 1\\n    -controlVertices 1\\n    -hulls 1\\n    -grid 0\\n    -imagePlane 1\\n    -joints 1\\n    -ikHandles 1\\n    -deformers 1\\n    -dynamics 1\\n    -particleInstancers 1\\n    -fluids 1\\n    -hairSystems 1\\n    -follicles 1\\n    -nCloths 1\\n    -nParticles 1\\n    -nRigids 1\\n    -dynamicConstraints 1\\n    -locators 1\\n    -manipulators 1\\n    -pluginShapes 1\\n    -dimensions 1\\n    -handles 1\\n    -pivots 1\\n    -textures 1\\n    -strokes 1\\n    -motionTrails 1\\n    -clipGhosts 1\\n    -greasePencils 1\\n    -shadows 0\\n    -captureSequenceNumber -1\\n    -width 1054\\n    -height 667\\n    -sceneRenderFilter 0\\n    $editorName;\\nmodelEditor -e -viewSelected 0 $editorName\"\n"
		+ "\t\t\t\t\t\"modelPanel -edit -l (localizedPanelLabel(\\\"Persp View\\\")) -mbv $menusOkayInPanels  $panelName;\\n$editorName = $panelName;\\nmodelEditor -e \\n    -cam `findStartUpCamera persp` \\n    -useInteractiveMode 0\\n    -displayLights \\\"default\\\" \\n    -displayAppearance \\\"smoothShaded\\\" \\n    -activeOnly 0\\n    -ignorePanZoom 0\\n    -wireframeOnShaded 1\\n    -headsUpDisplay 1\\n    -holdOuts 1\\n    -selectionHiliteDisplay 1\\n    -useDefaultMaterial 0\\n    -bufferMode \\\"double\\\" \\n    -twoSidedLighting 1\\n    -backfaceCulling 0\\n    -xray 0\\n    -jointXray 0\\n    -activeComponentsXray 0\\n    -displayTextures 1\\n    -smoothWireframe 0\\n    -lineWidth 1\\n    -textureAnisotropic 0\\n    -textureHilight 1\\n    -textureSampling 2\\n    -textureDisplay \\\"modulate\\\" \\n    -textureMaxSize 32768\\n    -fogging 0\\n    -fogSource \\\"fragment\\\" \\n    -fogMode \\\"linear\\\" \\n    -fogStart 0\\n    -fogEnd 100\\n    -fogDensity 0.1\\n    -fogColor 0.5 0.5 0.5 1 \\n    -depthOfFieldPreview 1\\n    -maxConstantTransparency 1\\n    -rendererName \\\"vp2Renderer\\\" \\n    -objectFilterShowInHUD 1\\n    -isFiltered 0\\n    -colorResolution 256 256 \\n    -bumpResolution 512 512 \\n    -textureCompression 0\\n    -transparencyAlgorithm \\\"frontAndBackCull\\\" \\n    -transpInShadows 0\\n    -cullingOverride \\\"none\\\" \\n    -lowQualityLighting 0\\n    -maximumNumHardwareLights 1\\n    -occlusionCulling 0\\n    -shadingModel 0\\n    -useBaseRenderer 0\\n    -useReducedRenderer 0\\n    -smallObjectCulling 0\\n    -smallObjectThreshold -1 \\n    -interactiveDisableShadows 0\\n    -interactiveBackFaceCull 0\\n    -sortTransparent 1\\n    -controllers 1\\n    -nurbsCurves 1\\n    -nurbsSurfaces 1\\n    -polymeshes 1\\n    -subdivSurfaces 1\\n    -planes 1\\n    -lights 1\\n    -cameras 1\\n    -controlVertices 1\\n    -hulls 1\\n    -grid 0\\n    -imagePlane 1\\n    -joints 1\\n    -ikHandles 1\\n    -deformers 1\\n    -dynamics 1\\n    -particleInstancers 1\\n    -fluids 1\\n    -hairSystems 1\\n    -follicles 1\\n    -nCloths 1\\n    -nParticles 1\\n    -nRigids 1\\n    -dynamicConstraints 1\\n    -locators 1\\n    -manipulators 1\\n    -pluginShapes 1\\n    -dimensions 1\\n    -handles 1\\n    -pivots 1\\n    -textures 1\\n    -strokes 1\\n    -motionTrails 1\\n    -clipGhosts 1\\n    -greasePencils 1\\n    -shadows 0\\n    -captureSequenceNumber -1\\n    -width 1054\\n    -height 667\\n    -sceneRenderFilter 0\\n    $editorName;\\nmodelEditor -e -viewSelected 0 $editorName\"\n"
		+ "\t\t\t\t$configName;\n\n            setNamedPanelLayout (localizedPanelLabel(\"Current Layout\"));\n        }\n\n        panelHistory -e -clear mainPanelHistory;\n        sceneUIReplacement -clear;\n\t}\n\n\ngrid -spacing 1 -size 1 -divisions 9 -displayAxes yes -displayGridLines yes -displayDivisionLines yes -displayPerspectiveLabels no -displayOrthographicLabels no -displayAxesBold yes -perspectiveLabelPosition axis -orthographicLabelPosition edge;\nviewManip -drawCompass 0 -compassAngle 0 -frontParameters \"\" -homeParameters \"\" -selectionLockParameters \"\";\n}\n");
	setAttr ".st" 3;
createNode script -n "sceneConfigurationScriptNode";
	rename -uid "B1B62C45-43F8-6D2F-D403-A4A5699B0204";
	setAttr ".b" -type "string" "playbackOptions -min 1 -max 120 -ast 1 -aet 200 ";
	setAttr ".st" 6;
createNode polyCube -n "polyCube9";
	rename -uid "12B0DC18-4E54-730B-E19F-4F96282FCC99";
	setAttr ".cuv" 4;
createNode polyCube -n "polyCube10";
	rename -uid "6704CB73-40C9-BAAF-2B64-13B5D0E2F552";
	setAttr ".cuv" 4;
createNode polyTweak -n "polyTweak12";
	rename -uid "3B276A47-4772-B159-CF7C-EEA8626BCD59";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk[8:15]" -type "float3"  0.23685755 -0.75825787 0.72733223
		 -0.23685755 -0.75825787 0.72733223 -0.23685755 -0.11242326 0.19272555 0.23685755
		 -0.11242326 0.19272555 0.18881273 -0.97279394 2.48196435 -0.18881273 -0.97279394
		 2.48196435 -0.18881273 -0.23703168 1.99615562 0.18881273 -0.23703168 1.99615562;
createNode deleteComponent -n "deleteComponent1";
	rename -uid "7EC8CE57-42B8-22C9-EA3E-3EA6BA64E935";
	setAttr ".dc" -type "componentList" 2 "f[0]" "f[10:13]";
createNode polyCloseBorder -n "polyCloseBorder1";
	rename -uid "8B579A55-4EE8-3C1A-8368-91B90D5AB529";
	setAttr ".ics" -type "componentList" 3 "e[14]" "e[16]" "e[18:19]";
createNode polyCube -n "polyCube11";
	rename -uid "2430BFF3-4351-23A5-B410-4A9C768BB8C2";
	setAttr ".cuv" 4;
createNode polyUnite -n "polyUnite1";
	rename -uid "EB8BDFF1-4EC2-4131-0D78-6AB98CDAA804";
	setAttr -s 10 ".ip";
	setAttr -s 10 ".im";
createNode groupId -n "groupId1";
	rename -uid "9EC44358-498C-0356-DF7E-75B4284E53EF";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts1";
	rename -uid "A42438EA-4090-3D99-D24E-21BA2F1EAAC1";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId2";
	rename -uid "3893D007-414D-5EDE-7238-FE8446204F5F";
	setAttr ".ihi" 0;
createNode groupId -n "groupId3";
	rename -uid "2168CA73-43AF-0BC4-A05F-CDA38C151CDA";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts2";
	rename -uid "37EF8BFC-442B-968A-DCD9-AEBC010B7B65";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId4";
	rename -uid "D93E9D58-4C0A-4AA6-491D-2BBE68A239F1";
	setAttr ".ihi" 0;
createNode groupId -n "groupId5";
	rename -uid "FF4CA486-4708-7C33-85BA-8186CB2D2944";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts3";
	rename -uid "814B95C4-4F24-3B6B-56DF-1E9539DFFA86";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:9]";
createNode groupId -n "groupId6";
	rename -uid "1A33F636-4621-8A0A-E531-5EBB5CD28C86";
	setAttr ".ihi" 0;
createNode groupId -n "groupId7";
	rename -uid "A2503A97-4877-7CE7-5A5A-0FAB8C3DBD09";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts4";
	rename -uid "1C06655C-4DCD-9A5C-80E6-7B84345A6FAD";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId8";
	rename -uid "ECA3AE00-4858-3B53-3D5F-5E86F5AF5632";
	setAttr ".ihi" 0;
createNode groupId -n "groupId9";
	rename -uid "748006DB-46C3-FE8C-BD9F-7480ED0C87D6";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts5";
	rename -uid "6A0200CA-48ED-C92E-D317-20A9333F8A43";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId10";
	rename -uid "F89A54E8-4178-2F90-0768-CE90BBD71281";
	setAttr ".ihi" 0;
createNode groupId -n "groupId11";
	rename -uid "48EA8F70-4D9D-C972-6582-45A7176FC2B9";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts6";
	rename -uid "04856CC8-4E86-79BF-78E2-3486E3E3EDA6";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:13]";
createNode groupId -n "groupId12";
	rename -uid "6B5B8EA7-437F-9B74-1C47-509A86F2344B";
	setAttr ".ihi" 0;
createNode groupId -n "groupId13";
	rename -uid "323E31F8-4861-B56E-B61D-9DBCFD7A950E";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts7";
	rename -uid "CDBE0E73-4A50-CC3D-4A96-8191609FE105";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:21]";
createNode groupId -n "groupId14";
	rename -uid "04817F0A-42FB-A827-E532-5AA1605EA2B5";
	setAttr ".ihi" 0;
createNode groupId -n "groupId15";
	rename -uid "AB075735-46B8-101D-F16F-2C99D35424A5";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts8";
	rename -uid "DFC717EF-4B6E-BD2C-CFA0-B384096E1D06";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId16";
	rename -uid "023AA8EF-4B2F-A5AB-7F74-76958601485E";
	setAttr ".ihi" 0;
createNode groupId -n "groupId17";
	rename -uid "26C1066C-498F-5B07-6FE4-01A8A7A25F74";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts9";
	rename -uid "00B5A544-46E3-A92A-C342-2A920A59C986";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:3]";
createNode groupId -n "groupId18";
	rename -uid "A36E4262-4BBD-DF8B-3835-DEB9F6433C9C";
	setAttr ".ihi" 0;
createNode groupId -n "groupId19";
	rename -uid "A724A6AF-4F33-7E11-2FE6-DB9B1F081CCE";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts10";
	rename -uid "5C7BCABD-429B-D20A-CD57-D2AD847A703B";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:5]";
createNode groupId -n "groupId20";
	rename -uid "EDE7520B-4470-D252-28D8-DE9A6A12E475";
	setAttr ".ihi" 0;
createNode polyMapDel -n "polyMapDel1";
	rename -uid "3C2000FD-490A-0FAA-AD6E-EFB9941CDE62";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[0:85]";
createNode lambert -n "layout";
	rename -uid "52A7A799-47CF-F63B-F386-4396AF6B262D";
createNode shadingEngine -n "lambert2SG";
	rename -uid "EB39CC94-4C02-915F-4B18-DF9BD36AD595";
	setAttr ".ihi" 0;
	setAttr ".ro" yes;
createNode materialInfo -n "materialInfo1";
	rename -uid "38A93E59-45F8-7A5E-6E35-009B36E0B2C3";
createNode lambert -n "pixelMeasure";
	rename -uid "FD455DA7-4EEC-4120-0D51-FFB7291DD558";
createNode shadingEngine -n "lambert3SG";
	rename -uid "0668AE5A-4143-83A1-4345-AD831F0C725A";
	setAttr ".ihi" 0;
	setAttr ".ro" yes;
createNode materialInfo -n "materialInfo2";
	rename -uid "24B5EF1B-4B1E-61D4-4E97-F9BBB0C925B6";
createNode file -n "file1";
	rename -uid "6797C36D-4CB7-D7BE-173B-8087C97150D4";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Documents/UVminecraftTexture.png";
	setAttr ".ft" 0;
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "place2dTexture1";
	rename -uid "0DBBE25A-4ED0-2050-167D-28BF3CF44EF9";
createNode polyAutoProj -n "polyAutoProj1";
	rename -uid "F3CE78BF-4165-EC25-5A34-4F9B6E91CF17";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[48:49]" "f[52:53]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 4.9019631147384644 4.9019631147384644 4.9019631147384644 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV1";
	rename -uid "472ABB8E-4E29-9F95-294F-0993CD0CBA9D";
	setAttr ".uopa" yes;
	setAttr -s 16 ".uvtk[0:15]" -type "float2" 0.49832156 -0.55273533 0.73351604
		 0.44328079 0.63480502 0.46658996 0.41104224 -0.48101461 0.6284411 -0.48275697 0.39323908
		 0.51325905 0.30596021 0.44153765 0.52973014 -0.5060668 0.29943389 0.48992991 0.20072287
		 0.46662003 0.42449275 -0.48098457 0.52320373 -0.45767462 0.17466028 0.47033432 0.075949341
		 0.49413332 0.052150398 0.39542234 0.15086126 0.37162334;
createNode polyMapSew -n "polyMapSew1";
	rename -uid "63150835-4E7F-5027-213F-ABBB2829F38E";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[102:103]";
createNode polyTweakUV -n "polyTweakUV2";
	rename -uid "29FE1400-4447-96D8-E426-C5A36D7AEA0F";
	setAttr ".uopa" yes;
	setAttr -s 12 ".uvtk[0:11]" -type "float2" 0 2.9802322e-08 9.059906e-06
		 -2.9802322e-08 8.5890293e-05 0.00017940765 2.1994114e-05 0.00017940765 -8.5830688e-05
		 0.00026911288 0 0.00026911288 -2.1994114e-05 0.00026917248 3.8385391e-05 0.00026908307
		 -0.0060822503 0.019729028 -0.0060821907 -0.027374795 0.04102153 -0.027374705 0.041021649
		 0.019729057;
createNode polyAutoProj -n "polyAutoProj2";
	rename -uid "D3DC4BF0-4D4F-7C8B-21DE-AF88CA39AF43";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[0:1]" "f[3:5]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.52605915069580078 0.52605915069580078 0.52605915069580078 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV3";
	rename -uid "89E16C57-434C-9A63-D264-819203D83ADE";
	setAttr ".uopa" yes;
	setAttr -s 32 ".uvtk[0:31]" -type "float2" -0.24210189 0 -0.24210192
		 0 -0.24210192 0 -0.24210192 0 -0.24210192 0 -0.24210192 0 -0.24210192 0 -0.24210192
		 0 -0.24210191 0 -0.24210191 0 -0.24210192 0 -0.24210192 0 0.13627894 -0.01930939
		 0.13627894 0.43072912 -0.21533847 0.44226614 -0.21533847 0.011455933 -0.22181526
		 -0.007772346 -0.22181526 0.44226614 -0.57343256 0.41150084 -0.57343256 -0.01930939
		 -0.67410892 -0.011455913 -0.78288084 -0.011455913 -0.78288084 -0.44226605 -0.67410892
		 -0.44226605 -0.57972938 0.44226611 -0.68850136 0.44226611 -0.68850136 -0.0077723758
		 -0.57972938 -0.0077723758 -0.55967504 -0.02299281 -0.66844702 -0.02299281 -0.66844702
		 -0.37461001 -0.55967504 -0.37461001;
createNode polyAutoProj -n "polyAutoProj3";
	rename -uid "EE2A9296-4330-9E23-36E2-079DC276F20A";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[70:75]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.1588753461837769 1.1588753461837769 1.1588753461837769 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV4";
	rename -uid "5D0788FE-423E-8AD9-088D-E8B35B9FF286";
	setAttr ".uopa" yes;
	setAttr -s 24 ".uvtk[32:55]" -type "float2" -0.25303134 0.046667818 -0.40330979
		 0.046667818 -0.40330979 -0.26758653 -0.25303134 -0.26758653 -0.097212926 0.046667818
		 -0.24749142 0.046667818 -0.24749142 -0.26758653 -0.097212926 -0.26758653 0.019768193
		 0.050271995 0.019768193 0.52368069 -0.13051024 0.52368069 -0.13051024 0.050271995
		 -0.40884969 -0.42674071 -0.40884969 0.046667818 -0.55912817 0.046667818 -0.55912817
		 -0.42674071 0.34023151 0.050271995 0.34023151 0.52368069 0.025977105 0.52368069 0.025977105
		 0.050271995 -0.13685575 0.050271995 -0.13685575 0.52368069 -0.45111006 0.52368069
		 -0.45111006 0.050271995;
createNode polyAutoProj -n "polyAutoProj4";
	rename -uid "7C0FE37F-4B6D-5233-EDA6-52A197195F22";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[54:61]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.4908400774002075 1.4908400774002075 1.4908400774002075 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV5";
	rename -uid "CD1E2AC5-4235-3A56-1E0D-6D9F6DA3D230";
	setAttr ".uopa" yes;
	setAttr -s 24 ".uvtk[56:79]" -type "float2" 0.051362514 -0.021747824
		 0.27383745 -0.021747824 0.27383745 0.41322693 0.051362514 0.41322693 -0.12645765
		 -0.025158549 -0.34893268 -0.025158549 -0.34893268 -0.46013328 -0.12645765 -0.46013328
		 0.50216031 0.41322693 0.27968544 0.41322693 0.27968544 -0.021747824 0.50216031 -0.021747824
		 -0.17679377 -0.021747824 0.045681179 -0.021747824 0.045681179 0.41322693 -0.17679377
		 0.41322693 0.060893983 -0.20726755 -0.080849119 -0.20726755 -0.12121499 -0.24763346
		 0.10125988 -0.24763346 -0.080849119 -0.065524429 -0.12121499 -0.025158549 0.060893983
		 -0.065524429 0.10125988 -0.025158549;
createNode polyAutoProj -n "polyAutoProj5";
	rename -uid "34C169DD-4F3E-AF52-09D4-FF806EFE32B5";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[50]" "f[62:69]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 2.7119088172912598 2.7119088172912598 2.7119088172912598 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV6";
	rename -uid "33A7E387-4117-0503-6542-D9893197A971";
	setAttr ".uopa" yes;
	setAttr -s 28 ".uvtk[80:107]" -type "float2" 0.23906815 0.022052497 0.44275036
		 0.022052497 0.44275036 0.36602214 0.23906815 0.36602214 0.12036747 0.019108385 -0.083314851
		 0.019108385 -0.083314851 -0.32486117 0.12036747 -0.32486117 0.65133536 0.36602208
		 0.44765314 0.36602208 0.44765314 0.022052467 0.65133536 0.022052467 0.030627087 0.022052407
		 0.23430932 0.022052407 0.23430932 0.36602208 0.030627087 0.36602208 0.32857493 0.019108325
		 0.12489271 0.019108325 0.12489271 -0.18457383 0.32857493 -0.18457383 0.48329881 -0.1310901
		 0.38658395 -0.1310901 0.33310029 -0.18457383 0.5367825 -0.18457383 0.38658395 -0.034375243
		 0.33310029 0.019108385 0.48329881 -0.034375243 0.5367825 0.019108385;
createNode polyAutoProj -n "polyAutoProj6";
	rename -uid "D8FA9582-4DD3-EC09-35B5-CA99B3C6FBC5";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[12:16]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 4.4317370653152466 4.4317370653152466 4.4317370653152466 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV7";
	rename -uid "4D3A5A0D-4904-867B-F15D-2E9238142248";
	setAttr ".uopa" yes;
	setAttr -s 20 ".uvtk[108:127]" -type "float2" 0.030338032 -0.57871282 0.21721937
		 0.25110653 0.049261659 0.29344791 -0.13761969 -0.53637147 -0.18528588 -0.52435505
		 0.043553386 0.29488695 -0.12440424 0.33722827 -0.35324353 -0.4820137 0.57598495 0.16066344
		 0.4136517 0.20158695 0.20579131 -0.62294376 0.3681246 -0.66386724 0.40700555 0.20326237
		 0.24467225 0.24418585 0.036811918 -0.5803448 0.19914518 -0.62126839 -0.35072982 -0.48639929
		 -0.51306313 -0.44547582 -0.55540442 -0.61343348 -0.39307112 -0.65435684;
createNode polyAutoProj -n "polyAutoProj7";
	rename -uid "3399FE4E-45B7-10A0-895C-61A59F51C7E0";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[17:20]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.3379738032817841 1.3379738032817841 1.3379738032817841 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV8";
	rename -uid "B0A2DC0D-498E-64AA-507D-038A9835E91E";
	setAttr ".uopa" yes;
	setAttr -s 16 ".uvtk[128:143]" -type "float2" -0.24252105 0.04692325 -0.24252105
		 -0.20922744 0.13509709 -0.038885698 0.13521494 0.17041619 -0.24774978 -0.20922744
		 -0.24774978 0.04692325 -0.62548578 0.17041619 -0.625368 -0.038885698 -0.43919805
		 -0.21252146 -0.68677104 -0.21252146 -0.63753736 -0.59025747 -0.48843166 -0.59025747
		 -0.43413475 -0.59013975 -0.1865617 -0.59013975 -0.23579535 -0.21252152 -0.38490114
		 -0.21252152;
createNode polyAutoProj -n "polyAutoProj8";
	rename -uid "CE4FB367-4827-8F52-E2A8-958D150400B0";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[80:85]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.7214889526367188 1.7214889526367188 1.7214889526367188 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV9";
	rename -uid "5D4DE014-42A6-11EF-C690-56B293F05986";
	setAttr ".uopa" yes;
	setAttr -s 24 ".uvtk[144:167]" -type "float2" -0.13873541 -0.2694025 -0.18650571
		 -0.030402586 -0.3946861 -0.022697642 -0.37157145 -0.34182826 -0.43905735 -0.65591776
		 -0.39128709 -0.41691783 -0.6241231 -0.34449208 -0.64723778 -0.66362262 0.017475903
		 -0.022697642 -0.13389049 -0.022697642 -0.12075858 -0.34182826 0.0043439753 -0.34182826
		 -0.24895811 -0.34449208 -0.37406063 -0.34449208 -0.38719255 -0.58349204 -0.23582616
		 -0.58349204 -0.080365255 -0.34449208 -0.23173162 -0.34449208 -0.23173162 -0.57732809
		 -0.080365255 -0.57732809 0.017475903 -0.57999194 -0.10762665 -0.57999194 -0.10762665
		 -0.78817225 0.017475903 -0.78817225;
createNode polyAutoProj -n "polyAutoProj9";
	rename -uid "B48857F4-4BC9-8E69-E654-0B9A5A46C92B";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[6]" "f[8:11]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.8785985708236694 1.8785985708236694 1.8785985708236694 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV10";
	rename -uid "B7D9EF53-4561-28C1-135E-E69A29434725";
	setAttr ".uopa" yes;
	setAttr -s 44 ".uvtk";
	setAttr ".uvtk[56]" -type "float2" -0.02024268 0.0082189506 ;
	setAttr ".uvtk[57]" -type "float2" 0.011002133 -0.031037264 ;
	setAttr ".uvtk[58]" -type "float2" 0.088572562 0.030051088 ;
	setAttr ".uvtk[59]" -type "float2" 0.057327989 0.069307312 ;
	setAttr ".uvtk[60]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[61]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[62]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[63]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[64]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[65]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[66]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[67]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[68]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[69]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[70]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[71]" -type "float2" 0 0.080514789 ;
	setAttr ".uvtk[72]" -type "float2" -0.051007383 -0.025052866 ;
	setAttr ".uvtk[73]" -type "float2" -0.04608845 -0.025052866 ;
	setAttr ".uvtk[74]" -type "float2" -0.044687621 -0.023652067 ;
	setAttr ".uvtk[75]" -type "float2" -0.052408211 -0.023652067 ;
	setAttr ".uvtk[76]" -type "float2" -0.04608845 -0.029971775 ;
	setAttr ".uvtk[77]" -type "float2" -0.044687621 -0.031372558 ;
	setAttr ".uvtk[78]" -type "float2" -0.051007383 -0.029971775 ;
	setAttr ".uvtk[79]" -type "float2" -0.052408211 -0.031372558 ;
	setAttr ".uvtk[168]" -type "float2" 0.62911505 -0.425686 ;
	setAttr ".uvtk[169]" -type "float2" 0.5091812 0.10691433 ;
	setAttr ".uvtk[170]" -type "float2" 0.3415969 0.16166674 ;
	setAttr ".uvtk[171]" -type "float2" 0.35984766 -0.3839699 ;
	setAttr ".uvtk[172]" -type "float2" 0.21515405 -0.37093359 ;
	setAttr ".uvtk[173]" -type "float2" 0.33508784 0.16166674 ;
	setAttr ".uvtk[174]" -type "float2" 0.065820523 0.11995062 ;
	setAttr ".uvtk[175]" -type "float2" 0.047569714 -0.425686 ;
	setAttr ".uvtk[176]" -type "float2" 0.041229628 0.16166674 ;
	setAttr ".uvtk[177]" -type "float2" -0.18110701 0.16166674 ;
	setAttr ".uvtk[178]" -type "float2" -0.18110701 -0.37093359 ;
	setAttr ".uvtk[179]" -type "float2" 0.041229628 -0.37093359 ;
	setAttr ".uvtk[180]" -type "float2" -0.011633679 -0.37439239 ;
	setAttr ".uvtk[181]" -type "float2" -0.23397037 -0.37439239 ;
	setAttr ".uvtk[182]" -type "float2" -0.23397037 -0.64365965 ;
	setAttr ".uvtk[183]" -type "float2" -0.011633679 -0.64365965 ;
	setAttr ".uvtk[184]" -type "float2" 0.16126706 -0.65148151 ;
	setAttr ".uvtk[185]" -type "float2" 0.16126706 -0.42914486 ;
	setAttr ".uvtk[186]" -type "float2" -0.0063171834 -0.42914486 ;
	setAttr ".uvtk[187]" -type "float2" -0.0063171834 -0.65148151 ;
createNode polyMapSew -n "polyMapSew2";
	rename -uid "D91EAD25-4857-1916-0E70-B285D84A2B6C";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[112]";
createNode polyTweakUV -n "polyTweakUV11";
	rename -uid "BA187477-4EA3-3D38-F585-91A4F41909E6";
	setAttr ".uopa" yes;
	setAttr -s 22 ".uvtk[56:77]" -type "float2" -0.00023340779 2.9802322e-08
		 -0.00023340779 0 5.9604645e-08 7.1853399e-05 -5.9604645e-08 -7.3701143e-05 -0.045186274
		 0.0052652978 -0.076430857 -0.033492621 0.0013324715 -0.094581112 0.032577045 -0.055823144
		 0.042982318 0.027935244 0.011737687 -0.011125022 0.089307204 -0.072213277 0.12055184
		 -0.033153079 -0.052351881 0.044239089 -0.021107225 0.0055811866 0.056424569 0.066669442
		 0.025180006 0.10532735 -0.00023340779 0 -0.00023340779 0 -0.00023340779 7.3701143e-05
		 -0.00023340779 0 -0.00023340779 0 -0.00023340779 -7.1853399e-05;
createNode polyMapSew -n "polyMapSew3";
	rename -uid "E9256CE9-4607-D41B-2989-D28BBF9D73B7";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[116:117]" "e[121]";
createNode polyTweakUV -n "polyTweakUV12";
	rename -uid "A5E3C766-468D-EEB4-F753-9B8571E25B56";
	setAttr ".uopa" yes;
	setAttr -s 39 ".uvtk";
	setAttr ".uvtk[56]" -type "float2" 3.3557415e-05 -3.8981438e-05 ;
	setAttr ".uvtk[57]" -type "float2" 0.00015944242 0 ;
	setAttr ".uvtk[58]" -type "float2" 0.00038506128 0 ;
	setAttr ".uvtk[59]" -type "float2" 0.00042839386 -3.9041042e-05 ;
	setAttr ".uvtk[60]" -type "float2" -0.00015944242 -5.9604645e-08 ;
	setAttr ".uvtk[61]" -type "float2" -0.00015002489 -0.0002655659 ;
	setAttr ".uvtk[62]" -type "float2" 0.00031717159 -0.0002655063 ;
	setAttr ".uvtk[63]" -type "float2" 0.00032658913 0 ;
	setAttr ".uvtk[64]" -type "float2" -0.00011122227 0.00057431124 ;
	setAttr ".uvtk[65]" -type "float2" 0.00049050187 0.00057437085 ;
	setAttr ".uvtk[68]" -type "float2" 0 3.9070845e-05 ;
	setAttr ".uvtk[72]" -type "float2" -0.081086427 0.025258474 ;
	setAttr ".uvtk[73]" -type "float2" -0.0086227804 -0.044938147 ;
	setAttr ".uvtk[74]" -type "float2" 0.10811599 0.077435613 ;
	setAttr ".uvtk[75]" -type "float2" 0.035652123 0.14763221 ;
	setAttr ".uvtk[76]" -type "float2" -0.12339635 0.16462016 ;
	setAttr ".uvtk[77]" -type "float2" -0.19586027 0.094485208 ;
	setAttr ".uvtk[78]" -type "float2" -0.079199702 -0.027888618 ;
	setAttr ".uvtk[79]" -type "float2" -0.006735947 0.042246345 ;
	setAttr ".uvtk[80]" -type "float2" 0.065781854 0.21787788 ;
	setAttr ".uvtk[81]" -type "float2" -0.0066819298 0.14787047 ;
	setAttr ".uvtk[82]" -type "float2" 0.10974213 0.025496652 ;
	setAttr ".uvtk[83]" -type "float2" 0.18220603 0.095504105 ;
	setAttr ".uvtk[84]" -type "float2" -0.15526345 0.23658733 ;
	setAttr ".uvtk[85]" -type "float2" -0.082799613 0.1656187 ;
	setAttr ".uvtk[86]" -type "float2" 0.033958472 0.28799254 ;
	setAttr ".uvtk[87]" -type "float2" -0.038505405 0.35896116 ;
	setAttr ".uvtk[88]" -type "float2" 0.1382333 0.16458905 ;
	setAttr ".uvtk[89]" -type "float2" 0.065769494 0.23555776 ;
	setAttr ".uvtk[90]" -type "float2" -0.0051993509 0.16309389 ;
	setAttr ".uvtk[91]" -type "float2" 0.067264438 0.09212514 ;
	setAttr ".uvtk[92]" -type "float2" -0.046095494 -0.047781687 ;
	setAttr ".uvtk[93]" -type "float2" -0.047155742 -0.047781687 ;
	setAttr ".uvtk[94]" -type "float2" -0.047742132 -0.048368018 ;
	setAttr ".uvtk[95]" -type "float2" -0.045509163 -0.048368018 ;
	setAttr ".uvtk[96]" -type "float2" -0.047155742 -0.04672138 ;
	setAttr ".uvtk[97]" -type "float2" -0.047742132 -0.046135079 ;
	setAttr ".uvtk[98]" -type "float2" -0.046095494 -0.04672138 ;
	setAttr ".uvtk[99]" -type "float2" -0.045509163 -0.046135079 ;
createNode polyPinUV -n "polyPinUV1";
	rename -uid "5F8E36F9-4A22-553E-3389-749CAB31E874";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[56:63]" "map[66:71]";
	setAttr -s 14 ".pn";
	setAttr ".pn[56]" 1;
	setAttr ".pn[57]" 1;
	setAttr ".pn[58]" 1;
	setAttr ".pn[59]" 1;
	setAttr ".pn[60]" 1;
	setAttr ".pn[61]" 1;
	setAttr ".pn[62]" 1;
	setAttr ".pn[63]" 1;
	setAttr ".pn[66]" 1;
	setAttr ".pn[67]" 1;
	setAttr ".pn[68]" 1;
	setAttr ".pn[69]" 1;
	setAttr ".pn[70]" 1;
	setAttr ".pn[71]" 1;
createNode polyPinUV -n "polyPinUV2";
	rename -uid "CA2DD3A7-4484-DD27-869D-9D988842D671";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV3";
	rename -uid "31F50E49-476C-4B45-5D2B-769911A2ED34";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "map[56:57]" "map[66:71]" "map[92:99]";
	setAttr -s 16 ".pn";
	setAttr ".pn[56]" 1;
	setAttr ".pn[57]" 1;
	setAttr ".pn[66]" 1;
	setAttr ".pn[67]" 1;
	setAttr ".pn[68]" 1;
	setAttr ".pn[69]" 1;
	setAttr ".pn[70]" 1;
	setAttr ".pn[71]" 1;
	setAttr ".pn[92]" 1;
	setAttr ".pn[93]" 1;
	setAttr ".pn[94]" 1;
	setAttr ".pn[95]" 1;
	setAttr ".pn[96]" 1;
	setAttr ".pn[97]" 1;
	setAttr ".pn[98]" 1;
	setAttr ".pn[99]" 1;
createNode polyPinUV -n "polyPinUV4";
	rename -uid "7528B40B-4EE2-32F8-0F90-71A8896C7420";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew4";
	rename -uid "66465B2B-414D-2E00-7315-E3B36561A93B";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "e[128]" "e[132:133]" "e[137:138]";
createNode polyTweakUV -n "polyTweakUV13";
	rename -uid "908C76D8-42A9-69D4-4E86-6DA0F3AA428E";
	setAttr ".uopa" yes;
	setAttr -s 62 ".uvtk";
	setAttr ".uvtk[0]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[1]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[2]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[3]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[4]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[5]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[6]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[7]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[8]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[9]" -type "float2" 0.06259077 0 ;
	setAttr ".uvtk[10]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[11]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[32]" -type "float2" 0.028058143 -0.017274691 ;
	setAttr ".uvtk[33]" -type "float2" 0.03623018 -0.017274691 ;
	setAttr ".uvtk[34]" -type "float2" 0.03623018 0.0066700568 ;
	setAttr ".uvtk[35]" -type "float2" 0.028058143 0.0066700568 ;
	setAttr ".uvtk[36]" -type "float2" 0.050894208 0.10762163 ;
	setAttr ".uvtk[37]" -type "float2" 0.028196009 0.10762163 ;
	setAttr ".uvtk[38]" -type "float2" 0.028196009 0.053572636 ;
	setAttr ".uvtk[39]" -type "float2" 0.050894208 0.053572636 ;
	setAttr ".uvtk[40]" -type "float2" 0.041023463 0.06894017 ;
	setAttr ".uvtk[41]" -type "float2" 0.041023463 0.044696331 ;
	setAttr ".uvtk[42]" -type "float2" 0.04931578 0.044696331 ;
	setAttr ".uvtk[43]" -type "float2" 0.04931578 0.06894017 ;
	setAttr ".uvtk[44]" -type "float2" 0.036011677 0.085140273 ;
	setAttr ".uvtk[45]" -type "float2" 0.036011945 0.1543088 ;
	setAttr ".uvtk[46]" -type "float2" 0.013168203 0.1543088 ;
	setAttr ".uvtk[47]" -type "float2" 0.013167934 0.085140273 ;
	setAttr ".uvtk[56]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[57]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[58]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[59]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[60]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[61]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[62]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[63]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[64]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[65]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[66]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[67]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[68]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[69]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[70]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[71]" -type "float2" 0.062590741 0 ;
	setAttr ".uvtk[72]" -type "float2" 0.062345788 -3.9041042e-05 ;
	setAttr ".uvtk[73]" -type "float2" 0.062429532 -2.9802322e-08 ;
	setAttr ".uvtk[74]" -type "float2" 0.062840462 -7.4744225e-05 ;
	setAttr ".uvtk[75]" -type "float2" 0.06289953 -0.00011712313 ;
	setAttr ".uvtk[76]" -type "float2" 0.062424704 0.00037691343 ;
	setAttr ".uvtk[77]" -type "float2" 0.062316522 0.00010366703 ;
	setAttr ".uvtk[78]" -type "float2" 0.062978446 0.00010366703 ;
	setAttr ".uvtk[79]" -type "float2" 0.0629704 0.00037911881 ;
	setAttr ".uvtk[80]" -type "float2" 0.062394843 -0.00026923936 ;
	setAttr ".uvtk[81]" -type "float2" 0.062956512 -0.00027812045 ;
	setAttr ".uvtk[82]" -type "float2" 0.062220201 0.00038358915 ;
	setAttr ".uvtk[83]" -type "float2" 0.062220201 -0.00028706115 ;
	setAttr ".uvtk[84]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[85]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[86]" -type "float2" 0.062373027 0.00011712313 ;
	setAttr ".uvtk[87]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[88]" -type "float2" 0.062373027 0 ;
	setAttr ".uvtk[89]" -type "float2" 0.062373027 7.4714422e-05 ;
createNode polyPinUV -n "polyPinUV5";
	rename -uid "8A9C1B19-4F6C-25DC-BC26-FA98432A8918";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[72:81]" "map[84:89]";
	setAttr -s 16 ".pn";
	setAttr ".pn[72]" 1;
	setAttr ".pn[73]" 1;
	setAttr ".pn[74]" 1;
	setAttr ".pn[75]" 1;
	setAttr ".pn[76]" 1;
	setAttr ".pn[77]" 1;
	setAttr ".pn[78]" 1;
	setAttr ".pn[79]" 1;
	setAttr ".pn[80]" 1;
	setAttr ".pn[81]" 1;
	setAttr ".pn[84]" 1;
	setAttr ".pn[85]" 1;
	setAttr ".pn[86]" 1;
	setAttr ".pn[87]" 1;
	setAttr ".pn[88]" 1;
	setAttr ".pn[89]" 1;
createNode polyPinUV -n "polyPinUV6";
	rename -uid "09301877-4BA0-169E-EF5A-DB9268BC5C2D";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[76]" "map[79:81]";
	setAttr -s 4 ".pn";
	setAttr ".pn[76]" 0;
	setAttr ".pn[79]" 0;
	setAttr ".pn[80]" 0;
	setAttr ".pn[81]" 0;
	setAttr ".op" 1;
createNode polyPinUV -n "polyPinUV7";
	rename -uid "83E31CED-4869-1933-2DD7-56B212E8760D";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV8";
	rename -uid "10625AE2-4DA9-69D9-4391-F48189335C88";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 4 "map[72:73]" "map[76:77]" "map[80]" "map[84:89]";
	setAttr -s 11 ".pn";
	setAttr ".pn[72]" 1;
	setAttr ".pn[73]" 1;
	setAttr ".pn[76]" 1;
	setAttr ".pn[77]" 1;
	setAttr ".pn[80]" 1;
	setAttr ".pn[84]" 1;
	setAttr ".pn[85]" 1;
	setAttr ".pn[86]" 1;
	setAttr ".pn[87]" 1;
	setAttr ".pn[88]" 1;
	setAttr ".pn[89]" 1;
createNode polyPinUV -n "polyPinUV9";
	rename -uid "4EFF2EFF-44D3-A3B6-B918-4A8C52EE86B3";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew5";
	rename -uid "169D6A00-47E6-5C09-08EC-D198C70C1749";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[146:147]" "e[150]";
createNode polyTweakUV -n "polyTweakUV14";
	rename -uid "0984821E-4B86-577C-78E9-CE977181B37B";
	setAttr ".uopa" yes;
	setAttr -s 18 ".uvtk[32:49]" -type "float2" -0.00010278821 0 1.8358231e-05
		 0 1.9073486e-05 0 -4.1931868e-05 0 4.8875809e-06 0 6.4074993e-05 0 2.1994114e-05
		 0 2.9206276e-05 0 -2.9176474e-05 0 0.00010278821 0 0.0094381087 -0.016960129 0.056444615
		 0.0057506561 0.041368954 0.044854332 -0.0056377426 0.022143383 0.088135041 0.10775155
		 0.088135101 0.17716628 0.033901405 0.17716628 0.033901405 0.10775155;
createNode polyMapSew -n "polyMapSew6";
	rename -uid "8A41EB14-4FFB-8223-D83C-4E9BAF050DB0";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[143]" "e[145]";
createNode polyTweakUV -n "polyTweakUV15";
	rename -uid "51D798A9-4BB9-1A8C-A0F3-49903560ECAE";
	setAttr ".uopa" yes;
	setAttr -s 38 ".uvtk[8:45]" -type "float2" -0.0001641546 0 -0.0001641546
		 0 -0.0001641546 0 -0.0001641546 0 0.11978044 -0.037606455 0.14506057 2.3874221e-05
		 0.11630798 0.020740023 0.092107959 -0.01528248 0.11511426 0.018140394 0.089781985
		 0.055770796 0.062112998 0.033405919 0.086362898 -0.0026165321 0.053798713 -0.0055807494
		 0.04470364 -0.013313321 0.069007002 -0.049335927 0.078102075 -0.041603357 0.0051304409
		 0.032368556 -0.0039645704 0.024701623 0.020677617 -0.01292862 0.029772623 -0.0052617923
		 0.038109999 -0.0066170292 0.02901499 -0.014257747 0.04836379 -0.043658517 0.057458803
		 -0.036017753 -7.1525574e-06 -3.0604118e-05 -1.3500452e-05 4.0206192e-05 -1.3500452e-05
		 6.5001725e-05 -7.212162e-06 -3.0723328e-05 1.3500452e-05 0.0078543387 7.1823597e-06
		 0.0077754804 7.1823597e-06 0.0078543387 -1.3500452e-05 0.0078543387 1.3500452e-05
		 0.015772389 7.1823597e-06 0.015759336 -0.0073273331 -0.00010147404 -0.0073272139
		 -0.00012626957 2.9802322e-08 0.0079331957 -2.9802322e-08 0.015785502;
createNode polyPinUV -n "polyPinUV10";
	rename -uid "4CF77FB6-4D0C-2EF2-692E-05915F0AF103";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[32:45]";
	setAttr -s 14 ".pn[32:45]"  1 1 1 1 1 1 1 1 1 1 1 1 1 1;
createNode polyPinUV -n "polyPinUV11";
	rename -uid "45E540EF-4704-FAFC-ABC6-B3BBCCD1CC18";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[34:41]" "map[44:45]";
	setAttr -s 10 ".pn";
	setAttr ".pn[34]" 0;
	setAttr ".pn[35]" 0;
	setAttr ".pn[36]" 0;
	setAttr ".pn[37]" 0;
	setAttr ".pn[38]" 0;
	setAttr ".pn[39]" 0;
	setAttr ".pn[40]" 0;
	setAttr ".pn[41]" 0;
	setAttr ".pn[44]" 0;
	setAttr ".pn[45]" 0;
	setAttr ".op" 1;
createNode polyPinUV -n "polyPinUV12";
	rename -uid "A568AF73-4B08-10C8-111F-EAB312133946";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[32:39]" "map[42:43]";
	setAttr -s 10 ".pn";
	setAttr ".pn[32]" 1;
	setAttr ".pn[33]" 1;
	setAttr ".pn[34]" 1;
	setAttr ".pn[35]" 1;
	setAttr ".pn[36]" 1;
	setAttr ".pn[37]" 1;
	setAttr ".pn[38]" 1;
	setAttr ".pn[39]" 1;
	setAttr ".pn[42]" 1;
	setAttr ".pn[43]" 1;
createNode polyPinUV -n "polyPinUV13";
	rename -uid "688E1E77-4149-D17D-16C8-0F8AA8A69C88";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyAutoProj -n "polyAutoProj10";
	rename -uid "BA0361D5-4F0C-3AEB-1DD1-089FBBBA94B6";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[28:31]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.648554727435112 0.648554727435112 0.648554727435112 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV16";
	rename -uid "164C8327-495D-8CEC-86B4-A3B3B2543D43";
	setAttr ".uopa" yes;
	setAttr -s 16 ".uvtk[160:175]" -type "float2" -0.12607425 -0.23097338 -0.12607425
		 0.39716166 -0.2451413 0.39716166 -0.2451413 -0.23097338 -0.59783733 -0.23087527 -0.59783733
		 0.39698598 -0.71671855 0.39698598 -0.71671855 -0.23087527 0.13501425 -0.2309027 0.22036225
		 0.39716202 0.083245456 0.39716202 -0.12268893 -0.2309027 -0.25036246 -0.23095773
		 -0.45629647 0.39710617 -0.59341329 0.39710617 -0.50806534 -0.23095773;
createNode polyAutoProj -n "polyAutoProj11";
	rename -uid "20AA47AB-47ED-3262-457A-0AB11885C434";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[22:27]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.27078300714492798 0.27078300714492798 0.27078300714492798 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV17";
	rename -uid "8F895A52-4745-6750-A0BC-40A825B58522";
	setAttr ".uopa" yes;
	setAttr -s 24 ".uvtk[176:199]" -type "float2" 0.20905049 -0.33253604 -0.10487805
		 -0.33253604 -0.10487805 -0.64646453 0.20905049 -0.64646453 -0.11231734 -0.28586528
		 -0.45719099 -0.28586528 -0.45719099 -0.63073903 -0.11231734 -0.63073903 -0.12767223
		 0.36515108 -0.4571521 0.34943971 -0.44183609 0.0199598 -0.11235628 0.035671189 -0.056023471
		 0.047682442 -0.38550332 0.063076213 -0.40089706 -0.2664037 -0.071417257 -0.28179747
		 0.19360475 0.33425054 -0.12063646 0.33425054 -0.12063646 0.019931041 0.19360475 0.019931041
		 -0.37566125 0.016208582 -0.72053468 0.016208641 -0.72053468 -0.32866496 -0.37566125
		 -0.32866502;
createNode polyMapSew -n "polyMapSew7";
	rename -uid "90D054CD-4CA5-B956-88F3-08ACA4D005A4";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[49:51]" "e[53:54]";
createNode polyTweakUV -n "polyTweakUV18";
	rename -uid "A4BA2C17-4589-D9CB-2B8A-298391340E50";
	setAttr ".uopa" yes;
	setAttr -s 130 ".uvtk";
	setAttr ".uvtk[12]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[13]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[14]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[15]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[16]" -type "float2" 0 0.0078412481 ;
	setAttr ".uvtk[17]" -type "float2" 0 0.0078412481 ;
	setAttr ".uvtk[18]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[19]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[20]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[21]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[22]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[23]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[24]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[25]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[26]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[27]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[28]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[29]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[30]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[31]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[80]" -type "float2" -0.049568087 0.11393483 ;
	setAttr ".uvtk[81]" -type "float2" -0.046351731 0.1140167 ;
	setAttr ".uvtk[82]" -type "float2" -0.046351731 0.11467184 ;
	setAttr ".uvtk[83]" -type "float2" -0.049568087 0.11458994 ;
	setAttr ".uvtk[84]" -type "float2" -0.04968759 -0.027336244 ;
	setAttr ".uvtk[85]" -type "float2" -0.046472695 -0.027418021 ;
	setAttr ".uvtk[86]" -type "float2" -0.046472754 -0.026763175 ;
	setAttr ".uvtk[87]" -type "float2" -0.04968759 -0.026681367 ;
	setAttr ".uvtk[88]" -type "float2" -0.046265017 0.063756466 ;
	setAttr ".uvtk[89]" -type "float2" -0.046265017 0.061957754 ;
	setAttr ".uvtk[90]" -type "float2" -0.049492847 0.061957784 ;
	setAttr ".uvtk[91]" -type "float2" -0.049492847 0.063756466 ;
	setAttr ".uvtk[92]" -type "float2" -0.046385497 0.11493115 ;
	setAttr ".uvtk[93]" -type "float2" -0.046385556 0.11322069 ;
	setAttr ".uvtk[94]" -type "float2" -0.049601972 0.11322066 ;
	setAttr ".uvtk[95]" -type "float2" -0.049601972 0.11493112 ;
	setAttr ".uvtk[96]" -type "float2" 0.21688367 0.070306823 ;
	setAttr ".uvtk[97]" -type "float2" 0.17011417 0.029383421 ;
	setAttr ".uvtk[98]" -type "float2" 0.21245559 -0.017544053 ;
	setAttr ".uvtk[99]" -type "float2" 0.25922495 0.023379318 ;
	setAttr ".uvtk[100]" -type "float2" 0.087511934 0.14781778 ;
	setAttr ".uvtk[101]" -type "float2" 0.087511934 0.13598143 ;
	setAttr ".uvtk[102]" -type "float2" 0.10496109 0.1438527 ;
	setAttr ".uvtk[103]" -type "float2" 0.10496648 0.15352421 ;
	setAttr ".uvtk[104]" -type "float2" 0.086358815 -0.042135477 ;
	setAttr ".uvtk[105]" -type "float2" 0.086358815 0.05449228 ;
	setAttr ".uvtk[106]" -type "float2" -0.056134671 0.10107765 ;
	setAttr ".uvtk[107]" -type "float2" -0.056090191 0.022122607 ;
	setAttr ".uvtk[108]" -type "float2" 0.045896292 0.02784525 ;
	setAttr ".uvtk[109]" -type "float2" -0.0065196324 0.066902332 ;
	setAttr ".uvtk[110]" -type "float2" -0.05876115 -0.020838546 ;
	setAttr ".uvtk[111]" -type "float2" -0.027192676 -0.044361509 ;
	setAttr ".uvtk[112]" -type "float2" 0.046746191 -0.17688066 ;
	setAttr ".uvtk[113]" -type "float2" 0.099161997 -0.13787191 ;
	setAttr ".uvtk[114]" -type "float2" 0.026681611 -0.065680526 ;
	setAttr ".uvtk[115]" -type "float2" -0.004886874 -0.089174338 ;
	setAttr ".uvtk[116]" -type "float2" 0.095476016 0.24190791 ;
	setAttr ".uvtk[117]" -type "float2" 0.13977945 0.32561946 ;
	setAttr ".uvtk[118]" -type "float2" 0.078972891 0.37906224 ;
	setAttr ".uvtk[119]" -type "float2" 0.0075663994 0.27727205 ;
	setAttr ".uvtk[120]" -type "float2" 0.063730434 0.061889909 ;
	setAttr ".uvtk[121]" -type "float2" 0.019425301 0.14560173 ;
	setAttr ".uvtk[122]" -type "float2" -0.068484947 0.1102358 ;
	setAttr ".uvtk[123]" -type "float2" 0.0029240185 0.0084454939 ;
	setAttr ".uvtk[124]" -type "float2" -0.29317728 -0.045255553 ;
	setAttr ".uvtk[125]" -type "float2" -0.29317728 -0.045255553 ;
	setAttr ".uvtk[126]" -type "float2" -0.29317728 -0.045255553 ;
	setAttr ".uvtk[127]" -type "float2" -0.29317728 -0.045255553 ;
	setAttr ".uvtk[128]" -type "float2" 0.12067341 0.17820898 ;
	setAttr ".uvtk[129]" -type "float2" 0.082995735 0.20877317 ;
	setAttr ".uvtk[130]" -type "float2" 0.02064996 0.14000079 ;
	setAttr ".uvtk[131]" -type "float2" 0.066237636 0.10302004 ;
	setAttr ".uvtk[132]" -type "float2" 0.11311636 0.17490421 ;
	setAttr ".uvtk[133]" -type "float2" 0.067528643 0.21221207 ;
	setAttr ".uvtk[134]" -type "float2" 0.0071806163 0.1420878 ;
	setAttr ".uvtk[135]" -type "float2" 0.052768338 0.10477999 ;
	setAttr ".uvtk[136]" -type "float2" -0.29317728 -0.045255553 ;
	setAttr ".uvtk[137]" -type "float2" -0.29317728 -0.045255553 ;
	setAttr ".uvtk[138]" -type "float2" -0.29317728 -0.04525556 ;
	setAttr ".uvtk[139]" -type "float2" -0.29317728 -0.04525556 ;
	setAttr ".uvtk[140]" -type "float2" 0.3797532 0.096414015 ;
	setAttr ".uvtk[141]" -type "float2" 0.3797532 0.096414 ;
	setAttr ".uvtk[142]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[143]" -type "float2" 0.3797532 0.096414015 ;
	setAttr ".uvtk[144]" -type "float2" 0.3797532 0.096414015 ;
	setAttr ".uvtk[145]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[146]" -type "float2" 0.3797532 0.096414015 ;
	setAttr ".uvtk[147]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[148]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[149]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[150]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[151]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[152]" -type "float2" 0.3797532 0.096414015 ;
	setAttr ".uvtk[153]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[154]" -type "float2" 0.37975314 0.096414 ;
	setAttr ".uvtk[155]" -type "float2" 0.3797532 0.096414 ;
	setAttr ".uvtk[156]" -type "float2" 0.37975314 0.096414 ;
	setAttr ".uvtk[157]" -type "float2" 0.37975314 0.096414015 ;
	setAttr ".uvtk[158]" -type "float2" 0.3797532 0.096414015 ;
	setAttr ".uvtk[159]" -type "float2" 0.3797532 0.096414 ;
	setAttr ".uvtk[160]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[161]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[162]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[163]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[164]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[165]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[166]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[167]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[168]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[169]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[170]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[171]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[172]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[173]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[174]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[175]" -type "float2" 0 0.0078412183 ;
	setAttr ".uvtk[176]" -type "float2" -0.023346502 0.038855366 ;
	setAttr ".uvtk[177]" -type "float2" -0.023452111 0.038939141 ;
	setAttr ".uvtk[178]" -type "float2" -0.023385622 0.039147712 ;
	setAttr ".uvtk[179]" -type "float2" -0.023411576 0.039169647 ;
	setAttr ".uvtk[180]" -type "float2" -0.023264207 0.03909155 ;
	setAttr ".uvtk[181]" -type "float2" -0.023475546 0.03909158 ;
	setAttr ".uvtk[182]" -type "float2" -0.023522306 0.039043963 ;
	setAttr ".uvtk[183]" -type "float2" -0.023272045 0.039043963 ;
	setAttr ".uvtk[184]" -type "float2" -0.023236878 0.039186932 ;
	setAttr ".uvtk[185]" -type "float2" -0.023448277 0.039186962 ;
	setAttr ".uvtk[186]" -type "float2" -0.023681771 0.0387301 ;
	setAttr ".uvtk[187]" -type "float2" -0.023681771 0.039226152 ;
	setAttr ".uvtk[188]" -type "float2" -0.023234766 0.039160408 ;
	setAttr ".uvtk[189]" -type "float2" -0.023234766 0.038897745 ;
createNode polyPinUV -n "polyPinUV14";
	rename -uid "028F5E31-4CDE-13F2-666C-B88EEC623547";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[177:185]" "map[188:189]";
	setAttr -s 11 ".pn";
	setAttr ".pn[177]" 1;
	setAttr ".pn[178]" 1;
	setAttr ".pn[179]" 1;
	setAttr ".pn[180]" 1;
	setAttr ".pn[181]" 1;
	setAttr ".pn[182]" 1;
	setAttr ".pn[183]" 1;
	setAttr ".pn[184]" 1;
	setAttr ".pn[185]" 1;
	setAttr ".pn[188]" 1;
	setAttr ".pn[189]" 1;
createNode polyPinUV -n "polyPinUV15";
	rename -uid "5D117219-401B-9EA6-DA0B-5A8500F87432";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV16";
	rename -uid "D73F68EE-4F6E-FB94-28B3-1C94D2E48F42";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[178:185]" "map[187:188]";
	setAttr -s 10 ".pn";
	setAttr ".pn[178]" 1;
	setAttr ".pn[179]" 1;
	setAttr ".pn[180]" 1;
	setAttr ".pn[181]" 1;
	setAttr ".pn[182]" 1;
	setAttr ".pn[183]" 1;
	setAttr ".pn[184]" 1;
	setAttr ".pn[185]" 1;
	setAttr ".pn[187]" 1;
	setAttr ".pn[188]" 1;
createNode polyPinUV -n "polyPinUV17";
	rename -uid "3E1D419A-43B4-C606-B724-2CBC0F8AB424";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew8";
	rename -uid "0C23D3E9-401B-0560-E89F-16A8AD05574B";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[165]";
createNode polyTweakUV -n "polyTweakUV19";
	rename -uid "F3CE157C-4DFF-426C-060B-B2AECA31600E";
	setAttr ".uopa" yes;
	setAttr -s 8 ".uvtk";
	setAttr ".uvtk[124]" -type "float2" 0.36440229 0.31713358 ;
	setAttr ".uvtk[125]" -type "float2" 0.31881461 0.35435304 ;
	setAttr ".uvtk[126]" -type "float2" 0.24429849 0.25501001 ;
	setAttr ".uvtk[127]" -type "float2" 0.28197616 0.22424853 ;
	setAttr ".uvtk[130]" -type "float2" -0.0048817853 5.7667494e-05 ;
	setAttr ".uvtk[131]" -type "float2" -0.0048817704 -2.4080276e-05 ;
	setAttr ".uvtk[132]" -type "float2" -0.0048818076 -5.7667494e-05 ;
	setAttr ".uvtk[133]" -type "float2" -0.0048818076 2.4110079e-05 ;
createNode polyMapCut -n "polyMapCut1";
	rename -uid "24B7E2FD-4A7C-CB8B-1039-879BDBAAEC56";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[165:166]" "e[169:170]";
createNode polyTweakUV -n "polyTweakUV20";
	rename -uid "9E348F33-47B3-574D-AD19-F9A2FBEF948F";
	setAttr ".uopa" yes;
	setAttr -s 44 ".uvtk";
	setAttr ".uvtk[80]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[81]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[82]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[83]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[84]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[85]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[86]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[87]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[88]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[89]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[90]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[91]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[92]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[93]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[94]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[95]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[96]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[97]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[98]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[99]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[100]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[101]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[102]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[103]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[104]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[105]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[106]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[107]" -type "float2" 0.16318433 0 ;
	setAttr ".uvtk[108]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[109]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[110]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[111]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[112]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[113]" -type "float2" 0.16318434 0 ;
	setAttr ".uvtk[114]" -type "float2" 0.16318433 0 ;
	setAttr ".uvtk[115]" -type "float2" 0.16318433 0 ;
	setAttr ".uvtk[131]" -type "float2" 6.7850138e-05 0 ;
	setAttr ".uvtk[132]" -type "float2" -0.0058435011 0 ;
	setAttr ".uvtk[133]" -type "float2" -0.0058435011 0 ;
	setAttr ".uvtk[134]" -type "float2" 0.55135936 0.15270048 ;
	setAttr ".uvtk[135]" -type "float2" 0.51368171 0.18279892 ;
	setAttr ".uvtk[136]" -type "float2" 0.4635956 0.1201003 ;
	setAttr ".uvtk[137]" -type "float2" 0.50127321 0.090001926 ;
	setAttr ".uvtk[188]" -type "float2" 6.7850138e-05 0 ;
createNode polyMapSew -n "polyMapSew9";
	rename -uid "827A5554-4C21-BF95-FFDB-80ADFAEA3F23";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[165:166]";
createNode polyTweakUV -n "polyTweakUV21";
	rename -uid "55E530DB-42B9-5FC9-410F-73972C4C23C2";
	setAttr ".uopa" yes;
	setAttr -s 10 ".uvtk";
	setAttr ".uvtk[124]" -type "float2" 0.0089579169 0 ;
	setAttr ".uvtk[125]" -type "float2" 0.0089579169 0 ;
	setAttr ".uvtk[126]" -type "float2" 0.016915951 0 ;
	setAttr ".uvtk[127]" -type "float2" 0.016915951 0 ;
	setAttr ".uvtk[130]" -type "float2" 0.0047146953 0 ;
	setAttr ".uvtk[131]" -type "float2" 0.0047146953 0 ;
	setAttr ".uvtk[132]" -type "float2" 0.0014074838 0 ;
	setAttr ".uvtk[133]" -type "float2" 0.0014074838 0 ;
	setAttr ".uvtk[134]" -type "float2" 0.0014074987 0 ;
	setAttr ".uvtk[135]" -type "float2" 0.0014074987 0 ;
createNode polyPinUV -n "polyPinUV18";
	rename -uid "22EA769A-4A8D-3549-CDA6-609423BEE754";
	setAttr ".uopa" yes;
	setAttr ".op" 3;
createNode polyPinUV -n "polyPinUV19";
	rename -uid "811A3483-4395-2734-C167-DDAF14B04489";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV20";
	rename -uid "FD9AFE7C-43D0-17B5-5669-308F2DE3515A";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[132:135]";
	setAttr -s 4 ".pn[132:135]"  1 1 1 1;
createNode polyPinUV -n "polyPinUV21";
	rename -uid "C83C4138-4875-4154-FEEC-FD910D32E0EF";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapCut -n "polyMapCut2";
	rename -uid "472FE0AF-453F-6236-9C34-DA94F168CFA5";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[165:166]" "e[169:170]";
createNode polyTweakUV -n "polyTweakUV22";
	rename -uid "BA09D57C-45DB-10A3-35D6-FA84B1A587E6";
	setAttr ".uopa" yes;
	setAttr -s 190 ".uvtk[0:189]" -type "float2" 0 0.24995746 0 0.24995746
		 0 0.24995743 0 0.24995743 0 0.2499574 0 0.2499574 0 0.2499574 0 0.2499574 0 0.24995746
		 0 0.24995743 0 0.24995749 0 0.24995746 0 0.24995749 0 0.24995749 0 0.2499574 0 0.2499574
		 0 0.24995746 0 0.24995746 0 0.24995746 0 0.24995755 0 0.24995746 0 0.24995749 0 0.24995749
		 0 0.24995746 0 0.2499574 0 0.24995749 0 0.24995746 0 0.24995746 0 0.2499574 0 0.2499574
		 0 0.24995743 0 0.24995752 0 0.24995746 0 0.24995746 0 0.24995746 0 0.24995746 0 0.24995746
		 0 0.24995746 0 0.2499574 0 0.2499574 0 0.24995746 0 0.24995746 0 0.24995746 0 0.24995746
		 0 0.24995746 0 0.24995746 0 0.24995746 0 0.24995749 0 0.24995749 0 0.24995746 0 0.2499574
		 0 0.24995746 0 0.24995746 0 0.2499574 0 0.2499574 0 0.2499574 0 0.24995749 0 0.24995749
		 0 0.24995746 0 0.24995749 0 0.24995749 0 0.24995749 0 0.24995746 0 0.24995755 0 0.24995755
		 0 0.24995746 0 0.2499574 0 0.24995746 0 0.24995746 0 0.2499574 0 0.2499574 0 0.2499574
		 0 0.2499574 0 0.2499574 0 0.24995743 0 0.24995743 0 0.24995746 0 0.24995749 0 0.24995749
		 0 0.24995755 -0.092352241 0.24995749 -0.092352182 0.24995755 -0.092352182 0.24995755
		 -0.092352211 0.24995755 -0.092352211 0.2499574 -0.092352182 0.24995746 -0.092352182
		 0.24995749 -0.092352211 0.24995746 -0.092352182 0.24995746 -0.092352182 0.24995746
		 -0.092352241 0.24995746 -0.092352241 0.24995746 -0.092352182 0.2499574 -0.092352182
		 0.24995746 -0.092352211 0.24995746 -0.092352241 0.2499574 -0.092352182 0.24995746
		 -0.092352182 0.2499574 -0.092352182 0.24995746 -0.092352182 0.24995755 -0.092352241
		 0.2499574 -0.092352241 0.2499574 -0.092352211 0.24995755 -0.092352211 0.24995755
		 -0.092352211 0.24995743 -0.092352211 0.24995743 -0.092352211 0.24995746 -0.092352211
		 0.24995746 -0.092352211 0.24995743 -0.092352211 0.24995752 -0.092352211 0.24995755
		 -0.092352241 0.24995749 -0.092352211 0.24995746 -0.092352241 0.2499574 -0.092352241
		 0.24995746 -0.092352241 0.24995743 0.015669206 0.24995755 0.015669206 0.24995749
		 0.015669206 0.24995752 0.015669206 0.24995746 0.015669206 0.24995743 0.015669206
		 0.2499574 0.015669206 0.24995746 0.015669206 0.24995746 0.0025026784 0.24995746 0.0099637099
		 0.24995755 0.0025026766 0.24995749 0.0025026766 0.24995749 0.015669206 0.24995749
		 0.015669206 0.24995749 0.015669206 0.24995749 0.0099637099 0.24995749 0.015669221
		 0.2499574 0.015669221 0.24995755 0.015669206 0.24995743 0.015669206 0.24995749 -0.53727722
		 0.051328395 -0.47541165 0.15144923 -0.49296361 0.1851604 -0.57245672 0.098369792
		 -0.52066886 0.0038909218 -0.58253187 0.10401121 -0.61771154 0.056971118 -0.53822118
		 -0.029819425 -0.54741031 0.10731254 -0.58165413 0.13842808 -0.66199166 0.056398075
		 -0.62774783 0.025282521 -0.64380926 0.024539746 -0.67805296 0.055780806 -0.71926415
		 0.014308818 -0.68502045 -0.016932242 -0.48913509 -0.017890897 -0.48913509 0.047539324
		 -0.54052573 0.047539324 -0.54052573 -0.017890897 0 0.24995749 0 0.24995752 0 0.24995752
		 0 0.24995749 0 0.24995749 0 0.24995749 0 0.24995749 0 0.24995749 0 0.24995746 0 0.24995749
		 0 0.24995749 0 0.24995746 0 0.24995755 0 0.24995755 0 0.24995755 0 0.24995755 0 0.2499574
		 0 0.2499574 0 0.24995746 0 0.24995746 0 0.2499574 0 0.2499574 0 0.24995746 0 0.24995746
		 0 0.24995746 0 0.24995746 0 0.2499574 0 0.24995746 0 0.24995746 0 0.2499574 0.0099637099
		 0.24995749 0.0025026784 0.24995755 0.015669206 0.24995749 0.0099637099 0.24995746;
createNode polyAutoProj -n "polyAutoProj12";
	rename -uid "0F2741C0-4B07-4683-781F-AD80B739E04C";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[34]" "f[36:47]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 0.75195562839508057 0.75195562839508057 0.75195562839508057 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweak -n "polyTweak13";
	rename -uid "9BE1AC6F-42D5-929C-2492-36B9E007545D";
	setAttr ".uopa" yes;
	setAttr -s 10 ".tk[92:101]" -type "float3"  0.029159628 0 0 -0.029159628
		 0 0 0.029159628 0 0 -0.029159628 0 0 0.029159628 0 0 -0.029159628 0 0 0.029159628
		 0 0 -0.029159628 0 0 0.029159628 0 0 -0.029159628 0 0;
createNode polyTweakUV -n "polyTweakUV23";
	rename -uid "0F8F9578-41CC-E897-5EB4-629D640C293C";
	setAttr ".uopa" yes;
	setAttr -s 34 ".uvtk[190:223]" -type "float2" -0.0060661063 -0.20908147
		 -0.094492137 -0.17429918 -0.246044 -0.4465746 -0.043333314 -0.4465746 0.11815645
		 0.056754671 0.029730238 0.056754671 0.068467461 0.19339955 -0.084554702 0.32010645
		 -0.40392092 -0.17429918 -0.49234709 -0.20908147 -0.45507976 -0.4465746 -0.25236914
		 -0.4465746 -0.52814341 0.056754671 -0.61656964 0.056754671 -0.41385838 0.32010645
		 -0.5668807 0.19339955 0.30816478 -0.20908147 0.22015072 -0.20908147 0.22015072 -0.4465746
		 0.30816478 -0.4465746 0.30816478 0.056754671 0.22015072 0.056754671 0.30816478 0.19339955
		 0.22015072 0.19339955 0.30816478 0.32010645 0.22015072 0.32010645 0.21306933 0.32010645
		 0.1250553 0.32010645 0.1250553 0.047831014 0.21306933 0.047831014 0.1250553 -0.18322268
		 0.21306933 -0.18322268 0.1250553 -0.44657442 0.21306933 -0.44657442;
createNode polyAutoProj -n "polyAutoProj13";
	rename -uid "4F582507-44B4-E464-9C64-D5973FEC6AE7";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[76:79]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.2696511745452881 1.2696511745452881 1.2696511745452881 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV24";
	rename -uid "8940FEC3-4DF4-E5D9-309F-9A9ECED106B6";
	setAttr ".uopa" yes;
	setAttr -s 46 ".uvtk[190:235]" -type "float2" 0.0078649027 0.018925168
		 0.004464129 0.020262871 -0.0013644018 0.0097914655 0.0064315917 0.0097914655 0.012642332
		 0.02914891 0.0092415595 0.02914891 0.010731348 0.034404129 0.0048463438 0.039277092
		 0.00026737619 0.020255063 -0.0031336332 0.01891727 -0.0017002905 0.0097828563 0.0060963556
		 0.0097828563 -0.0045104092 0.029141795 -0.0079114502 0.029141795 -0.00011480879 0.039270751
		 -0.0060002888 0.034397401 0.018048814 0.018905401 0.019032976 0.018905401 0.019032976
		 0.0098430943 0.018048814 0.0098430943 0.018048814 0.028630644 0.019032976 0.028630644
		 0.018048814 0.034577802 0.019032976 0.034577802 0.018048814 0.037714586 0.019032976
		 0.037714586 0.041818634 0.068822332 0.027331933 0.068822272 0.027331933 0.037033059
		 0.041818634 0.037033118 0.027331963 0.010939168 0.041818604 0.010939168 0.027331933
		 -0.020588171 0.041818634 -0.020588111 0.35199919 0.14358056 0.078292035 0.11975694
		 0.1268087 -0.43764666 0.40051588 -0.41382304 0.33529669 0.3354736 0.061589547 0.31164998
		 0.14828336 -0.68436593 0.42199051 -0.66054231 0.031244393 0.32640633 -0.22716215
		 0.32640633 -0.22716215 0.022277055 0.031244393 0.022277055;
createNode polyPinUV -n "polyPinUV22";
	rename -uid "DB23072B-47C0-9A39-4CED-4085A37ADF0B";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[206:215]";
	setAttr -s 10 ".pn[206:215]"  1 1 1 1 1 1 1 1 1 1;
createNode polyPinUV -n "polyPinUV23";
	rename -uid "2B7A3796-4CFC-AF21-D2CA-4E8E0810E633";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapCut -n "polyMapCut3";
	rename -uid "29CCF499-44FA-A0F9-8FF9-6EA173CD1715";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[155:164]";
createNode polyTweakUV -n "polyTweakUV25";
	rename -uid "43AE41CD-4167-77A4-F9FA-0291F5CC5FAB";
	setAttr ".uopa" yes;
	setAttr -s 16 ".uvtk[224:239]" -type "float2" -0.0095496941 0.0060676383
		 -0.0095011834 0.014426553 -0.026644008 0.014381682 -0.026655138 0.0061038435 -0.0082937721
		 0.0062344903 -0.0082938019 0.014426553 -0.032607406 0.014462113 -0.032607406 0.0061038136
		 -9.2840404e-05 0.01531738 -0.015393413 -0.00021260994 9.2828064e-05 -0.015540719
		 0.0153934 -1.0729011e-05 -0.026655108 0.014462143 -0.026644008 0.0060676681 -0.0095496643
		 0.014381682 -0.0095012132 0.0062344903;
createNode polyMapSew -n "polyMapSew10";
	rename -uid "568B92C7-4CD2-B7F9-D56F-2E9FE9236C0C";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "e[155]" "e[158]" "e[161]";
createNode polyTweakUV -n "polyTweakUV26";
	rename -uid "A1E361CC-4AC6-CA1B-5002-09B8D5AD4AF0";
	setAttr ".uopa" yes;
	setAttr -s 11 ".uvtk";
	setAttr ".uvtk[224]" -type "float2" 7.7994468e-05 1.6868114e-05 ;
	setAttr ".uvtk[225]" -type "float2" 7.7994468e-05 6.9826841e-05 ;
	setAttr ".uvtk[226]" -type "float2" 2.9802322e-08 5.2064657e-05 ;
	setAttr ".uvtk[227]" -type "float2" -2.9802322e-08 8.2224607e-05 ;
	setAttr ".uvtk[228]" -type "float2" -2.9802322e-08 -7.4386597e-05 ;
	setAttr ".uvtk[229]" -type "float2" 2.9802322e-08 -1.1235476e-05 ;
	setAttr ".uvtk[230]" -type "float2" 0 1.1771917e-05 ;
	setAttr ".uvtk[231]" -type "float2" -2.9802322e-08 6.4074993e-05 ;
	setAttr ".uvtk[232]" -type "float2" 0 -8.2194805e-05 ;
	setAttr ".uvtk[233]" -type "float2" 0 -6.9826841e-05 ;
createNode polyMapDel -n "polyMapDel2";
	rename -uid "6E01C04E-43F8-CF0E-42B6-B2B438F59B43";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[50]";
createNode polyExtrudeFace -n "polyExtrudeFace11";
	rename -uid "150FCE51-4F9A-BD53-F1E9-91A77A5F58AF";
	setAttr ".ics" -type "componentList" 1 "f[50]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.34076327 -5.7812481 ;
	setAttr ".rs" 52547;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.80293023586273193 -0.46216711401939392 -5.7812480926513672 ;
	setAttr ".cbx" -type "double3" 0.80293023586273193 1.1436936855316162 -5.7812480926513672 ;
	setAttr ".raf" no;
createNode polyExtrudeFace -n "polyExtrudeFace12";
	rename -uid "EE7F9BEF-4D60-9DF3-EE54-F1B60F306B9F";
	setAttr ".ics" -type "componentList" 1 "f[50]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".ws" yes;
	setAttr ".pvt" -type "float3" 0 0.34076333 -5.7812481 ;
	setAttr ".rs" 35820;
	setAttr ".c[0]"  0 1 1;
	setAttr ".cbn" -type "double3" -0.6223304271697998 -0.28156712651252747 -5.7812480926513672 ;
	setAttr ".cbx" -type "double3" 0.6223304271697998 0.96309375762939453 -5.7812480926513672 ;
	setAttr ".raf" no;
createNode polyTweak -n "polyTweak14";
	rename -uid "061B3965-44E6-E38A-729A-CFB5F98E184F";
	setAttr ".uopa" yes;
	setAttr -s 8 ".tk";
	setAttr ".tk[110]" -type "float3" 0.18059984 -0.18059996 1.4901161e-08 ;
	setAttr ".tk[111]" -type "float3" -0.18059984 -0.18059996 1.4901161e-08 ;
	setAttr ".tk[112]" -type "float3" -0.18059984 0.18059999 1.4901161e-08 ;
	setAttr ".tk[113]" -type "float3" 0.18059984 0.18059999 1.4901161e-08 ;
	setAttr ".tk[115]" -type "float3" 0 -7.4505806e-09 0 ;
createNode polyAutoProj -n "polyAutoProj14";
	rename -uid "4B55D1B9-47E9-9A92-CFE5-B29FA772DACE";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "f[86:89]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 1.6058607995510101 1.6058607995510101 1.6058607995510101 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweak -n "polyTweak15";
	rename -uid "E701D9DE-4C23-4C0E-43B1-B984739ABE89";
	setAttr ".uopa" yes;
	setAttr -s 6 ".tk";
	setAttr ".tk[114]" -type "float3" 0 0 2.5476904 ;
	setAttr ".tk[115]" -type "float3" 0 0 2.5476904 ;
	setAttr ".tk[116]" -type "float3" 0 0 2.5476904 ;
	setAttr ".tk[117]" -type "float3" 0 0 2.5476904 ;
createNode polyTweakUV -n "polyTweakUV27";
	rename -uid "B668BF4A-41C3-0449-B03A-118332685888";
	setAttr ".uopa" yes;
	setAttr -s 116 ".uvtk";
	setAttr ".uvtk[0]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[1]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[2]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[3]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[4]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[5]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[6]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[7]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[8]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[9]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[10]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[11]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[12]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[13]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[14]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[15]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[16]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[17]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[18]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[19]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[20]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[21]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[22]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[23]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[24]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[25]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[26]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[27]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[28]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[29]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[30]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[31]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[32]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[33]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[34]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[35]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[36]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[37]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[38]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[39]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[40]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[41]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[42]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[43]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[44]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[45]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[46]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[47]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[48]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[49]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[50]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[51]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[52]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[53]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[54]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[55]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[56]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[57]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[58]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[59]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[60]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[61]" -type "float2" -0.14061671 0 ;
	setAttr ".uvtk[62]" -type "float2" -0.14061669 0 ;
	setAttr ".uvtk[63]" -type "float2" -0.14061669 0 ;
	setAttr ".uvtk[64]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[65]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[66]" -type "float2" -0.14061669 0 ;
	setAttr ".uvtk[67]" -type "float2" -0.14061669 0 ;
	setAttr ".uvtk[68]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[69]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[70]" -type "float2" -0.14061669 0 ;
	setAttr ".uvtk[71]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[72]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[73]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[74]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[75]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[76]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[77]" -type "float2" -0.14061674 0 ;
	setAttr ".uvtk[154]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[155]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[156]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[157]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[158]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[159]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[160]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[161]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[162]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[163]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[164]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[165]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[166]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[167]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[168]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[169]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[170]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[171]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[172]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[173]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[174]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[175]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[176]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[177]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[178]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[179]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[180]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[181]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[182]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[183]" -type "float2" -0.14061673 0 ;
	setAttr ".uvtk[232]" -type "float2" 0.82622296 0.90420151 ;
	setAttr ".uvtk[233]" -type "float2" -0.16979271 0.97444296 ;
	setAttr ".uvtk[234]" -type "float2" -0.065677285 0.85452849 ;
	setAttr ".uvtk[235]" -type "float2" 0.70630866 0.80008626 ;
	setAttr ".uvtk[236]" -type "float2" -0.24003404 -0.021572899 ;
	setAttr ".uvtk[237]" -type "float2" -0.12011972 0.082542732 ;
	setAttr ".uvtk[238]" -type "float2" 0.75598162 -0.091814384 ;
	setAttr ".uvtk[239]" -type "float2" 0.65186626 0.028100364 ;
createNode polyMapSew -n "polyMapSew11";
	rename -uid "EDD67BB7-4A09-A97C-1188-4F8C08801DD7";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[138]";
createNode polyTweakUV -n "polyTweakUV28";
	rename -uid "3E3B32E5-410F-B05E-4225-0693E75A8F4C";
	setAttr ".uopa" yes;
	setAttr -s 23 ".uvtk";
	setAttr ".uvtk[64]" -type "float2" 2.4616718e-05 0 ;
	setAttr ".uvtk[65]" -type "float2" 2.4616718e-05 0 ;
	setAttr ".uvtk[66]" -type "float2" 0 -2.6583672e-05 ;
	setAttr ".uvtk[68]" -type "float2" 2.4616718e-05 0 ;
	setAttr ".uvtk[69]" -type "float2" -2.4616718e-05 0 ;
	setAttr ".uvtk[70]" -type "float2" 0 -5.4895878e-05 ;
	setAttr ".uvtk[71]" -type "float2" -2.4616718e-05 0 ;
	setAttr ".uvtk[232]" -type "float2" 0 2.6583672e-05 ;
	setAttr ".uvtk[233]" -type "float2" 0 5.4895878e-05 ;
	setAttr ".uvtk[235]" -type "float2" 0 -5.9604645e-08 ;
	setAttr ".uvtk[236]" -type "float2" 0 -5.9604645e-08 ;
	setAttr ".uvtk[237]" -type "float2" -5.9604645e-08 5.9604645e-08 ;
createNode polyAutoProj -n "polyAutoProj15";
	rename -uid "B39B674A-4BEB-1280-404C-8781F94CF133";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "f[50]" "f[90:93]";
	setAttr ".ix" -type "matrix" 1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1;
	setAttr ".s" -type "double3" 2.5476903915405273 2.5476903915405273 2.5476903915405273 ;
	setAttr ".ps" 0.20000000298023224;
	setAttr ".dl" yes;
createNode polyTweakUV -n "polyTweakUV29";
	rename -uid "E5FB44E2-4DBD-B8DD-4392-28AE46DBCB75";
	setAttr ".uopa" yes;
	setAttr -s 21 ".uvtk";
	setAttr ".uvtk[238]" -type "float2" 0.28029931 0.26650497 ;
	setAttr ".uvtk[239]" -type "float2" 0.52266455 0.20321323 ;
	setAttr ".uvtk[240]" -type "float2" 0.63184178 0.69930941 ;
	setAttr ".uvtk[241]" -type "float2" 0.38947642 0.7626012 ;
	setAttr ".uvtk[242]" -type "float2" 0.06009623 0.3887507 ;
	setAttr ".uvtk[243]" -type "float2" -0.18226901 0.32563117 ;
	setAttr ".uvtk[244]" -type "float2" -0.073848046 -0.17046545 ;
	setAttr ".uvtk[245]" -type "float2" 0.16851708 -0.10734607 ;
	setAttr ".uvtk[246]" -type "float2" 0.77167106 0.82550198 ;
	setAttr ".uvtk[247]" -type "float2" 0.52930588 0.76263851 ;
	setAttr ".uvtk[248]" -type "float2" 0.63782924 0.26654187 ;
	setAttr ".uvtk[249]" -type "float2" 0.88019466 0.32940558 ;
	setAttr ".uvtk[250]" -type "float2" 0.032208584 0.20395769 ;
	setAttr ".uvtk[251]" -type "float2" 0.27457407 0.1412463 ;
	setAttr ".uvtk[252]" -type "float2" 0.38357535 0.63734239 ;
	setAttr ".uvtk[253]" -type "float2" 0.14120971 0.70005411 ;
	setAttr ".uvtk[254]" -type "float2" 0.46368822 0.3668015 ;
	setAttr ".uvtk[255]" -type "float2" 0.22132294 0.40254045 ;
	setAttr ".uvtk[256]" -type "float2" 0.18558399 0.16017511 ;
	setAttr ".uvtk[257]" -type "float2" 0.42794928 0.12443614 ;
createNode polyMapSew -n "polyMapSew12";
	rename -uid "30ADFB4A-43A3-C9B5-EAA5-8D8C494C4FE8";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "e[185:186]" "e[188]";
createNode polyTweakUV -n "polyTweakUV30";
	rename -uid "25C2ABC3-4C8D-ACAF-9104-858C23451C35";
	setAttr ".uopa" yes;
	setAttr -s 15 ".uvtk";
	setAttr ".uvtk[238]" -type "float2" 6.6948123e-05 0.00093759765 ;
	setAttr ".uvtk[239]" -type "float2" 0.00025583501 0.001312602 ;
	setAttr ".uvtk[240]" -type "float2" 0.00047690887 0.0013127808 ;
	setAttr ".uvtk[241]" -type "float2" 0.00052697677 0.00093771686 ;
	setAttr ".uvtk[242]" -type "float2" -0.00023250555 -1.1920929e-07 ;
	setAttr ".uvtk[243]" -type "float2" -0.00022106146 0.00056260522 ;
	setAttr ".uvtk[244]" -type "float2" 0.00061692018 0.00056266482 ;
	setAttr ".uvtk[245]" -type "float2" 0.00065685529 1.7881393e-07 ;
	setAttr ".uvtk[246]" -type "float2" 0.00016779918 0.0014888516 ;
	setAttr ".uvtk[247]" -type "float2" 0.00047678966 0.0014892688 ;
	setAttr ".uvtk[248]" -type "float2" -0.046127543 -0.04055512 ;
	setAttr ".uvtk[249]" -type "float2" 0.016525038 -0.076294057 ;
	setAttr ".uvtk[250]" -type "float2" 0.052264109 -0.013641534 ;
	setAttr ".uvtk[251]" -type "float2" -0.010388605 0.02209747 ;
createNode polyPinUV -n "polyPinUV24";
	rename -uid "4CF3760A-485F-F447-AAF5-1A8BDD2363C7";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[238:245]";
	setAttr -s 8 ".pn[238:245]"  1 1 1 1 1 1 1 1;
createNode polyPinUV -n "polyPinUV25";
	rename -uid "508D91CA-4EDF-8E28-9587-2A83C5F37C3C";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyMapSew -n "polyMapSew13";
	rename -uid "3638C58F-4CCD-CBA5-DA24-68911F44289A";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "e[192]";
createNode polyTweakUV -n "polyTweakUV31";
	rename -uid "A2785344-4AFA-371B-5028-2BAFE198B061";
	setAttr ".uopa" yes;
	setAttr -s 45 ".uvtk";
	setAttr ".uvtk[79]" -type "float2" -0.00091953255 0 ;
	setAttr ".uvtk[80]" -type "float2" -0.00091953255 0 ;
	setAttr ".uvtk[82]" -type "float2" 0.21036342 0.037334774 ;
	setAttr ".uvtk[83]" -type "float2" -0.21086758 0.048075523 ;
	setAttr ".uvtk[84]" -type "float2" -0.21086735 -0.03791688 ;
	setAttr ".uvtk[85]" -type "float2" 0.21036342 -0.048657913 ;
	setAttr ".uvtk[86]" -type "float2" -0.21128291 0.039462704 ;
	setAttr ".uvtk[87]" -type "float2" -0.21128291 -0.038786873 ;
	setAttr ".uvtk[88]" -type "float2" 0.20997402 -0.038786873 ;
	setAttr ".uvtk[89]" -type "float2" 0.20997402 0.039462704 ;
	setAttr ".uvtk[90]" -type "float2" -0.00091953255 0 ;
	setAttr ".uvtk[91]" -type "float2" -0.00091953255 0 ;
	setAttr ".uvtk[98]" -type "float2" -0.00082220003 0 ;
	setAttr ".uvtk[99]" -type "float2" -0.00082220003 0 ;
	setAttr ".uvtk[102]" -type "float2" -0.00082220003 0.0024265042 ;
	setAttr ".uvtk[103]" -type "float2" -0.00082220003 0.002426534 ;
	setAttr ".uvtk[104]" -type "float2" 0 0.002426534 ;
	setAttr ".uvtk[105]" -type "float2" 0 0.0024265042 ;
	setAttr ".uvtk[106]" -type "float2" -0.00082220003 0 ;
	setAttr ".uvtk[107]" -type "float2" -0.00082220003 0 ;
	setAttr ".uvtk[110]" -type "float2" -0.00082220003 0.015367961 ;
	setAttr ".uvtk[111]" -type "float2" -0.00082220003 0.015367961 ;
	setAttr ".uvtk[112]" -type "float2" 0 0.015367961 ;
	setAttr ".uvtk[113]" -type "float2" 0 0.015367961 ;
	setAttr ".uvtk[240]" -type "float2" 3.6239624e-05 0 ;
	setAttr ".uvtk[241]" -type "float2" 3.6239624e-05 0 ;
	setAttr ".uvtk[242]" -type "float2" 0 7.4148178e-05 ;
	setAttr ".uvtk[243]" -type "float2" 0 3.8504601e-05 ;
	setAttr ".uvtk[244]" -type "float2" -3.6299229e-05 0 ;
	setAttr ".uvtk[245]" -type "float2" -3.6299229e-05 0 ;
	setAttr ".uvtk[247]" -type "float2" 3.6239624e-05 0 ;
	setAttr ".uvtk[248]" -type "float2" 5.9604645e-08 -3.8444996e-05 ;
	setAttr ".uvtk[249]" -type "float2" -5.9604645e-08 -7.4207783e-05 ;
createNode file -n "file2";
	rename -uid "F43DF5A4-4AEA-4A5E-979C-888AC2692BB9";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/jawaIonBlaster//jawaIonBlasterLayout.png";
	setAttr ".ft" 0;
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "place2dTexture2";
	rename -uid "4D189281-46F6-16CB-5E78-959524AD251E";
createNode polyPinUV -n "polyPinUV26";
	rename -uid "F5F235A9-45E4-19D0-E884-30967F0EAD5A";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 3 "map[94:97]" "map[102:105]" "map[110:113]";
	setAttr -s 12 ".pn";
	setAttr ".pn[94]" 1;
	setAttr ".pn[95]" 1;
	setAttr ".pn[96]" 1;
	setAttr ".pn[97]" 1;
	setAttr ".pn[102]" 1;
	setAttr ".pn[103]" 1;
	setAttr ".pn[104]" 1;
	setAttr ".pn[105]" 1;
	setAttr ".pn[110]" 1;
	setAttr ".pn[111]" 1;
	setAttr ".pn[112]" 1;
	setAttr ".pn[113]" 1;
createNode polyPinUV -n "polyPinUV27";
	rename -uid "D8642101-4CF9-BBB0-6EC7-0BB5468FB019";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 2 "map[102:105]" "map[110:113]";
	setAttr -s 8 ".pn";
	setAttr ".pn[102]" 0;
	setAttr ".pn[103]" 0;
	setAttr ".pn[104]" 0;
	setAttr ".pn[105]" 0;
	setAttr ".pn[110]" 0;
	setAttr ".pn[111]" 0;
	setAttr ".pn[112]" 0;
	setAttr ".pn[113]" 0;
	setAttr ".op" 1;
createNode polyPinUV -n "polyPinUV28";
	rename -uid "416985A4-4A6A-DF81-E4E4-3E8A7DA13E32";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode polyPinUV -n "polyPinUV29";
	rename -uid "BBFE526F-4F59-C500-F996-63BBA0AC47B7";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 1 "map[78:93]";
	setAttr -s 16 ".pn[78:93]"  1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1;
createNode polyPinUV -n "polyPinUV30";
	rename -uid "906601EF-4802-39E7-4183-61A96D28C4B0";
	setAttr ".uopa" yes;
	setAttr ".op" 2;
createNode nodeGraphEditorInfo -n "hyperShadePrimaryNodeEditorSavedTabsInfo";
	rename -uid "A516B58D-4B56-7200-746F-3F984DA9D7C0";
	setAttr ".tgi[0].tn" -type "string" "Untitled_1";
	setAttr ".tgi[0].vl" -type "double2" -473.21426691044024 -374.40474702725407 ;
	setAttr ".tgi[0].vh" -type "double2" 456.54760090604736 386.30950845896314 ;
	setAttr -s 8 ".tgi[0].ni";
	setAttr ".tgi[0].ni[0].x" 365.71429443359375;
	setAttr ".tgi[0].ni[0].y" 122.85713958740234;
	setAttr ".tgi[0].ni[0].nvs" 1923;
	setAttr ".tgi[0].ni[1].x" 58.571430206298828;
	setAttr ".tgi[0].ni[1].y" 145.71427917480469;
	setAttr ".tgi[0].ni[1].nvs" 1923;
	setAttr ".tgi[0].ni[2].x" -555.71429443359375;
	setAttr ".tgi[0].ni[2].y" 122.85713958740234;
	setAttr ".tgi[0].ni[2].nvs" 1923;
	setAttr ".tgi[0].ni[3].x" 58.571430206298828;
	setAttr ".tgi[0].ni[3].y" 145.71427917480469;
	setAttr ".tgi[0].ni[3].nvs" 1923;
	setAttr ".tgi[0].ni[4].x" -555.71429443359375;
	setAttr ".tgi[0].ni[4].y" 122.85713958740234;
	setAttr ".tgi[0].ni[4].nvs" 1923;
	setAttr ".tgi[0].ni[5].x" -248.57142639160156;
	setAttr ".tgi[0].ni[5].y" 145.71427917480469;
	setAttr ".tgi[0].ni[5].nvs" 1923;
	setAttr ".tgi[0].ni[6].x" 365.71429443359375;
	setAttr ".tgi[0].ni[6].y" 122.85713958740234;
	setAttr ".tgi[0].ni[6].nvs" 1923;
	setAttr ".tgi[0].ni[7].x" -248.57142639160156;
	setAttr ".tgi[0].ni[7].y" 145.71427917480469;
	setAttr ".tgi[0].ni[7].nvs" 1923;
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
	setAttr -s 20 ".dsm";
	setAttr ".ro" yes;
	setAttr -s 20 ".gn";
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
connectAttr "groupParts10.og" "pCubeShape11.i";
connectAttr "groupId19.id" "pCubeShape11.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape11.iog.og[0].gco";
connectAttr "groupId20.id" "pCubeShape11.ciog.cog[0].cgid";
connectAttr "groupParts4.og" "pCubeShape10.i";
connectAttr "groupId7.id" "pCubeShape10.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape10.iog.og[0].gco";
connectAttr "groupId8.id" "pCubeShape10.ciog.cog[0].cgid";
connectAttr "groupParts5.og" "pCubeShape9.i";
connectAttr "groupId9.id" "pCubeShape9.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape9.iog.og[0].gco";
connectAttr "groupId10.id" "pCubeShape9.ciog.cog[0].cgid";
connectAttr "groupParts9.og" "pPlaneShape1.i";
connectAttr "groupId17.id" "pPlaneShape1.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pPlaneShape1.iog.og[0].gco";
connectAttr "groupId18.id" "pPlaneShape1.ciog.cog[0].cgid";
connectAttr "groupParts6.og" "pCubeShape8.i";
connectAttr "groupId11.id" "pCubeShape8.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape8.iog.og[0].gco";
connectAttr "groupId12.id" "pCubeShape8.ciog.cog[0].cgid";
connectAttr "groupParts2.og" "pCubeShape7.i";
connectAttr "groupId3.id" "pCubeShape7.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape7.iog.og[0].gco";
connectAttr "groupId4.id" "pCubeShape7.ciog.cog[0].cgid";
connectAttr "groupParts8.og" "pCubeShape6.i";
connectAttr "groupId15.id" "pCubeShape6.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape6.iog.og[0].gco";
connectAttr "groupId16.id" "pCubeShape6.ciog.cog[0].cgid";
connectAttr "groupParts1.og" "pCubeShape3.i";
connectAttr "groupId1.id" "pCubeShape3.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape3.iog.og[0].gco";
connectAttr "groupId2.id" "pCubeShape3.ciog.cog[0].cgid";
connectAttr "groupParts7.og" "pCubeShape2.i";
connectAttr "groupId13.id" "pCubeShape2.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape2.iog.og[0].gco";
connectAttr "groupId14.id" "pCubeShape2.ciog.cog[0].cgid";
connectAttr "groupParts3.og" "pCubeShape1.i";
connectAttr "groupId5.id" "pCubeShape1.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "pCubeShape1.iog.og[0].gco";
connectAttr "groupId6.id" "pCubeShape1.ciog.cog[0].cgid";
connectAttr "polyPinUV30.out" "pCube12Shape.i";
connectAttr "polyTweakUV31.uvtk[0]" "pCube12Shape.uvst[0].uvtw";
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
connectAttr "polyTweak2.out" "polyExtrudeFace2.ip";
connectAttr "pCubeShape1.wm" "polyExtrudeFace2.mp";
connectAttr "polyExtrudeFace1.out" "polyTweak2.ip";
connectAttr "polyTweak3.out" "polyExtrudeFace3.ip";
connectAttr "pCubeShape2.wm" "polyExtrudeFace3.mp";
connectAttr "polyCube2.out" "polyTweak3.ip";
connectAttr "polyTweak4.out" "polyExtrudeFace4.ip";
connectAttr "pCubeShape2.wm" "polyExtrudeFace4.mp";
connectAttr "polyExtrudeFace3.out" "polyTweak4.ip";
connectAttr "polyTweak5.out" "polyExtrudeFace5.ip";
connectAttr "pCubeShape2.wm" "polyExtrudeFace5.mp";
connectAttr "polyExtrudeFace4.out" "polyTweak5.ip";
connectAttr "polyTweak6.out" "polyExtrudeFace6.ip";
connectAttr "pCubeShape2.wm" "polyExtrudeFace6.mp";
connectAttr "polyExtrudeFace5.out" "polyTweak6.ip";
connectAttr "polyTweak8.out" "polyExtrudeFace9.ip";
connectAttr "pCubeShape8.wm" "polyExtrudeFace9.mp";
connectAttr "polyCube8.out" "polyTweak8.ip";
connectAttr "polyTweak9.out" "polyExtrudeFace10.ip";
connectAttr "pCubeShape8.wm" "polyExtrudeFace10.mp";
connectAttr "polyExtrudeFace9.out" "polyTweak9.ip";
connectAttr "polyPlane1.out" "polyExtrudeEdge1.ip";
connectAttr "pPlaneShape1.wm" "polyExtrudeEdge1.mp";
connectAttr "polyTweak10.out" "polyExtrudeEdge2.ip";
connectAttr "pPlaneShape1.wm" "polyExtrudeEdge2.mp";
connectAttr "polyExtrudeEdge1.out" "polyTweak10.ip";
connectAttr "polyTweak11.out" "polyExtrudeEdge3.ip";
connectAttr "pPlaneShape1.wm" "polyExtrudeEdge3.mp";
connectAttr "polyExtrudeEdge2.out" "polyTweak11.ip";
connectAttr "polyExtrudeFace2.out" "polyTweak12.ip";
connectAttr "polyTweak12.out" "deleteComponent1.ig";
connectAttr "deleteComponent1.og" "polyCloseBorder1.ip";
connectAttr "pCubeShape3.o" "polyUnite1.ip[0]";
connectAttr "pCubeShape7.o" "polyUnite1.ip[1]";
connectAttr "pCubeShape1.o" "polyUnite1.ip[2]";
connectAttr "pCubeShape10.o" "polyUnite1.ip[3]";
connectAttr "pCubeShape9.o" "polyUnite1.ip[4]";
connectAttr "pCubeShape8.o" "polyUnite1.ip[5]";
connectAttr "pCubeShape2.o" "polyUnite1.ip[6]";
connectAttr "pCubeShape6.o" "polyUnite1.ip[7]";
connectAttr "pPlaneShape1.o" "polyUnite1.ip[8]";
connectAttr "pCubeShape11.o" "polyUnite1.ip[9]";
connectAttr "pCubeShape3.wm" "polyUnite1.im[0]";
connectAttr "pCubeShape7.wm" "polyUnite1.im[1]";
connectAttr "pCubeShape1.wm" "polyUnite1.im[2]";
connectAttr "pCubeShape10.wm" "polyUnite1.im[3]";
connectAttr "pCubeShape9.wm" "polyUnite1.im[4]";
connectAttr "pCubeShape8.wm" "polyUnite1.im[5]";
connectAttr "pCubeShape2.wm" "polyUnite1.im[6]";
connectAttr "pCubeShape6.wm" "polyUnite1.im[7]";
connectAttr "pPlaneShape1.wm" "polyUnite1.im[8]";
connectAttr "pCubeShape11.wm" "polyUnite1.im[9]";
connectAttr "polyCube3.out" "groupParts1.ig";
connectAttr "groupId1.id" "groupParts1.gi";
connectAttr "polyCube7.out" "groupParts2.ig";
connectAttr "groupId3.id" "groupParts2.gi";
connectAttr "polyCloseBorder1.out" "groupParts3.ig";
connectAttr "groupId5.id" "groupParts3.gi";
connectAttr "polyCube10.out" "groupParts4.ig";
connectAttr "groupId7.id" "groupParts4.gi";
connectAttr "polyCube9.out" "groupParts5.ig";
connectAttr "groupId9.id" "groupParts5.gi";
connectAttr "polyExtrudeFace10.out" "groupParts6.ig";
connectAttr "groupId11.id" "groupParts6.gi";
connectAttr "polyExtrudeFace6.out" "groupParts7.ig";
connectAttr "groupId13.id" "groupParts7.gi";
connectAttr "polyCube6.out" "groupParts8.ig";
connectAttr "groupId15.id" "groupParts8.gi";
connectAttr "polyExtrudeEdge3.out" "groupParts9.ig";
connectAttr "groupId17.id" "groupParts9.gi";
connectAttr "polyCube11.out" "groupParts10.ig";
connectAttr "groupId19.id" "groupParts10.gi";
connectAttr "polyUnite1.out" "polyMapDel1.ip";
connectAttr "file2.oc" "layout.c";
connectAttr "file2.ot" "layout.it";
connectAttr "layout.oc" "lambert2SG.ss";
connectAttr "pCube12Shape.iog" "lambert2SG.dsm" -na;
connectAttr "lambert2SG.msg" "materialInfo1.sg";
connectAttr "layout.msg" "materialInfo1.m";
connectAttr "file2.msg" "materialInfo1.t" -na;
connectAttr "file1.oc" "pixelMeasure.c";
connectAttr "pixelMeasure.oc" "lambert3SG.ss";
connectAttr "lambert3SG.msg" "materialInfo2.sg";
connectAttr "pixelMeasure.msg" "materialInfo2.m";
connectAttr "file1.msg" "materialInfo2.t" -na;
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
connectAttr "polyMapDel1.out" "polyAutoProj1.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj1.mp";
connectAttr "polyAutoProj1.out" "polyTweakUV1.ip";
connectAttr "polyTweakUV1.out" "polyMapSew1.ip";
connectAttr "polyMapSew1.out" "polyTweakUV2.ip";
connectAttr "polyTweakUV2.out" "polyAutoProj2.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj2.mp";
connectAttr "polyAutoProj2.out" "polyTweakUV3.ip";
connectAttr "polyTweakUV3.out" "polyAutoProj3.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj3.mp";
connectAttr "polyAutoProj3.out" "polyTweakUV4.ip";
connectAttr "polyTweakUV4.out" "polyAutoProj4.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj4.mp";
connectAttr "polyAutoProj4.out" "polyTweakUV5.ip";
connectAttr "polyTweakUV5.out" "polyAutoProj5.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj5.mp";
connectAttr "polyAutoProj5.out" "polyTweakUV6.ip";
connectAttr "polyTweakUV6.out" "polyAutoProj6.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj6.mp";
connectAttr "polyAutoProj6.out" "polyTweakUV7.ip";
connectAttr "polyTweakUV7.out" "polyAutoProj7.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj7.mp";
connectAttr "polyAutoProj7.out" "polyTweakUV8.ip";
connectAttr "polyTweakUV8.out" "polyAutoProj8.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj8.mp";
connectAttr "polyAutoProj8.out" "polyTweakUV9.ip";
connectAttr "polyTweakUV9.out" "polyAutoProj9.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj9.mp";
connectAttr "polyAutoProj9.out" "polyTweakUV10.ip";
connectAttr "polyTweakUV10.out" "polyMapSew2.ip";
connectAttr "polyMapSew2.out" "polyTweakUV11.ip";
connectAttr "polyTweakUV11.out" "polyMapSew3.ip";
connectAttr "polyMapSew3.out" "polyTweakUV12.ip";
connectAttr "polyTweakUV12.out" "polyPinUV1.ip";
connectAttr "polyPinUV1.out" "polyPinUV2.ip";
connectAttr "polyPinUV2.out" "polyPinUV3.ip";
connectAttr "polyPinUV3.out" "polyPinUV4.ip";
connectAttr "polyPinUV4.out" "polyMapSew4.ip";
connectAttr "polyMapSew4.out" "polyTweakUV13.ip";
connectAttr "polyTweakUV13.out" "polyPinUV5.ip";
connectAttr "polyPinUV5.out" "polyPinUV6.ip";
connectAttr "polyPinUV6.out" "polyPinUV7.ip";
connectAttr "polyPinUV7.out" "polyPinUV8.ip";
connectAttr "polyPinUV8.out" "polyPinUV9.ip";
connectAttr "polyPinUV9.out" "polyMapSew5.ip";
connectAttr "polyMapSew5.out" "polyTweakUV14.ip";
connectAttr "polyTweakUV14.out" "polyMapSew6.ip";
connectAttr "polyMapSew6.out" "polyTweakUV15.ip";
connectAttr "polyTweakUV15.out" "polyPinUV10.ip";
connectAttr "polyPinUV10.out" "polyPinUV11.ip";
connectAttr "polyPinUV11.out" "polyPinUV12.ip";
connectAttr "polyPinUV12.out" "polyPinUV13.ip";
connectAttr "polyPinUV13.out" "polyAutoProj10.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj10.mp";
connectAttr "polyAutoProj10.out" "polyTweakUV16.ip";
connectAttr "polyTweakUV16.out" "polyAutoProj11.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj11.mp";
connectAttr "polyAutoProj11.out" "polyTweakUV17.ip";
connectAttr "polyTweakUV17.out" "polyMapSew7.ip";
connectAttr "polyMapSew7.out" "polyTweakUV18.ip";
connectAttr "polyTweakUV18.out" "polyPinUV14.ip";
connectAttr "polyPinUV14.out" "polyPinUV15.ip";
connectAttr "polyPinUV15.out" "polyPinUV16.ip";
connectAttr "polyPinUV16.out" "polyPinUV17.ip";
connectAttr "polyPinUV17.out" "polyMapSew8.ip";
connectAttr "polyMapSew8.out" "polyTweakUV19.ip";
connectAttr "polyTweakUV19.out" "polyMapCut1.ip";
connectAttr "polyMapCut1.out" "polyTweakUV20.ip";
connectAttr "polyTweakUV20.out" "polyMapSew9.ip";
connectAttr "polyMapSew9.out" "polyTweakUV21.ip";
connectAttr "polyTweakUV21.out" "polyPinUV18.ip";
connectAttr "polyPinUV18.out" "polyPinUV19.ip";
connectAttr "polyPinUV19.out" "polyPinUV20.ip";
connectAttr "polyPinUV20.out" "polyPinUV21.ip";
connectAttr "polyPinUV21.out" "polyMapCut2.ip";
connectAttr "polyMapCut2.out" "polyTweakUV22.ip";
connectAttr "polyTweak13.out" "polyAutoProj12.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj12.mp";
connectAttr "polyTweakUV22.out" "polyTweak13.ip";
connectAttr "polyAutoProj12.out" "polyTweakUV23.ip";
connectAttr "polyTweakUV23.out" "polyAutoProj13.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj13.mp";
connectAttr "polyAutoProj13.out" "polyTweakUV24.ip";
connectAttr "polyTweakUV24.out" "polyPinUV22.ip";
connectAttr "polyPinUV22.out" "polyPinUV23.ip";
connectAttr "polyPinUV23.out" "polyMapCut3.ip";
connectAttr "polyMapCut3.out" "polyTweakUV25.ip";
connectAttr "polyTweakUV25.out" "polyMapSew10.ip";
connectAttr "polyMapSew10.out" "polyTweakUV26.ip";
connectAttr "polyTweakUV26.out" "polyMapDel2.ip";
connectAttr "polyMapDel2.out" "polyExtrudeFace11.ip";
connectAttr "pCube12Shape.wm" "polyExtrudeFace11.mp";
connectAttr "polyTweak14.out" "polyExtrudeFace12.ip";
connectAttr "pCube12Shape.wm" "polyExtrudeFace12.mp";
connectAttr "polyExtrudeFace11.out" "polyTweak14.ip";
connectAttr "polyTweak15.out" "polyAutoProj14.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj14.mp";
connectAttr "polyExtrudeFace12.out" "polyTweak15.ip";
connectAttr "polyAutoProj14.out" "polyTweakUV27.ip";
connectAttr "polyTweakUV27.out" "polyMapSew11.ip";
connectAttr "polyMapSew11.out" "polyTweakUV28.ip";
connectAttr "polyTweakUV28.out" "polyAutoProj15.ip";
connectAttr "pCube12Shape.wm" "polyAutoProj15.mp";
connectAttr "polyAutoProj15.out" "polyTweakUV29.ip";
connectAttr "polyTweakUV29.out" "polyMapSew12.ip";
connectAttr "polyMapSew12.out" "polyTweakUV30.ip";
connectAttr "polyTweakUV30.out" "polyPinUV24.ip";
connectAttr "polyPinUV24.out" "polyPinUV25.ip";
connectAttr "polyPinUV25.out" "polyMapSew13.ip";
connectAttr "polyMapSew13.out" "polyTweakUV31.ip";
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
connectAttr "polyTweakUV31.out" "polyPinUV26.ip";
connectAttr "polyPinUV26.out" "polyPinUV27.ip";
connectAttr "polyPinUV27.out" "polyPinUV28.ip";
connectAttr "polyPinUV28.out" "polyPinUV29.ip";
connectAttr "polyPinUV29.out" "polyPinUV30.ip";
connectAttr "lambert2SG.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[0].dn"
		;
connectAttr "layout.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[1].dn"
		;
connectAttr "place2dTexture1.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[2].dn"
		;
connectAttr "pixelMeasure.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[3].dn"
		;
connectAttr "place2dTexture2.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[4].dn"
		;
connectAttr "file2.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[5].dn"
		;
connectAttr "lambert3SG.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[6].dn"
		;
connectAttr "file1.msg" "hyperShadePrimaryNodeEditorSavedTabsInfo.tgi[0].ni[7].dn"
		;
connectAttr "lambert2SG.pa" ":renderPartition.st" -na;
connectAttr "lambert3SG.pa" ":renderPartition.st" -na;
connectAttr "layout.msg" ":defaultShaderList1.s" -na;
connectAttr "pixelMeasure.msg" ":defaultShaderList1.s" -na;
connectAttr "place2dTexture1.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "place2dTexture2.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "defaultRenderLayer.msg" ":defaultRenderingList1.r" -na;
connectAttr "file1.msg" ":defaultTextureList1.tx" -na;
connectAttr "file2.msg" ":defaultTextureList1.tx" -na;
connectAttr "pCubeShape3.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape3.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape7.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape7.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape1.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape1.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape10.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape10.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape9.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape9.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape8.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape8.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape2.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape2.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape6.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape6.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pPlaneShape1.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pPlaneShape1.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape11.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "pCubeShape11.ciog.cog[0]" ":initialShadingGroup.dsm" -na;
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
// End of v07.ma
