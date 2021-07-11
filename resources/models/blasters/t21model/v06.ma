//Maya ASCII 2020 scene
//Name: v06.ma
//Last modified: Sun, Jun 20, 2021 12:22:33 PM
//Codeset: 1252
requires maya "2020";
requires "stereoCamera" "10.0";
currentUnit -l centimeter -a degree -t film;
fileInfo "application" "maya";
fileInfo "product" "Maya 2020";
fileInfo "version" "2020";
fileInfo "cutIdentifier" "201911140446-42a737a01c";
fileInfo "osv" "Microsoft Windows 10 Technical Preview  (Build 19041)\n";
fileInfo "UUID" "4AC4DACE-4D36-43CC-C59C-46A406CBF938";
createNode transform -s -n "persp";
	rename -uid "148A83F5-4D94-2261-559E-4990074A1E56";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 2.5009718089128858 0.44358297348489223 0.51095930237633125 ;
	setAttr ".r" -type "double3" 0.32877924280050258 116.99999999979431 0 ;
	setAttr ".rp" -type "double3" 0 0 4.4408920985006262e-16 ;
	setAttr ".rpt" -type "double3" 3.9917038394014439e-16 2.5340867213094038e-17 -2.5112270669674582e-16 ;
createNode camera -s -n "perspShape" -p "persp";
	rename -uid "B5BBE8A1-4564-DD17-688D-7D9B50FF1B39";
	setAttr -k off ".v" no;
	setAttr ".fl" 34.999999999999993;
	setAttr ".coi" 3.3003770032459849;
	setAttr ".imn" -type "string" "persp";
	setAttr ".den" -type "string" "persp_depth";
	setAttr ".man" -type "string" "persp_mask";
	setAttr ".hc" -type "string" "viewSet -p %camera";
createNode transform -s -n "top";
	rename -uid "B63D97EC-4760-9778-B4AB-1E86BA630AC0";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 0 1000.1 0 ;
	setAttr ".r" -type "double3" -90 0 0 ;
createNode camera -s -n "topShape" -p "top";
	rename -uid "F9B85122-4BCE-2894-25EB-FBB808D8136F";
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
	rename -uid "F36014B2-4458-4EB4-86FF-52BEC711C547";
	setAttr ".v" no;
	setAttr ".t" -type "double3" 0 0 1000.1 ;
createNode camera -s -n "frontShape" -p "front";
	rename -uid "9EE8F784-47FB-C407-3B03-EB841D8208D0";
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
	rename -uid "F45243F2-4062-DE28-4D1F-A996BD515963";
	setAttr ".t" -type "double3" 1000.1 0.32035486126824753 1.7163323110906841 ;
	setAttr ".r" -type "double3" 0 90 0 ;
createNode camera -s -n "sideShape" -p "side";
	rename -uid "F2DBCC14-4A3F-C7DE-2678-0AAD0C435C65";
	setAttr -k off ".v";
	setAttr ".rnd" no;
	setAttr ".coi" 1000.1;
	setAttr ".ow" 10.089961568691026;
	setAttr ".imn" -type "string" "side";
	setAttr ".den" -type "string" "side_depth";
	setAttr ".man" -type "string" "side_mask";
	setAttr ".hc" -type "string" "viewSet -s %camera";
	setAttr ".o" yes;
createNode transform -n "imagePlane1";
	rename -uid "7EC25F2E-4603-3483-DEC4-519CA5287966";
	setAttr ".t" -type "double3" -7.3977642545051365 0.67453930001938589 0 ;
	setAttr ".r" -type "double3" 0 90 0 ;
	setAttr ".s" -type "double3" 0.76661368200632096 0.76661368200632096 0.76661368200632096 ;
createNode imagePlane -n "imagePlaneShape1" -p "imagePlane1";
	rename -uid "F9BB1158-41AA-6B32-A7FF-3BBBE1B61D2F";
	setAttr -k off ".v";
	setAttr ".fc" 102;
	setAttr ".imn" -type "string" "C:/Users/Liam - Moose/Downloads/T-21_Blaster_DICE.png";
	setAttr ".cov" -type "short2" 953 387 ;
	setAttr ".dlc" no;
	setAttr ".w" 9.53;
	setAttr ".h" 3.8699999999999997;
	setAttr ".cs" -type "string" "sRGB";
createNode transform -n "group1";
	rename -uid "99A0E401-46F9-F38B-0D0D-2BBDBAB84733";
createNode transform -n "pCube9" -p "group1";
	rename -uid "7D5D512A-4416-DECF-5E12-629C5E004CF8";
	setAttr ".t" -type "double3" 0 0.47514110298328677 1.2833310994666487 ;
	setAttr ".s" -type "double3" 0.086529677929878132 0.2068537399157461 0.2068537399157461 ;
createNode mesh -n "polySurfaceShape1" -p "pCube9";
	rename -uid "01662BB9-451C-452C-040F-B0883C1880A2";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.5 0.125 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr -s 14 ".uvst[0].uvsp[0:13]" -type "float2" 0.375 0 0.625 0 0.375
		 0.25 0.625 0.25 0.375 0.5 0.625 0.5 0.375 0.75 0.625 0.75 0.375 1 0.625 1 0.875 0
		 0.875 0.25 0.125 0 0.125 0.25;
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 8 ".pt[0:7]" -type "float3"  0 0.81777686 0.17777753 0 
		0.81777686 0.17777753 0 0 0.39194405 0 0 0.39194405 0 0 -0.39194447 0 0 -0.39194447 
		0 0.81777686 -0.17777774 0 0.81777686 -0.17777774;
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
createNode transform -n "group2";
	rename -uid "F41AA91A-4AE4-B731-882B-81971011E4C8";
	setAttr ".rp" -type "double3" 0 0.59690190856270431 0.02260204539674926 ;
	setAttr ".sp" -type "double3" 0 0.59690190856270431 0.02260204539674926 ;
createNode mesh -n "group2Shape" -p "group2";
	rename -uid "A467CD30-43C1-F6E2-2752-55B87A5AA783";
	setAttr -k off ".v";
	setAttr -s 2 ".iog[0].og";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.58196753263473511 0.097672358155250549 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
createNode mesh -n "polySurfaceShape2" -p "group2";
	rename -uid "EA6B9747-4878-7D38-6F8E-AB8BA66A5D7F";
	setAttr -k off ".v";
	setAttr ".io" yes;
	setAttr ".iog[0].og[0].gcl" -type "componentList" 1 "f[0:107]";
	setAttr ".vir" yes;
	setAttr ".vif" yes;
	setAttr ".pv" -type "double2" 0.58196753263473511 0.097672358155250549 ;
	setAttr ".uvst[0].uvsn" -type "string" "map1";
	setAttr -s 286 ".uvst[0].uvsp";
	setAttr ".uvst[0].uvsp[0:249]" -type "float2" 0.28109756 0.22626537 0.47664142
		 0.22626537 0.47664142 0.26559347 0.28109756 0.26559347 0.28109756 0.28913781 0.47664142
		 0.28913781 0.47664142 0.32808456 0.28109756 0.32808456 0.47664142 0.35171053 0.28109756
		 0.35171053 0.17109682 0.35228208 0.17109682 0.39083859 0.11721684 0.37011656 0.11721684
		 0.34012833 0.10514905 0.37450299 0.039136555 0.37450299 0.039136555 0.30798039 0.1710601
		 0.30453214 0.1710601 0.26591566 0.11709625 0.25374296 0.11709625 0.28377792 0.10500965
		 0.28823414 0.038928542 0.22158064 0.038928542 0.28823414 0.27310956 0.31270257 0.27310956
		 0.33600381 0.21856949 0.33600381 0.21856949 0.31270257 0.20697615 0.33600381 0.20697615
		 0.31270257 0.1405455 0.33600381 0.1405455 0.31270257 0.015727516 0.22158064 0.015727516
		 0.28823414 0.27319571 0.22673191 0.27319571 0.25026459 0.21870659 0.25026459 0.21870659
		 0.22673191 0.14082901 0.25026459 0.14082901 0.22673191 0.070303395 0.41410336 0.070303395
		 0.39860103 0.085926339 0.39860103 0.085926339 0.41410336 0.070303395 0.39358959 0.085926339
		 0.39358959 0.085926339 0.41913125 0.070303395 0.41913125 0.065180846 0.41410336 0.065180846
		 0.39860103 0.090976 0.39860103 0.090976 0.41410336 0.28898406 0.35955253 0.3750965
		 0.35955253 0.3750965 0.36731514 0.28898406 0.36731514 0.28898406 0.37505445 0.3750965
		 0.37505445 0.3750965 0.38284144 0.28898406 0.38284144 0.38277933 0.37505445 0.38277933
		 0.38284144 0.28131443 0.35955253 0.28131443 0.36731514 0.28908116 0.42197475 0.3437596
		 0.42197475 0.3437596 0.42979977 0.28908116 0.42979977 0.3437596 0.44549552 0.28908116
		 0.44549552 0.28908116 0.43755278 0.3437596 0.43755278 0.3437596 0.45323285 0.28908116
		 0.45323285 0.28128946 0.42197475 0.28128946 0.42979977 0.35144395 0.44549552 0.35144395
		 0.45323285 0.29690781 0.40635338 0.30464882 0.40635338 0.30464882 0.41414002 0.29690781
		 0.41414002 0.30464882 0.39854601 0.29690781 0.39854601 0.29690781 0.39077774 0.30464882
		 0.39077774 0.32817221 0.40635797 0.3359721 0.40635797 0.3359721 0.41418543 0.32817221
		 0.41418543 0.3359721 0.39854947 0.32817221 0.39854947 0.32817221 0.39075062 0.33597213
		 0.39075062 0.42975965 0.3633016 0.43749109 0.3633016 0.43749109 0.3751097 0.42975965
		 0.3751097 0.44527721 0.3633016 0.45306477 0.3633016 0.45306477 0.3751097 0.44527721
		 0.3751097 0.42975965 0.38282815 0.43749109 0.38282815 0.46094951 0.3633016 0.46094951
		 0.3751097 0.3669962 0.19522579 0.3669962 0.17225344 0.3903859 0.15652208 0.3903859
		 0.21095721 0.42149529 0.15664174 0.42149529 0.21086212 0.39806232 0.1952381 0.39806232
		 0.17226572 0.43731219 0.15643759 0.49223298 0.15643759 0.49223298 0.21081226 0.43731219
		 0.21081226 0.42153832 0.1564102 0.39815918 0.15641026 0.39815912 0.14863907 0.42153826
		 0.14863901 0.4215689 0.21869712 0.39814302 0.21869712 0.39814302 0.21096848 0.4215689
		 0.21096854 0.60170805 0.15643759 0.60170805 0.21081226 0.60170805 0.32052061 0.49223298
		 0.32052061 0.49223298 0.26565966 0.60170805 0.26565966 0.60170805 0.37531081 0.49223298
		 0.37531081 0.65624344 0.32052061 0.65624344 0.37531081 0.60560119 0.37140885 0.65231818
		 0.37140885 0.65231818 0.32469186 0.60560119 0.32469186 0.67955685 0.43754193 0.67955685
		 0.39068517 0.83586335 0.39068517 0.83586335 0.43754193 0.67955685 0.53129715 0.67955685
		 0.48433694 0.83586335 0.48433694 0.83586335 0.53129715 0.67955685 0.57802731 0.83586335
		 0.57802731 0.85955119 0.39389536 0.85955119 0.43410167 0.85944271 0.48716441 0.85944271
		 0.52804416 0.85942888 0.44114169 0.85942888 0.48081443 0.8596679 0.53446823 0.8596679
		 0.57496947 0.93731296 0.39389536 0.93731296 0.43410167 0.93726397 0.48716441 0.93726397
		 0.52804416 0.93752468 0.44114169 0.93752468 0.48081443 0.93740249 0.53446823 0.93740249
		 0.57496947 0.98190975 0.53440458 0.98190999 0.57486039 0.94145387 0.57486051 0.94145405
		 0.53440458 0.60149443 0.41377282 0.60938638 0.41377282 0.60938638 0.42935264 0.60149443
		 0.42935264 0.60938638 0.40639183 0.60149443 0.40639183 0.60149443 0.39102003 0.60938638
		 0.39102003 0.84364098 0.12911163 0.87030035 0.12911163 0.87451231 0.13269539 0.81956172
		 0.13269539 0.82377362 0.12911163 0.87173319 0.11560605 0.83567303 0.085763052 0.86214864
		 0.079010263 0.90539634 0.13267596 0.8911761 0.13267596 0.89570475 0.11912309 0.90347147
		 0.11912309 0.87906361 0.11871137 0.88184822 0.11544241 0.88049102 0.11871137 0.88049102
		 0.12452276 0.88194692 0.13267167 0.87825108 0.13267167 0.87825108 0.12452276 0.63182372
		 0.12904887 0.61190563 0.12904887 0.607683 0.13264175 0.66277397 0.13264175 0.65855122
		 0.12904887 0.65998775 0.11550881 0.6503787 0.078819528 0.62383538 0.085589603 0.6792568
		 0.13264741 0.69342577 0.13264741 0.69150782 0.11914353 0.68376923 0.11914353 0.66710615
		 0.118504 0.66628456 0.12437941 0.66628456 0.13261791 0.67002106 0.13261791 0.66854906
		 0.12437941 0.66854906 0.118504 0.66992128 0.11519916 0.92760068 0.1094379 0.95501435
		 0.1094379 0.96051955 0.13252349 0.92209554 0.13252349 0.74273348 0.10937499 0.71523988
		 0.10937499 0.7097187 0.13257007 0.74825466 0.13257007 0.57810128 0.10930796 0.58583379
		 0.10930796 0.58583379 0.12869121 0.57810128 0.12869121 0.58583379 0.13274635 0.57810128
		 0.13274635 0.58583379 0.062598363 0.57810128 0.062598363 0.86254174 0.054720387 0.86254174
		 0.062465951 0.83605897 0.062465951 0.83605903 0.054720387 0.98431504 0.12902562 0.97660828
		 0.12902562 0.97660828 0.11529209 0.98431504 0.11529209 0.98431504 0.13290845 0.97660828
		 0.13290845 0.97667873 0.07814385 0.98430067 0.07814385 0.98430067 0.1152557 0.97667873
		 0.1152557 0.71882701 0.093870863 0.71882701 0.085769571 0.74210018 0.085769519 0.74210012
		 0.093870863;
	setAttr ".uvst[0].uvsp[250:285]" 0.7498734 0.085769549 0.7498734 0.093870863
		 0.69520485 0.093703136 0.68742824 0.093703136 0.68742824 0.085894451 0.69520485 0.085894451
		 0.69520485 0.10939203 0.68742824 0.10939203 0.68742824 0.070475176 0.69520485 0.070475176
		 0.9531548 0.070221096 0.94536841 0.070221096 0.94536841 0.043315634 0.9531548 0.043315634
		 0.95290351 0.039235383 0.9454205 0.039235383 0.9454205 0.015662447 0.95290351 0.015662447
		 0.9531548 0.093581721 0.94536841 0.093581721 0.79671347 0.13276406 0.78896916 0.13276406
		 0.78896916 0.12420391 0.79671347 0.12420391 0.78896916 0.12176446 0.79671347 0.12176446
		 0.78896916 0.1092508 0.79671347 0.1092508 0.7734735 0.12416439 0.78109539 0.12416439
		 0.78109539 0.13269208 0.7734735 0.13269208 0.7734735 0.12183944 0.78109539 0.12183944
		 0.7734735 0.10933881 0.78109539 0.10933881;
	setAttr ".cuvs" -type "string" "map1";
	setAttr ".dcc" -type "string" "Ambient+Diffuse";
	setAttr ".covm[0]"  0 1 1;
	setAttr ".cdvm[0]"  0 1 1;
	setAttr -s 4 ".pt";
	setAttr ".pt[106]" -type "float3" 0 0 -0.039954122 ;
	setAttr ".pt[107]" -type "float3" 0 0 -0.039954122 ;
	setAttr ".pt[120]" -type "float3" 0 -0.0049942662 -0.01331806 ;
	setAttr ".pt[121]" -type "float3" 0 -0.0049942662 -0.01331806 ;
	setAttr -s 156 ".vt[0:155]"  -0.11951976 0.57037902 2.10838199 0.11951976 0.57037902 2.10838199
		 -0.11951976 0.95627785 2.10838199 0.11951976 0.95627785 2.10838199 -0.11951976 0.95627785 0.18160319
		 0.11951976 0.95627785 0.18160319 -0.11951976 0.57037902 0.18160319 0.11951976 0.57037902 0.18160319
		 -0.11951976 0.44873434 2.64764977 0.11951976 0.44873434 2.64764977 0.11951976 0.74887794 2.64764977
		 -0.11951976 0.74887794 2.64764977 -0.11951976 0.12697601 3.42913103 0.11951976 0.12697601 3.42913103
		 0.11951976 0.78729206 3.42913103 -0.11951976 0.78729206 3.42913103 0.11951976 0.79826933 2.76843214
		 -0.11951976 0.79826933 2.76843214 -0.043534569 0.91160911 2.097791195 0.043534569 0.91160911 2.097791195
		 -0.043534569 0.99867827 2.097791195 0.043534569 0.99867827 2.097791195 -0.043534569 0.99867827 1.20179605
		 0.043534569 0.99867827 1.20179605 -0.043534569 0.91160911 1.20179605 0.043534569 0.91160911 1.20179605
		 -0.035925742 0.99813795 2.073405981 0.035925742 0.99813795 2.073405981 -0.035925742 0.99813795 2.0015544891
		 0.035925742 0.99813795 2.0015544891 -0.035925742 1.062140703 2.073405981 -0.035925742 1.062140703 2.0015544891
		 0.035925742 1.062140703 2.073405981 0.035925742 1.062140703 2.0015544891 -0.035925742 0.99813795 1.65765595
		 0.035925742 0.99813795 1.65765595 -0.035925742 0.99813795 1.58580446 0.035925742 0.99813795 1.58580446
		 -0.035925742 1.062140703 1.65765595 -0.035925742 1.062140703 1.58580446 0.035925742 1.062140703 1.65765595
		 0.035925742 1.062140703 1.58580446 -0.033761505 1.016850471 2.091210842 0.033761505 1.016850471 2.091210842
		 -0.033761505 1.084373474 2.091210842 0.033761505 1.084373474 2.091210842 -0.033761505 1.084373474 1.53421986
		 0.033761505 1.084373474 1.53421986 -0.033761505 1.016850471 1.53421986 0.033761505 1.016850471 1.53421986
		 -0.048723441 0.94179541 0.80340451 0.048723441 0.94179541 0.80340451 -0.048723444 1.088627338 0.80340451
		 0.048723437 1.088627338 0.80340451 -0.048723444 1.088627338 0.70595759 0.048723437 1.088627338 0.70595759
		 -0.048723441 0.94179541 0.70595759 0.048723441 0.94179541 0.70595759 -0.25710893 0.56539214 0.18711925
		 0.25710893 0.56539214 0.18711925 -0.25710893 1.079610229 0.18711925 0.25710893 1.079610229 0.18711925
		 -0.25710893 1.079610229 -0.86778975 0.25710893 1.079610229 -0.86778975 -0.25710893 0.56539214 -0.86778975
		 0.25710893 0.56539214 -0.86778975 -0.2201198 1.042621136 -0.86778975 0.2201198 1.042621136 -0.86778975
		 0.2201198 0.60238129 -0.86778975 -0.2201198 0.60238129 -0.86778975 -0.2201198 1.042621136 -2.395257
		 0.2201198 1.042621136 -2.395257 0.2201198 0.60238129 -2.395257 -0.2201198 0.60238129 -2.395257
		 -0.1905285 1.013029814 -2.63065529 0.1905285 1.013029814 -2.63065529 0.1905285 0.63197261 -2.63065529
		 -0.1905285 0.63197261 -2.63065529 -0.1905285 1.013029814 -3.38392687 0.1905285 1.013029814 -3.38392687
		 0.1905285 0.63197261 -3.38392687 -0.1905285 0.63197261 -3.38392687 -0.11411875 0.64694011 0.42382777
		 0.11411875 0.64694011 0.42382777 -0.11411875 0.7258482 0.42382777 0.11411875 0.7258482 0.42382777
		 -0.25314721 0.7258482 0.17789967 0.25314721 0.7258482 0.17789967 -0.25314721 0.64694011 0.17789967
		 0.25314721 0.64694011 0.17789967 -0.035925742 1.034737468 -2.30009532 0.035925742 1.034737468 -2.30009532
		 -0.035925742 1.034737468 -2.37194681 0.035925742 1.034737468 -2.37194681 -0.035925742 1.17989194 -2.30009532
		 -0.035925742 1.17989194 -2.37194681 0.035925742 1.17989194 -2.30009532 0.035925742 1.17989194 -2.37194681
		 -0.04326484 0.33191186 0.98128468 0.04326484 0.33191186 0.98128468 -0.04326484 0.57856798 1.037594795
		 0.04326484 0.57856798 1.037594795 -0.04326484 0.57856798 0.64457273 0.04326484 0.57856798 0.64457273
		 -0.04326484 0.33191186 0.70088297 0.04326484 0.33191186 0.70088297 -0.04326484 0.54087448 1.74200106
		 0.04326484 0.54087448 1.74200106 -0.04326484 0.5785681 1.41729867 0.04326484 0.5785681 1.41729867
		 -0.04326484 0.54087448 1.46159983 0.04326484 0.54087448 1.46159983 -0.04326484 0.54087448 1.95096421
		 0.04326484 0.54087448 1.95096421 0.04326484 0.5785681 1.99526477 -0.04326484 0.5785681 1.99526477
		 -0.034482159 0.39882374 1.44652867 0.034482159 0.39882374 1.44652867 -0.034482159 0.013911873 1.54733884
		 0.034482159 0.013911873 1.54733884 0.034482159 0.084937334 1.82580733 -0.034482159 0.084937334 1.82580733
		 -0.034482159 0.39882374 1.27655149 0.034482159 0.39882374 1.27655149 -0.034482159 0.45664775 1.22587609
		 0.034482159 0.45664775 1.22587609 -0.034482159 0.57706183 1.14007807 0.034482159 0.57706183 1.14007807
		 0.034482159 0.57571232 1.26620603 -0.034482159 0.57571232 1.26620603 -0.034482159 0.45605874 1.15715075
		 0.034482159 0.45605874 1.15715075 0.034482159 0.45629787 1.22603846 -0.034482159 0.45629787 1.22603846
		 -0.017455891 0.50139093 1.37388933 0.017455891 0.50139093 1.37388933 -0.017455891 0.58440262 1.36841404
		 0.017455891 0.58440262 1.36841404 -0.017455891 0.58440262 1.33350205 0.017455891 0.58440262 1.33350205
		 -0.017455891 0.50139093 1.35022688 0.017455891 0.50139093 1.35022688 -0.017455891 0.44219103 1.34644186
		 0.017455891 0.44219103 1.34644186 0.017455891 0.44219103 1.36287415 -0.017455891 0.44219103 1.36287415
		 -0.017455891 0.40889108 1.33450758 0.017455891 0.40889108 1.33450758 -0.083611093 0.7807858 3.12255406
		 0.083611093 0.7807858 3.12255406 -0.083611093 0.83460653 3.12255406 0.083611093 0.83460653 3.12255406
		 -0.083611093 0.83460653 2.95533204 0.083611093 0.83460653 2.95533204 -0.083611093 0.7807858 2.95533204
		 0.083611093 0.7807858 2.95533204;
	setAttr -s 238 ".ed";
	setAttr ".ed[0:165]"  0 1 0 2 3 0 4 5 0 6 7 0 0 2 0 1 3 0 2 4 0 3 5 0 4 6 0
		 5 7 0 6 0 0 7 1 0 0 8 0 1 9 0 8 9 0 3 10 0 9 10 0 2 11 0 11 10 0 8 11 0 12 13 0 10 16 0
		 13 14 0 11 17 0 15 14 0 12 15 0 16 14 0 9 13 0 8 12 0 17 15 0 17 16 1 18 19 0 20 21 0
		 22 23 0 24 25 0 18 20 0 19 21 0 20 22 0 21 23 0 22 24 0 23 25 0 24 18 0 25 19 0 26 27 0
		 26 28 0 27 29 0 28 29 0 26 30 0 28 31 0 30 31 0 27 32 0 29 33 0 32 33 0 34 35 0 34 36 0
		 35 37 0 36 37 0 34 38 0 36 39 0 38 39 0 35 40 0 37 41 0 40 41 0 42 43 0 44 45 0 46 47 0
		 48 49 0 42 44 0 43 45 0 44 46 0 45 47 0 46 48 0 47 49 0 48 42 0 49 43 0 50 51 0 52 53 0
		 54 55 0 56 57 0 50 52 0 51 53 0 52 54 0 53 55 0 54 56 0 55 57 0 56 50 0 57 51 0 58 59 0
		 60 61 0 62 63 0 64 65 0 58 60 0 59 61 0 60 62 0 61 63 0 62 64 0 63 65 0 64 58 0 65 59 0
		 66 67 0 67 68 0 69 68 0 66 69 0 66 70 0 67 71 0 70 71 0 68 72 0 71 72 0 69 73 0 73 72 0
		 70 73 0 70 74 0 71 75 0 74 75 0 72 76 0 75 76 0 73 77 0 77 76 0 74 77 0 74 78 0 75 79 0
		 78 79 0 76 80 0 79 80 0 77 81 0 81 80 0 78 81 0 82 83 0 84 85 0 86 87 0 88 89 0 82 84 0
		 83 85 0 84 86 0 85 87 0 86 88 0 87 89 0 88 82 0 89 83 0 90 91 0 90 92 0 91 93 0 92 93 0
		 90 94 0 92 95 0 94 95 0 91 96 0 93 97 0 96 97 0 98 99 0 100 101 0 102 103 0 104 105 0
		 98 100 0 99 101 0 100 102 0 101 103 0 102 104 0 103 105 0 104 98 0 105 99 0 106 107 0
		 108 109 0 108 110 0 109 111 0 106 112 0;
	setAttr ".ed[166:237]" 107 113 0 112 113 0 109 114 0 113 114 0 108 115 0 115 114 0
		 112 115 0 110 116 0 111 117 0 116 117 0 116 118 0 117 119 0 118 119 0 107 120 0 119 120 0
		 106 121 0 121 120 0 118 121 0 116 122 0 117 123 0 122 123 0 122 124 0 123 125 0 124 125 0
		 110 106 1 111 110 1 111 107 1 126 127 0 127 128 0 129 128 0 126 129 0 126 130 0 127 131 0
		 130 131 0 128 132 0 131 132 0 129 133 0 133 132 0 130 133 0 134 135 0 136 137 0 138 139 0
		 140 141 0 134 136 0 135 137 0 136 138 0 137 139 0 138 140 0 139 141 0 140 142 0 141 143 0
		 142 143 0 135 144 0 134 145 0 145 144 0 142 146 0 143 147 0 146 147 0 144 147 0 145 146 0
		 148 149 0 150 151 0 152 153 0 154 155 0 148 150 0 149 151 0 150 152 0 151 153 0 152 154 0
		 153 155 0 154 148 0 155 149 0;
	setAttr -s 108 -ch 448 ".fc[0:107]" -type "polyFaces" 
		f 4 20 22 -25 -26
		mu 0 4 22 32 33 23
		f 4 1 7 -3 -7
		mu 0 4 4 3 2 5
		f 4 2 9 -4 -9
		f 4 3 11 -1 -11
		mu 0 4 6 8 9 7
		f 4 -12 -10 -8 -6
		mu 0 4 0 1 2 3
		f 4 10 4 6 8
		mu 0 4 6 7 4 5
		f 4 0 13 -15 -13
		mu 0 4 34 35 36 37
		f 4 5 15 -17 -14
		mu 0 4 10 11 12 13
		f 4 -2 17 18 -16
		mu 0 4 24 25 26 27
		f 4 -5 12 19 -18
		mu 0 4 17 18 19 20
		f 4 14 27 -21 -29
		mu 0 4 37 36 38 39
		f 5 16 21 26 -23 -28
		mu 0 5 13 12 14 15 16
		f 4 -19 23 30 -22
		mu 0 4 27 26 28 29
		f 5 -24 -20 28 25 -30
		mu 0 5 21 20 19 22 23
		f 4 -31 29 24 -27
		mu 0 4 29 28 30 31
		f 4 31 36 -33 -36
		mu 0 4 62 52 55 63
		f 4 32 38 -34 -38
		mu 0 4 56 55 54 57
		f 4 33 40 -35 -40
		mu 0 4 57 60 61 58
		f 4 34 42 -32 -42
		f 4 -43 -41 -39 -37
		mu 0 4 52 53 54 55
		f 4 41 35 37 39
		mu 0 4 58 59 56 57
		f 4 43 45 -47 -45
		mu 0 4 78 83 82 79
		f 4 44 48 -50 -48
		mu 0 4 78 79 80 81
		f 4 -46 50 52 -52
		mu 0 4 82 83 84 85
		f 4 53 55 -57 -55
		mu 0 4 86 91 90 87
		f 4 54 58 -60 -58
		mu 0 4 86 87 88 89
		f 4 -56 60 62 -62
		mu 0 4 90 91 92 93
		f 4 63 68 -65 -68
		mu 0 4 74 64 67 75
		f 4 64 70 -66 -70
		mu 0 4 70 67 66 71
		f 4 65 72 -67 -72
		mu 0 4 76 77 72 68
		f 4 66 74 -64 -74
		mu 0 4 68 72 73 69
		f 4 -75 -73 -71 -69
		mu 0 4 64 65 66 67
		f 4 73 67 69 71
		mu 0 4 68 69 70 71
		f 4 75 80 -77 -80
		mu 0 4 99 104 105 100
		f 4 76 82 -78 -82
		mu 0 4 102 97 96 103
		f 4 77 84 -79 -84
		mu 0 4 101 96 95 98
		f 4 78 86 -76 -86
		f 4 -87 -85 -83 -81
		mu 0 4 94 95 96 97
		f 4 85 79 81 83
		mu 0 4 98 99 100 101
		f 4 87 92 -89 -92
		mu 0 4 114 115 116 117
		f 4 88 94 -90 -94
		mu 0 4 130 116 127 131
		f 4 121 123 -126 -127
		mu 0 4 166 167 168 169
		f 4 90 98 -88 -98
		mu 0 4 128 132 133 129
		f 4 -99 -97 -95 -93
		mu 0 4 115 126 127 116
		f 4 97 91 93 95
		mu 0 4 128 129 130 131
		f 4 -96 89 96 -91
		mu 0 4 128 134 135 132
		h 4 -101 -100 102 101
		mu 0 4 136 137 138 139
		f 4 99 104 -106 -104
		mu 0 4 145 140 143 146
		f 4 100 106 -108 -105
		mu 0 4 140 141 142 143
		f 4 -102 108 109 -107
		mu 0 4 148 144 147 149
		f 4 -103 103 110 -109
		mu 0 4 144 145 146 147
		f 4 105 112 -114 -112
		mu 0 4 146 143 154 155
		f 4 107 114 -116 -113
		mu 0 4 143 142 150 151
		f 4 -110 116 117 -115
		mu 0 4 149 147 156 157
		f 4 -111 111 118 -117
		mu 0 4 147 146 152 153
		f 4 113 120 -122 -120
		mu 0 4 155 154 162 163
		f 4 115 122 -124 -121
		mu 0 4 151 150 158 159
		f 4 -118 124 125 -123
		mu 0 4 157 156 164 165
		f 4 -119 119 126 -125
		mu 0 4 153 152 160 161
		f 4 127 132 -129 -132
		f 4 128 134 -130 -134
		mu 0 4 106 107 108 109
		f 4 129 136 -131 -136
		f 4 130 138 -128 -138
		mu 0 4 110 111 112 113
		f 4 -139 -137 -135 -133
		mu 0 4 118 119 120 121
		f 4 137 131 133 135
		mu 0 4 122 123 124 125
		f 4 139 141 -143 -141
		mu 0 4 170 175 174 171
		f 4 140 144 -146 -144
		mu 0 4 170 171 172 173
		f 4 -142 146 148 -148
		mu 0 4 174 175 176 177
		f 4 149 154 -151 -154
		mu 0 4 264 265 266 267
		f 4 150 156 -152 -156
		f 4 151 158 -153 -158
		mu 0 4 268 269 261 260
		f 4 152 160 -150 -160
		mu 0 4 260 261 262 263
		f 4 -161 -159 -157 -155
		mu 0 4 216 217 218 219
		f 4 159 153 155 157
		mu 0 4 220 221 222 223
		f 4 167 169 -172 -173
		mu 0 4 227 226 228 229
		f 4 -163 170 171 -169
		f 4 178 180 -183 -184
		mu 0 4 232 233 234 235
		f 5 165 172 -171 163 190
		mu 0 5 197 198 199 200 201
		f 5 176 183 -182 -191 173
		mu 0 5 202 203 204 197 201
		f 4 161 166 -168 -166
		mu 0 4 224 225 226 227
		f 4 -164 162 164 191
		mu 0 4 236 240 241 237
		f 4 -192 174 -176 -174
		mu 0 4 236 237 238 239
		f 4 -162 181 182 -180
		mu 0 4 225 224 231 230
		f 4 177 -179 -177 175
		mu 0 4 245 242 243 244
		f 5 -193 -165 168 -170 -167
		mu 0 5 178 179 180 181 182
		f 5 179 -181 -178 -175 192
		mu 0 5 178 184 185 183 179
		f 4 175 185 -187 -185
		mu 0 4 246 247 248 249
		f 4 186 188 -190 -188
		mu 0 4 249 248 250 251
		f 4 199 201 -204 -205
		mu 0 4 252 253 254 255
		f 4 193 194 -196 -197
		f 4 193 198 -200 -198
		mu 0 4 256 257 253 252
		f 4 194 200 -202 -199
		mu 0 4 186 187 188 189
		f 4 -196 202 203 -201
		mu 0 4 258 259 255 254
		f 4 -197 197 204 -203
		mu 0 4 205 206 207 208
		f 4 205 210 -207 -210
		mu 0 4 278 279 280 281
		f 4 206 212 -208 -212
		f 4 207 214 -209 -214
		mu 0 4 270 271 272 273
		f 7 -220 209 211 213 215 221 -226
		mu 0 7 209 210 211 212 213 214 215
		f 4 208 216 -218 -216
		mu 0 4 273 272 274 275
		f 4 -206 219 220 -219
		mu 0 4 279 278 282 283
		f 4 217 222 -224 -222
		mu 0 4 275 274 276 277
		f 7 224 -223 -217 -215 -213 -211 218
		mu 0 7 190 191 192 193 194 195 196
		f 4 -221 225 223 -225
		mu 0 4 283 282 284 285
		f 4 226 231 -228 -231
		mu 0 4 48 49 41 40
		f 4 227 233 -229 -233
		mu 0 4 40 41 42 43
		f 4 228 235 -230 -235
		mu 0 4 43 42 50 51
		f 4 229 237 -227 -237
		f 4 -238 -236 -234 -232
		mu 0 4 44 45 42 41
		f 4 236 230 232 234
		mu 0 4 46 47 40 43;
	setAttr ".cd" -type "dataPolyComponent" Index_Data Edge 0 ;
	setAttr ".cvd" -type "dataPolyComponent" Index_Data Vertex 0 ;
	setAttr ".pd[0]" -type "dataPolyComponent" Index_Data UV 71 
		36 0 
		37 0 
		40 0 
		41 0 
		42 0 
		43 0 
		52 0 
		54 0 
		55 0 
		56 0 
		57 0 
		58 0 
		64 0 
		66 0 
		67 0 
		68 0 
		69 0 
		70 0 
		71 0 
		72 0 
		78 0 
		79 0 
		82 0 
		83 0 
		86 0 
		87 0 
		90 0 
		91 0 
		95 0 
		96 0 
		97 0 
		98 0 
		99 0 
		100 0 
		101 0 
		115 0 
		116 0 
		127 0 
		128 0 
		129 0 
		130 0 
		131 0 
		132 0 
		140 0 
		142 0 
		143 0 
		144 0 
		145 0 
		146 0 
		147 0 
		149 0 
		150 0 
		151 0 
		152 0 
		153 0 
		154 0 
		155 0 
		156 0 
		157 0 
		170 0 
		171 0 
		174 0 
		175 0 
		224 0 
		225 0 
		252 0 
		253 0 
		254 0 
		255 0 
		260 0 
		261 0 ;
	setAttr ".hfd" -type "dataPolyComponent" Index_Data Face 0 ;
createNode lightLinker -s -n "lightLinker1";
	rename -uid "DD3BD762-4E76-62FF-3624-9B9B140CAB7A";
	setAttr -s 7 ".lnk";
	setAttr -s 7 ".slnk";
createNode shapeEditorManager -n "shapeEditorManager";
	rename -uid "3DE9985D-4369-E701-0A51-84A85399A787";
createNode poseInterpolatorManager -n "poseInterpolatorManager";
	rename -uid "6DB044E2-4AA1-6A97-5AB4-BDAF94DE3348";
createNode displayLayerManager -n "layerManager";
	rename -uid "66AB3D85-4471-0281-13FF-128A81C95851";
	setAttr ".cdl" 1;
	setAttr -s 2 ".dli[1]"  1;
	setAttr -s 2 ".dli";
createNode displayLayer -n "defaultLayer";
	rename -uid "7A83E266-4E48-2EE0-AE7F-1FBFAE626EE4";
createNode renderLayerManager -n "renderLayerManager";
	rename -uid "AAC8DF43-4F22-CECE-513A-D4BC9957DAFC";
createNode renderLayer -n "defaultRenderLayer";
	rename -uid "27EA34B9-4DD9-11F6-9641-2B9E643A099C";
	setAttr ".g" yes;
createNode displayLayer -n "layer1";
	rename -uid "52E0E06A-4F03-6E0D-8BE3-6DB680861403";
	setAttr ".dt" 2;
	setAttr ".do" 1;
createNode script -n "uiConfigurationScriptNode";
	rename -uid "4E3F952F-4D2A-DB5B-104C-B583F78B931D";
	setAttr ".b" -type "string" (
		"// Maya Mel UI Configuration File.\n//\n//  This script is machine generated.  Edit at your own risk.\n//\n//\n\nglobal string $gMainPane;\nif (`paneLayout -exists $gMainPane`) {\n\n\tglobal int $gUseScenePanelConfig;\n\tint    $useSceneConfig = $gUseScenePanelConfig;\n\tint    $nodeEditorPanelVisible = stringArrayContains(\"nodeEditorPanel1\", `getPanel -vis`);\n\tint    $nodeEditorWorkspaceControlOpen = (`workspaceControl -exists nodeEditorPanel1Window` && `workspaceControl -q -visible nodeEditorPanel1Window`);\n\tint    $menusOkayInPanels = `optionVar -q allowMenusInPanels`;\n\tint    $nVisPanes = `paneLayout -q -nvp $gMainPane`;\n\tint    $nPanes = 0;\n\tstring $editorName;\n\tstring $panelName;\n\tstring $itemFilterName;\n\tstring $panelConfig;\n\n\t//\n\t//  get current state of the UI\n\t//\n\tsceneUIReplacement -update $gMainPane;\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Top View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Top View\")) -mbv $menusOkayInPanels  $panelName;\n"
		+ "\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"top\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 0\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n"
		+ "            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n"
		+ "            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Side View\")) `;\n"
		+ "\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Side View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"side\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 0\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 0\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n"
		+ "            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n"
		+ "            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n            -width 1\n            -height 1\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n"
		+ "\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Front View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Front View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"side\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 1\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 1\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n            -activeComponentsXray 0\n            -displayTextures 0\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n"
		+ "            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n"
		+ "            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n            -strokes 1\n            -motionTrails 1\n            -clipGhosts 1\n            -greasePencils 1\n            -shadows 0\n            -captureSequenceNumber -1\n"
		+ "            -width 436\n            -height 663\n            -sceneRenderFilter 0\n            $editorName;\n        modelEditor -e -viewSelected 0 $editorName;\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\t$panelName = `sceneUIReplacement -getNextPanel \"modelPanel\" (localizedPanelLabel(\"Persp View\")) `;\n\tif (\"\" != $panelName) {\n\t\t$label = `panel -q -label $panelName`;\n\t\tmodelPanel -edit -l (localizedPanelLabel(\"Persp View\")) -mbv $menusOkayInPanels  $panelName;\n\t\t$editorName = $panelName;\n        modelEditor -e \n            -camera \"side\" \n            -useInteractiveMode 0\n            -displayLights \"default\" \n            -displayAppearance \"smoothShaded\" \n            -activeOnly 0\n            -ignorePanZoom 0\n            -wireframeOnShaded 1\n            -headsUpDisplay 1\n            -holdOuts 1\n            -selectionHiliteDisplay 1\n            -useDefaultMaterial 0\n            -bufferMode \"double\" \n            -twoSidedLighting 1\n            -backfaceCulling 0\n            -xray 0\n            -jointXray 0\n"
		+ "            -activeComponentsXray 0\n            -displayTextures 1\n            -smoothWireframe 0\n            -lineWidth 1\n            -textureAnisotropic 0\n            -textureHilight 1\n            -textureSampling 2\n            -textureDisplay \"modulate\" \n            -textureMaxSize 32768\n            -fogging 0\n            -fogSource \"fragment\" \n            -fogMode \"linear\" \n            -fogStart 0\n            -fogEnd 100\n            -fogDensity 0.1\n            -fogColor 0.5 0.5 0.5 1 \n            -depthOfFieldPreview 1\n            -maxConstantTransparency 1\n            -rendererName \"vp2Renderer\" \n            -objectFilterShowInHUD 1\n            -isFiltered 0\n            -colorResolution 256 256 \n            -bumpResolution 512 512 \n            -textureCompression 0\n            -transparencyAlgorithm \"frontAndBackCull\" \n            -transpInShadows 0\n            -cullingOverride \"none\" \n            -lowQualityLighting 0\n            -maximumNumHardwareLights 1\n            -occlusionCulling 0\n            -shadingModel 0\n"
		+ "            -useBaseRenderer 0\n            -useReducedRenderer 0\n            -smallObjectCulling 0\n            -smallObjectThreshold -1 \n            -interactiveDisableShadows 0\n            -interactiveBackFaceCull 0\n            -sortTransparent 1\n            -controllers 1\n            -nurbsCurves 1\n            -nurbsSurfaces 1\n            -polymeshes 1\n            -subdivSurfaces 1\n            -planes 1\n            -lights 1\n            -cameras 1\n            -controlVertices 1\n            -hulls 1\n            -grid 1\n            -imagePlane 1\n            -joints 1\n            -ikHandles 1\n            -deformers 1\n            -dynamics 1\n            -particleInstancers 1\n            -fluids 1\n            -hairSystems 1\n            -follicles 1\n            -nCloths 1\n            -nParticles 1\n            -nRigids 1\n            -dynamicConstraints 1\n            -locators 1\n            -manipulators 1\n            -pluginShapes 1\n            -dimensions 1\n            -handles 1\n            -pivots 1\n            -textures 1\n"
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
		+ "{ string $editorName = ($panelName+\"Editor\");\n            stereoCameraView -e \n                -camera \"persp\" \n                -useInteractiveMode 0\n                -displayLights \"default\" \n                -displayAppearance \"wireframe\" \n                -activeOnly 0\n                -ignorePanZoom 0\n                -wireframeOnShaded 0\n                -headsUpDisplay 1\n                -holdOuts 1\n                -selectionHiliteDisplay 1\n                -useDefaultMaterial 0\n                -bufferMode \"double\" \n                -twoSidedLighting 1\n                -backfaceCulling 0\n                -xray 0\n                -jointXray 0\n                -activeComponentsXray 0\n                -displayTextures 0\n                -smoothWireframe 0\n                -lineWidth 1\n                -textureAnisotropic 0\n                -textureHilight 1\n                -textureSampling 2\n                -textureDisplay \"modulate\" \n                -textureMaxSize 32768\n                -fogging 0\n                -fogSource \"fragment\" \n"
		+ "                -fogMode \"linear\" \n                -fogStart 0\n                -fogEnd 100\n                -fogDensity 0.1\n                -fogColor 0.5 0.5 0.5 1 \n                -depthOfFieldPreview 1\n                -maxConstantTransparency 1\n                -objectFilterShowInHUD 1\n                -isFiltered 0\n                -colorResolution 4 4 \n                -bumpResolution 4 4 \n                -textureCompression 0\n                -transparencyAlgorithm \"frontAndBackCull\" \n                -transpInShadows 0\n                -cullingOverride \"none\" \n                -lowQualityLighting 0\n                -maximumNumHardwareLights 0\n                -occlusionCulling 0\n                -shadingModel 0\n                -useBaseRenderer 0\n                -useReducedRenderer 0\n                -smallObjectCulling 0\n                -smallObjectThreshold -1 \n                -interactiveDisableShadows 0\n                -interactiveBackFaceCull 0\n                -sortTransparent 1\n                -controllers 1\n                -nurbsCurves 1\n"
		+ "                -nurbsSurfaces 1\n                -polymeshes 1\n                -subdivSurfaces 1\n                -planes 1\n                -lights 1\n                -cameras 1\n                -controlVertices 1\n                -hulls 1\n                -grid 1\n                -imagePlane 1\n                -joints 1\n                -ikHandles 1\n                -deformers 1\n                -dynamics 1\n                -particleInstancers 1\n                -fluids 1\n                -hairSystems 1\n                -follicles 1\n                -nCloths 1\n                -nParticles 1\n                -nRigids 1\n                -dynamicConstraints 1\n                -locators 1\n                -manipulators 1\n                -pluginShapes 1\n                -dimensions 1\n                -handles 1\n                -pivots 1\n                -textures 1\n                -strokes 1\n                -motionTrails 1\n                -clipGhosts 1\n                -greasePencils 1\n                -shadows 0\n                -captureSequenceNumber -1\n"
		+ "                -width 0\n                -height 0\n                -sceneRenderFilter 0\n                -displayMode \"centerEye\" \n                -viewColor 0 0 0 1 \n                -useCustomBackground 1\n                $editorName;\n            stereoCameraView -e -viewSelected 0 $editorName; };\n\t\tif (!$useSceneConfig) {\n\t\t\tpanel -e -l $label $panelName;\n\t\t}\n\t}\n\n\n\tif ($useSceneConfig) {\n        string $configName = `getPanel -cwl (localizedPanelLabel(\"Current Layout\"))`;\n        if (\"\" != $configName) {\n\t\t\tpanelConfiguration -edit -label (localizedPanelLabel(\"Current Layout\")) \n\t\t\t\t-userCreated false\n\t\t\t\t-defaultImage \"vacantCell.xP:/\"\n\t\t\t\t-image \"\"\n\t\t\t\t-sc false\n\t\t\t\t-configString \"global string $gMainPane; paneLayout -e -cn \\\"single\\\" -ps 1 100 100 $gMainPane;\"\n\t\t\t\t-removeAllPanels\n\t\t\t\t-ap false\n\t\t\t\t\t(localizedPanelLabel(\"Persp View\")) \n\t\t\t\t\t\"modelPanel\"\n"
		+ "\t\t\t\t\t\"$panelName = `modelPanel -unParent -l (localizedPanelLabel(\\\"Persp View\\\")) -mbv $menusOkayInPanels `;\\n$editorName = $panelName;\\nmodelEditor -e \\n    -camera \\\"side\\\" \\n    -useInteractiveMode 0\\n    -displayLights \\\"default\\\" \\n    -displayAppearance \\\"smoothShaded\\\" \\n    -activeOnly 0\\n    -ignorePanZoom 0\\n    -wireframeOnShaded 1\\n    -headsUpDisplay 1\\n    -holdOuts 1\\n    -selectionHiliteDisplay 1\\n    -useDefaultMaterial 0\\n    -bufferMode \\\"double\\\" \\n    -twoSidedLighting 1\\n    -backfaceCulling 0\\n    -xray 0\\n    -jointXray 0\\n    -activeComponentsXray 0\\n    -displayTextures 1\\n    -smoothWireframe 0\\n    -lineWidth 1\\n    -textureAnisotropic 0\\n    -textureHilight 1\\n    -textureSampling 2\\n    -textureDisplay \\\"modulate\\\" \\n    -textureMaxSize 32768\\n    -fogging 0\\n    -fogSource \\\"fragment\\\" \\n    -fogMode \\\"linear\\\" \\n    -fogStart 0\\n    -fogEnd 100\\n    -fogDensity 0.1\\n    -fogColor 0.5 0.5 0.5 1 \\n    -depthOfFieldPreview 1\\n    -maxConstantTransparency 1\\n    -rendererName \\\"vp2Renderer\\\" \\n    -objectFilterShowInHUD 1\\n    -isFiltered 0\\n    -colorResolution 256 256 \\n    -bumpResolution 512 512 \\n    -textureCompression 0\\n    -transparencyAlgorithm \\\"frontAndBackCull\\\" \\n    -transpInShadows 0\\n    -cullingOverride \\\"none\\\" \\n    -lowQualityLighting 0\\n    -maximumNumHardwareLights 1\\n    -occlusionCulling 0\\n    -shadingModel 0\\n    -useBaseRenderer 0\\n    -useReducedRenderer 0\\n    -smallObjectCulling 0\\n    -smallObjectThreshold -1 \\n    -interactiveDisableShadows 0\\n    -interactiveBackFaceCull 0\\n    -sortTransparent 1\\n    -controllers 1\\n    -nurbsCurves 1\\n    -nurbsSurfaces 1\\n    -polymeshes 1\\n    -subdivSurfaces 1\\n    -planes 1\\n    -lights 1\\n    -cameras 1\\n    -controlVertices 1\\n    -hulls 1\\n    -grid 1\\n    -imagePlane 1\\n    -joints 1\\n    -ikHandles 1\\n    -deformers 1\\n    -dynamics 1\\n    -particleInstancers 1\\n    -fluids 1\\n    -hairSystems 1\\n    -follicles 1\\n    -nCloths 1\\n    -nParticles 1\\n    -nRigids 1\\n    -dynamicConstraints 1\\n    -locators 1\\n    -manipulators 1\\n    -pluginShapes 1\\n    -dimensions 1\\n    -handles 1\\n    -pivots 1\\n    -textures 1\\n    -strokes 1\\n    -motionTrails 1\\n    -clipGhosts 1\\n    -greasePencils 1\\n    -shadows 0\\n    -captureSequenceNumber -1\\n    -width 1054\\n    -height 663\\n    -sceneRenderFilter 0\\n    $editorName;\\nmodelEditor -e -viewSelected 0 $editorName\"\n"
		+ "\t\t\t\t\t\"modelPanel -edit -l (localizedPanelLabel(\\\"Persp View\\\")) -mbv $menusOkayInPanels  $panelName;\\n$editorName = $panelName;\\nmodelEditor -e \\n    -camera \\\"side\\\" \\n    -useInteractiveMode 0\\n    -displayLights \\\"default\\\" \\n    -displayAppearance \\\"smoothShaded\\\" \\n    -activeOnly 0\\n    -ignorePanZoom 0\\n    -wireframeOnShaded 1\\n    -headsUpDisplay 1\\n    -holdOuts 1\\n    -selectionHiliteDisplay 1\\n    -useDefaultMaterial 0\\n    -bufferMode \\\"double\\\" \\n    -twoSidedLighting 1\\n    -backfaceCulling 0\\n    -xray 0\\n    -jointXray 0\\n    -activeComponentsXray 0\\n    -displayTextures 1\\n    -smoothWireframe 0\\n    -lineWidth 1\\n    -textureAnisotropic 0\\n    -textureHilight 1\\n    -textureSampling 2\\n    -textureDisplay \\\"modulate\\\" \\n    -textureMaxSize 32768\\n    -fogging 0\\n    -fogSource \\\"fragment\\\" \\n    -fogMode \\\"linear\\\" \\n    -fogStart 0\\n    -fogEnd 100\\n    -fogDensity 0.1\\n    -fogColor 0.5 0.5 0.5 1 \\n    -depthOfFieldPreview 1\\n    -maxConstantTransparency 1\\n    -rendererName \\\"vp2Renderer\\\" \\n    -objectFilterShowInHUD 1\\n    -isFiltered 0\\n    -colorResolution 256 256 \\n    -bumpResolution 512 512 \\n    -textureCompression 0\\n    -transparencyAlgorithm \\\"frontAndBackCull\\\" \\n    -transpInShadows 0\\n    -cullingOverride \\\"none\\\" \\n    -lowQualityLighting 0\\n    -maximumNumHardwareLights 1\\n    -occlusionCulling 0\\n    -shadingModel 0\\n    -useBaseRenderer 0\\n    -useReducedRenderer 0\\n    -smallObjectCulling 0\\n    -smallObjectThreshold -1 \\n    -interactiveDisableShadows 0\\n    -interactiveBackFaceCull 0\\n    -sortTransparent 1\\n    -controllers 1\\n    -nurbsCurves 1\\n    -nurbsSurfaces 1\\n    -polymeshes 1\\n    -subdivSurfaces 1\\n    -planes 1\\n    -lights 1\\n    -cameras 1\\n    -controlVertices 1\\n    -hulls 1\\n    -grid 1\\n    -imagePlane 1\\n    -joints 1\\n    -ikHandles 1\\n    -deformers 1\\n    -dynamics 1\\n    -particleInstancers 1\\n    -fluids 1\\n    -hairSystems 1\\n    -follicles 1\\n    -nCloths 1\\n    -nParticles 1\\n    -nRigids 1\\n    -dynamicConstraints 1\\n    -locators 1\\n    -manipulators 1\\n    -pluginShapes 1\\n    -dimensions 1\\n    -handles 1\\n    -pivots 1\\n    -textures 1\\n    -strokes 1\\n    -motionTrails 1\\n    -clipGhosts 1\\n    -greasePencils 1\\n    -shadows 0\\n    -captureSequenceNumber -1\\n    -width 1054\\n    -height 663\\n    -sceneRenderFilter 0\\n    $editorName;\\nmodelEditor -e -viewSelected 0 $editorName\"\n"
		+ "\t\t\t\t$configName;\n\n            setNamedPanelLayout (localizedPanelLabel(\"Current Layout\"));\n        }\n\n        panelHistory -e -clear mainPanelHistory;\n        sceneUIReplacement -clear;\n\t}\n\n\ngrid -spacing 1 -size 1 -divisions 9 -displayAxes yes -displayGridLines yes -displayDivisionLines yes -displayPerspectiveLabels no -displayOrthographicLabels no -displayAxesBold yes -perspectiveLabelPosition axis -orthographicLabelPosition edge;\nviewManip -drawCompass 0 -compassAngle 0 -frontParameters \"\" -homeParameters \"\" -selectionLockParameters \"\";\n}\n");
	setAttr ".st" 3;
createNode script -n "sceneConfigurationScriptNode";
	rename -uid "66AEC729-4164-3059-18F0-AAA81556F4BC";
	setAttr ".b" -type "string" "playbackOptions -min 1 -max 120 -ast 1 -aet 200 ";
	setAttr ".st" 6;
createNode file -n "file1";
	rename -uid "C5D20B9C-4D84-D713-86C5-B586AD2995BD";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/t21model/t21Layout.png";
	setAttr ".ft" 0;
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "place2dTexture1";
	rename -uid "7AEB9DFE-4B7F-A087-DB09-D58E85655991";
createNode shadingEngine -n "lambert3SG";
	rename -uid "21B1B4DF-4D47-A955-78D7-2982EB52B14F";
	setAttr ".ihi" 0;
	setAttr ".ro" yes;
createNode materialInfo -n "materialInfo1";
	rename -uid "5F103ABB-4B39-9B49-39FF-819C3E1C7554";
createNode lambert -n "lambert3SG1";
	rename -uid "ABDCFA3D-4AA4-ECBF-C39B-F7966DE6BBD0";
createNode file -n "lambert3SG1F";
	rename -uid "9BF763DA-4E11-4F15-8833-7AA8E7B24F78";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/bowcasterModel/bowcasterUVlayout.png";
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "lambert3SG1P2D";
	rename -uid "7D083015-44CA-34D5-148F-5F8001CC6ADB";
createNode shadingEngine -n "dl18model_lambert3SG";
	rename -uid "1A38A1DF-4A11-010B-7C01-E1874810D105";
	setAttr ".ihi" 0;
	setAttr ".ro" yes;
createNode materialInfo -n "dl18model_materialInfo1";
	rename -uid "D7A0320B-49D5-7851-A49F-E29AF17EF95F";
createNode lambert -n "dl18model_lambert3SG1";
	rename -uid "9DEB2599-4F71-0052-0BC3-4E82B67A2810";
createNode file -n "dl18model_lambert3SG1F";
	rename -uid "655CCEFC-41FB-0E41-208C-6BBDECFB15B2";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/DL18model/uvLayoutDL18.png";
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "dl18model_lambert3SG1P2D";
	rename -uid "C309B845-4C2E-6B4D-9742-29A84B1805AF";
createNode shadingEngine -n "lambert2SG";
	rename -uid "FCBCAE1D-4675-3236-9404-81B620B66437";
	setAttr ".ihi" 0;
	setAttr ".ro" yes;
createNode materialInfo -n "dl44model_materialInfo1";
	rename -uid "DB482C35-47BF-2DB2-99A4-66BFF6618446";
createNode lambert -n "lambert2SG1";
	rename -uid "4B601C58-4F28-7764-1464-EDB4E48C511F";
createNode file -n "lambert2SG1F";
	rename -uid "1F67C2C5-4131-9C70-3EF9-0E9CB21475A4";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/New Folder/dl44layout.png";
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "lambert2SG1P2D";
	rename -uid "E1CA2F31-448C-28A0-6C8A-7D85E211B262";
createNode shadingEngine -n "v11_lambert3SG";
	rename -uid "449CF642-4253-A969-8E7F-00B3C0F61FA4";
	setAttr ".ihi" 0;
	setAttr ".ro" yes;
createNode materialInfo -n "v11_materialInfo1";
	rename -uid "68A597D1-4F87-B78C-B486-E18CC44FC111";
createNode lambert -n "v11_lambert3SG1";
	rename -uid "0E9782AF-4613-52F9-97FA-4A84A7456DFF";
createNode file -n "v11_lambert3SG1F";
	rename -uid "CA97366A-44DE-3D19-B0A7-148B40E6CE67";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/se14cUPDATE/se14cLayout.png";
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "v11_lambert3SG1P2D";
	rename -uid "7AFBF539-4C13-C09C-5859-5194829E7F16";
createNode shadingEngine -n "tuskenCyclerModel_lambert3SG";
	rename -uid "C85C2345-4AAA-2B9A-4737-1B9ECA94E495";
	setAttr ".ihi" 0;
	setAttr ".ro" yes;
createNode materialInfo -n "tuskenCyclerModel_materialInfo1";
	rename -uid "A013BB3C-481F-06F5-1B23-BABBE6DE0837";
createNode lambert -n "tuskenCyclerModel_lambert3SG1";
	rename -uid "4EF06F82-41B6-89BA-41DE-C390E63F27D8";
createNode file -n "tuskenCyclerModel_lambert3SG1F";
	rename -uid "AAB04BBB-48E7-7D0A-9AD5-9FBBE0DD456D";
	setAttr ".ftn" -type "string" "C:/Users/Liam - Moose/Desktop/PSWGM/tuskenCyclerModel/tuskenCyclerUVlayout.png";
	setAttr ".cs" -type "string" "sRGB";
createNode place2dTexture -n "tuskenCyclerModel_lambert3SG1P2D";
	rename -uid "89A6EA02-4ABF-2E15-E85C-4E9DED5D4A5E";
createNode groupId -n "groupId1";
	rename -uid "EB3A18A4-44A9-A692-F3D1-B1A1C6B5EF79";
	setAttr ".ihi" 0;
createNode groupParts -n "groupParts1";
	rename -uid "1CD1CFF4-46BB-5243-45E1-04AD4D6C18F1";
	setAttr ".ihi" 0;
	setAttr ".ic" -type "componentList" 1 "f[0:107]";
createNode polyPinUV -n "polyPinUV1";
	rename -uid "FF627BDD-409E-9958-3A47-C690D4A95B44";
	setAttr ".uopa" yes;
	setAttr ".ics" -type "componentList" 21 "map[36:37]" "map[40:43]" "map[52]" "map[54:58]" "map[64]" "map[66:72]" "map[78:79]" "map[82:83]" "map[86:87]" "map[90:91]" "map[95:101]" "map[115:116]" "map[127:132]" "map[140]" "map[142:147]" "map[149:157]" "map[170:171]" "map[174:175]" "map[224:235]" "map[252:255]" "map[260:261]";
	setAttr -s 81 ".pn";
	setAttr ".pn[36]" 0;
	setAttr ".pn[37]" 0;
	setAttr ".pn[40]" 0;
	setAttr ".pn[41]" 0;
	setAttr ".pn[42]" 0;
	setAttr ".pn[43]" 0;
	setAttr ".pn[52]" 0;
	setAttr ".pn[54]" 0;
	setAttr ".pn[55]" 0;
	setAttr ".pn[56]" 0;
	setAttr ".pn[57]" 0;
	setAttr ".pn[58]" 0;
	setAttr ".pn[64]" 0;
	setAttr ".pn[66]" 0;
	setAttr ".pn[67]" 0;
	setAttr ".pn[68]" 0;
	setAttr ".pn[69]" 0;
	setAttr ".pn[70]" 0;
	setAttr ".pn[71]" 0;
	setAttr ".pn[72]" 0;
	setAttr ".pn[78]" 0;
	setAttr ".pn[79]" 0;
	setAttr ".pn[82]" 0;
	setAttr ".pn[83]" 0;
	setAttr ".pn[86]" 0;
	setAttr ".pn[87]" 0;
	setAttr ".pn[90]" 0;
	setAttr ".pn[91]" 0;
	setAttr ".pn[95]" 0;
	setAttr ".pn[96]" 0;
	setAttr ".pn[97]" 0;
	setAttr ".pn[98]" 0;
	setAttr ".pn[99]" 0;
	setAttr ".pn[100]" 0;
	setAttr ".pn[101]" 0;
	setAttr ".pn[115]" 0;
	setAttr ".pn[116]" 0;
	setAttr ".pn[127]" 0;
	setAttr ".pn[128]" 0;
	setAttr ".pn[129]" 0;
	setAttr ".pn[130]" 0;
	setAttr ".pn[131]" 0;
	setAttr ".pn[132]" 0;
	setAttr ".pn[140]" 0;
	setAttr ".pn[142]" 0;
	setAttr ".pn[143]" 0;
	setAttr ".pn[144]" 0;
	setAttr ".pn[145]" 0;
	setAttr ".pn[146]" 0;
	setAttr ".pn[147]" 0;
	setAttr ".pn[149]" 0;
	setAttr ".pn[150]" 0;
	setAttr ".pn[151]" 0;
	setAttr ".pn[152]" 0;
	setAttr ".pn[153]" 0;
	setAttr ".pn[154]" 0;
	setAttr ".pn[155]" 0;
	setAttr ".pn[156]" 0;
	setAttr ".pn[157]" 0;
	setAttr ".pn[170]" 0;
	setAttr ".pn[171]" 0;
	setAttr ".pn[174]" 0;
	setAttr ".pn[175]" 0;
	setAttr ".pn[224]" 1;
	setAttr ".pn[225]" 1;
	setAttr ".pn[226]" 1;
	setAttr ".pn[227]" 1;
	setAttr ".pn[228]" 1;
	setAttr ".pn[229]" 1;
	setAttr ".pn[230]" 1;
	setAttr ".pn[231]" 1;
	setAttr ".pn[232]" 1;
	setAttr ".pn[233]" 1;
	setAttr ".pn[234]" 1;
	setAttr ".pn[235]" 1;
	setAttr ".pn[252]" 0;
	setAttr ".pn[253]" 0;
	setAttr ".pn[254]" 0;
	setAttr ".pn[255]" 0;
	setAttr ".pn[260]" 0;
	setAttr ".pn[261]" 0;
createNode polyTweakUV -n "polyTweakUV1";
	rename -uid "B191ACB6-43B4-DEF6-A4C7-4A8ED3950C1D";
	setAttr ".uopa" yes;
	setAttr -s 17 ".uvtk";
	setAttr ".uvtk[178]" -type "float2" 0.0067371167 0 ;
	setAttr ".uvtk[184]" -type "float2" 0.0025263983 0 ;
	setAttr ".uvtk[185]" -type "float2" 0.0025263983 0 ;
	setAttr ".uvtk[197]" -type "float2" 0.0067371167 0 ;
	setAttr ".uvtk[203]" -type "float2" 0.0025263983 0 ;
	setAttr ".uvtk[204]" -type "float2" 0.0025263983 0 ;
	setAttr ".uvtk[242]" -type "float2" 0.0025263983 0 ;
	setAttr ".uvtk[243]" -type "float2" 0.0025263983 0 ;
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
	setAttr -s 7 ".st";
select -ne :renderGlobalsList1;
select -ne :defaultShaderList1;
	setAttr -s 10 ".s";
select -ne :postProcessList1;
	setAttr -s 2 ".p";
select -ne :defaultRenderUtilityList1;
	setAttr -s 6 ".u";
select -ne :defaultRenderingList1;
select -ne :defaultTextureList1;
	setAttr -s 6 ".tx";
select -ne :lambert1;
select -ne :initialShadingGroup;
	setAttr ".ro" yes;
select -ne :initialParticleSE;
	setAttr ".ro" yes;
select -ne :initialMaterialInfo;
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
connectAttr "groupId1.id" "group2Shape.iog.og[0].gid";
connectAttr ":initialShadingGroup.mwc" "group2Shape.iog.og[0].gco";
connectAttr "polyTweakUV1.out" "group2Shape.i";
connectAttr "polyTweakUV1.uvtk[0]" "group2Shape.uvst[0].uvtw";
relationship "link" ":lightLinker1" ":initialShadingGroup.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" ":initialParticleSE.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" "lambert3SG.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" "dl18model_lambert3SG.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" "lambert2SG.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" "v11_lambert3SG.message" ":defaultLightSet.message";
relationship "link" ":lightLinker1" "tuskenCyclerModel_lambert3SG.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" ":initialShadingGroup.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" ":initialParticleSE.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" "lambert3SG.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" "dl18model_lambert3SG.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" "lambert2SG.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" "v11_lambert3SG.message" ":defaultLightSet.message";
relationship "shadowLink" ":lightLinker1" "tuskenCyclerModel_lambert3SG.message" ":defaultLightSet.message";
connectAttr "layerManager.dli[0]" "defaultLayer.id";
connectAttr "renderLayerManager.rlmi[0]" "defaultRenderLayer.rlid";
connectAttr "layerManager.dli[1]" "layer1.id";
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
connectAttr "lambert3SG1.oc" "lambert3SG.ss";
connectAttr "lambert3SG.msg" "materialInfo1.sg";
connectAttr "lambert3SG1.msg" "materialInfo1.m";
connectAttr "lambert3SG1F.msg" "materialInfo1.t" -na;
connectAttr "lambert3SG1F.oc" "lambert3SG1.c";
connectAttr "lambert3SG1P2D.c" "lambert3SG1F.c";
connectAttr "lambert3SG1P2D.tf" "lambert3SG1F.tf";
connectAttr "lambert3SG1P2D.rf" "lambert3SG1F.rf";
connectAttr "lambert3SG1P2D.s" "lambert3SG1F.s";
connectAttr "lambert3SG1P2D.wu" "lambert3SG1F.wu";
connectAttr "lambert3SG1P2D.wv" "lambert3SG1F.wv";
connectAttr "lambert3SG1P2D.re" "lambert3SG1F.re";
connectAttr "lambert3SG1P2D.of" "lambert3SG1F.of";
connectAttr "lambert3SG1P2D.r" "lambert3SG1F.ro";
connectAttr "lambert3SG1P2D.o" "lambert3SG1F.uv";
connectAttr "lambert3SG1P2D.ofs" "lambert3SG1F.fs";
connectAttr ":defaultColorMgtGlobals.cme" "lambert3SG1F.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "lambert3SG1F.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "lambert3SG1F.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "lambert3SG1F.ws";
connectAttr "dl18model_lambert3SG1.oc" "dl18model_lambert3SG.ss";
connectAttr "dl18model_lambert3SG.msg" "dl18model_materialInfo1.sg";
connectAttr "dl18model_lambert3SG1.msg" "dl18model_materialInfo1.m";
connectAttr "dl18model_lambert3SG1F.msg" "dl18model_materialInfo1.t" -na;
connectAttr "dl18model_lambert3SG1F.oc" "dl18model_lambert3SG1.c";
connectAttr "dl18model_lambert3SG1P2D.c" "dl18model_lambert3SG1F.c";
connectAttr "dl18model_lambert3SG1P2D.tf" "dl18model_lambert3SG1F.tf";
connectAttr "dl18model_lambert3SG1P2D.rf" "dl18model_lambert3SG1F.rf";
connectAttr "dl18model_lambert3SG1P2D.s" "dl18model_lambert3SG1F.s";
connectAttr "dl18model_lambert3SG1P2D.wu" "dl18model_lambert3SG1F.wu";
connectAttr "dl18model_lambert3SG1P2D.wv" "dl18model_lambert3SG1F.wv";
connectAttr "dl18model_lambert3SG1P2D.re" "dl18model_lambert3SG1F.re";
connectAttr "dl18model_lambert3SG1P2D.of" "dl18model_lambert3SG1F.of";
connectAttr "dl18model_lambert3SG1P2D.r" "dl18model_lambert3SG1F.ro";
connectAttr "dl18model_lambert3SG1P2D.o" "dl18model_lambert3SG1F.uv";
connectAttr "dl18model_lambert3SG1P2D.ofs" "dl18model_lambert3SG1F.fs";
connectAttr ":defaultColorMgtGlobals.cme" "dl18model_lambert3SG1F.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "dl18model_lambert3SG1F.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "dl18model_lambert3SG1F.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "dl18model_lambert3SG1F.ws";
connectAttr "lambert2SG1.oc" "lambert2SG.ss";
connectAttr "lambert2SG.msg" "dl44model_materialInfo1.sg";
connectAttr "lambert2SG1.msg" "dl44model_materialInfo1.m";
connectAttr "lambert2SG1F.msg" "dl44model_materialInfo1.t" -na;
connectAttr "lambert2SG1F.oc" "lambert2SG1.c";
connectAttr "lambert2SG1P2D.c" "lambert2SG1F.c";
connectAttr "lambert2SG1P2D.tf" "lambert2SG1F.tf";
connectAttr "lambert2SG1P2D.rf" "lambert2SG1F.rf";
connectAttr "lambert2SG1P2D.s" "lambert2SG1F.s";
connectAttr "lambert2SG1P2D.wu" "lambert2SG1F.wu";
connectAttr "lambert2SG1P2D.wv" "lambert2SG1F.wv";
connectAttr "lambert2SG1P2D.re" "lambert2SG1F.re";
connectAttr "lambert2SG1P2D.of" "lambert2SG1F.of";
connectAttr "lambert2SG1P2D.r" "lambert2SG1F.ro";
connectAttr "lambert2SG1P2D.o" "lambert2SG1F.uv";
connectAttr "lambert2SG1P2D.ofs" "lambert2SG1F.fs";
connectAttr ":defaultColorMgtGlobals.cme" "lambert2SG1F.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "lambert2SG1F.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "lambert2SG1F.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "lambert2SG1F.ws";
connectAttr "v11_lambert3SG1.oc" "v11_lambert3SG.ss";
connectAttr "v11_lambert3SG.msg" "v11_materialInfo1.sg";
connectAttr "v11_lambert3SG1.msg" "v11_materialInfo1.m";
connectAttr "v11_lambert3SG1F.msg" "v11_materialInfo1.t" -na;
connectAttr "v11_lambert3SG1F.oc" "v11_lambert3SG1.c";
connectAttr "v11_lambert3SG1P2D.c" "v11_lambert3SG1F.c";
connectAttr "v11_lambert3SG1P2D.tf" "v11_lambert3SG1F.tf";
connectAttr "v11_lambert3SG1P2D.rf" "v11_lambert3SG1F.rf";
connectAttr "v11_lambert3SG1P2D.s" "v11_lambert3SG1F.s";
connectAttr "v11_lambert3SG1P2D.wu" "v11_lambert3SG1F.wu";
connectAttr "v11_lambert3SG1P2D.wv" "v11_lambert3SG1F.wv";
connectAttr "v11_lambert3SG1P2D.re" "v11_lambert3SG1F.re";
connectAttr "v11_lambert3SG1P2D.of" "v11_lambert3SG1F.of";
connectAttr "v11_lambert3SG1P2D.r" "v11_lambert3SG1F.ro";
connectAttr "v11_lambert3SG1P2D.o" "v11_lambert3SG1F.uv";
connectAttr "v11_lambert3SG1P2D.ofs" "v11_lambert3SG1F.fs";
connectAttr ":defaultColorMgtGlobals.cme" "v11_lambert3SG1F.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "v11_lambert3SG1F.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "v11_lambert3SG1F.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "v11_lambert3SG1F.ws";
connectAttr "tuskenCyclerModel_lambert3SG1.oc" "tuskenCyclerModel_lambert3SG.ss"
		;
connectAttr "tuskenCyclerModel_lambert3SG.msg" "tuskenCyclerModel_materialInfo1.sg"
		;
connectAttr "tuskenCyclerModel_lambert3SG1.msg" "tuskenCyclerModel_materialInfo1.m"
		;
connectAttr "tuskenCyclerModel_lambert3SG1F.msg" "tuskenCyclerModel_materialInfo1.t"
		 -na;
connectAttr "tuskenCyclerModel_lambert3SG1F.oc" "tuskenCyclerModel_lambert3SG1.c"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.c" "tuskenCyclerModel_lambert3SG1F.c"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.tf" "tuskenCyclerModel_lambert3SG1F.tf"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.rf" "tuskenCyclerModel_lambert3SG1F.rf"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.s" "tuskenCyclerModel_lambert3SG1F.s"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.wu" "tuskenCyclerModel_lambert3SG1F.wu"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.wv" "tuskenCyclerModel_lambert3SG1F.wv"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.re" "tuskenCyclerModel_lambert3SG1F.re"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.of" "tuskenCyclerModel_lambert3SG1F.of"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.r" "tuskenCyclerModel_lambert3SG1F.ro"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.o" "tuskenCyclerModel_lambert3SG1F.uv"
		;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.ofs" "tuskenCyclerModel_lambert3SG1F.fs"
		;
connectAttr ":defaultColorMgtGlobals.cme" "tuskenCyclerModel_lambert3SG1F.cme";
connectAttr ":defaultColorMgtGlobals.cfe" "tuskenCyclerModel_lambert3SG1F.cmcf";
connectAttr ":defaultColorMgtGlobals.cfp" "tuskenCyclerModel_lambert3SG1F.cmcp";
connectAttr ":defaultColorMgtGlobals.wsn" "tuskenCyclerModel_lambert3SG1F.ws";
connectAttr "polySurfaceShape2.o" "groupParts1.ig";
connectAttr "groupId1.id" "groupParts1.gi";
connectAttr "groupParts1.og" "polyPinUV1.ip";
connectAttr "polyPinUV1.out" "polyTweakUV1.ip";
connectAttr "lambert3SG.pa" ":renderPartition.st" -na;
connectAttr "dl18model_lambert3SG.pa" ":renderPartition.st" -na;
connectAttr "lambert2SG.pa" ":renderPartition.st" -na;
connectAttr "v11_lambert3SG.pa" ":renderPartition.st" -na;
connectAttr "tuskenCyclerModel_lambert3SG.pa" ":renderPartition.st" -na;
connectAttr "lambert3SG1.msg" ":defaultShaderList1.s" -na;
connectAttr "dl18model_lambert3SG1.msg" ":defaultShaderList1.s" -na;
connectAttr "lambert2SG1.msg" ":defaultShaderList1.s" -na;
connectAttr "v11_lambert3SG1.msg" ":defaultShaderList1.s" -na;
connectAttr "tuskenCyclerModel_lambert3SG1.msg" ":defaultShaderList1.s" -na;
connectAttr "place2dTexture1.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "lambert3SG1P2D.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "dl18model_lambert3SG1P2D.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "lambert2SG1P2D.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "v11_lambert3SG1P2D.msg" ":defaultRenderUtilityList1.u" -na;
connectAttr "tuskenCyclerModel_lambert3SG1P2D.msg" ":defaultRenderUtilityList1.u"
		 -na;
connectAttr "defaultRenderLayer.msg" ":defaultRenderingList1.r" -na;
connectAttr "file1.msg" ":defaultTextureList1.tx" -na;
connectAttr "lambert3SG1F.msg" ":defaultTextureList1.tx" -na;
connectAttr "dl18model_lambert3SG1F.msg" ":defaultTextureList1.tx" -na;
connectAttr "lambert2SG1F.msg" ":defaultTextureList1.tx" -na;
connectAttr "v11_lambert3SG1F.msg" ":defaultTextureList1.tx" -na;
connectAttr "tuskenCyclerModel_lambert3SG1F.msg" ":defaultTextureList1.tx" -na;
connectAttr "file1.oc" ":lambert1.c";
connectAttr "file1.ot" ":lambert1.it";
connectAttr "group2Shape.iog.og[0]" ":initialShadingGroup.dsm" -na;
connectAttr "groupId1.msg" ":initialShadingGroup.gn" -na;
connectAttr "file1.msg" ":initialMaterialInfo.t" -na;
// End of v06.ma
