Global Version#=1.7

AppTitle "Tank Battle v"+Version#

Global timer=CreateTimer(60)

;setup system stuff
SetBuffer(BackBuffer())
HidePointer()

;load game
Global name$=GetEnv("USERNAME")
Global gametype#=1
Global control#=1
Global control2#=2
Global consoleout=False
Global maxtrax#=0
Global traxlist$="trax.txt"
HidePointer()

If FileType("game.inf")=1 Then

	information=ReadFile("game.inf")
	name$=ReadLine(information)
	filename$=ReadLine(information)
	control#=ReadLine(information)
	traxlist$=ReadLine(information)
	CloseFile information

EndIf

;Include "CDKeyTest.bb"
m1#=4
m2#=6
m3#=2
m4#=9
;inputkey(0)

Include "launcher3d.bb"
Include "GNET_inc.bb"
Include "BPliteV1.14.bb"

;setup random
SeedRnd(MilliSecs())

;setup gfx stuff
SetBuffer(BackBuffer())

;Load GFX
Global gfxbackground=LoadImage("gfx/background.bmp")
ResizeImage gfxbackground,GraphicsWidth(),GraphicsHeight()
Global gfxpoint=LoadAnimImage("gfx/point.bmp",32,32,0,4)
Global gfxoneplayer=LoadImage("gfx/oneplayer.bmp")
Global gfxlogo=LoadImage("gfx/logo.bmp")
MidHandle(gfxlogo)
Global gfxoptions=LoadImage("gfx/options.bmp")
Global gfxoptionsback=LoadImage("gfx/optionsback.bmp")
ResizeImage gfxoptionsback,GraphicsWidth(),GraphicsHeight()
Global gfxcharback=LoadImage("gfx/charback.bmp")
ResizeImage gfxcharback,GraphicsWidth(),GraphicsHeight()
Global gfxstank=LoadImage("gfx/Stank.bmp")
Global gfxrally=LoadImage("gfx/Rally.bmp")
Global damage=LoadAnimImage("gfx/Damage.bmp",100,50,0,11)
Global DamageB=LoadAnimImage("gfx/DamageB.bmp",100,50,0,11)

;define global variables
Global turnposit#=0
Global lstturn#=0
Global goon=False
Global laser=False

Global hits#=0
Global hitsB#=0
Global Bounce=False
Global Bullength=1
Global bgm
Global gravity#=-.06
Global pointrot#=0
Global tank
Global Tank2
Global map
Global tankToneA#=255
Global tankToneB#=255
Global tankToneC#=255
Global track#=0
Global tankAT
Global camera
Global geer#
Global armed=True
Global HangTime
Global LastHang=0
Global reload#=0
Global ReloadB#=0
Global Espeed#=0
Global speed#=0
Global Accel#=0
Global TurnFactor#
Global MaxSpeed#
Global tankname$
Global Sound
Global gamehosted=0
Global x_vel#=0
Global y_vel#=0
Global z_vel#=0
Global showhits#=0
Global showhitsB#=0
Global Rounds#=0
Global LRounds#=0
Global Fired=False
Global wire=False
Global mapcam

; Load SFX
Global sfxBeep=Load3DSound("SFX/BEEP.wav")
Global sfxShoot=Load3DSound("SFX/shoot.wav")
Global sfxAHHHH=Load3DSound("SFX/AHHHH.WAV")
Global TDsfxAHHHH=LoadSound("SFX/AHHHH.WAV")
Global sfxSpawn=Load3DSound("SFX/OPTIMIS.WAV")
Global sfxStart=LoadSound("SFX/INTRO9.WAV")
Global sfxBOOM=Load3DSound("SFX/WORKS.WAV")

Global CHNSFX

	Global spark_sprite=LoadSprite( "GFX/Bigspark.BMP" )
	EntityBlend spark_sprite,2
	SpriteViewMode spark_sprite,2
	HideEntity spark_sprite

;setup types
Type Player
	Field tankO,name$,Net_ID,tank$
End Type

Type track
	Field number#,loadname$
End Type

Type scriptentry
	Field sdat$
End Type

Type Spark
	Field alpha#,sprite
End Type

Type Bullet
	Field sprite,time_out
End Type

Type NetBullet
	Field rot#,sprite,time_out
End Type

Type bot
	Field botBODY,CHANNEL,cam,delaytime#
End Type

Type Info
	Field txt$
End Type

;setup constants
Const BODY=1,WHEEL=2,SCENE=3,TYPE_BULLET=4,POLICE=5,CAMBOX=6,TYPE_GRASS=7,NET=8,NET_BULLET=9,GHOST=10,TYPE_BOT=11

;load main menu
mainmenu()

;main menu
Function mainmenu()

StopChannel(bgm)

Local logoframe#=0

MoveMouse 269,179

FlushMouse()
FlushKeys()

Font=LoadFont("Microsoft Sans Serif",28,0,0,0)
SetFont Font

game=True

While game=True
Cls

If KeyHit(65) Then
	scrshot()
EndIf

	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("bgm/Into_Intro.mp3")
	EndIf
	
DrawImage gfxbackground,0,0

Text 650,0,"Time: "+CurrentTime$()

If KeyHit(1) Then 
writesettings()
savelog()
End
EndIf

DrawImage gfxlogo,GraphicsWidth()/2,GraphicsHeight()/4

Color 225,0,0
Text GraphicsWidth()/2,(GraphicsHeight()/2)-FontHeight()*1,"Play Game",True
Text GraphicsWidth()/2,(GraphicsHeight()/2),"Netplay",True
Text GraphicsWidth()/2,(GraphicsHeight()/2)+FontHeight()*1,"Options",True
If Not First info=Null Then
	inf.info=First info
	Text 0,0,"Info: "+inf\txt$
EndIf

DrawImage gfxpoint,MouseX(),MouseY(),pointrot#
pointrot#=pointrot#+1
If pointrot#=4 Then pointrot#=0

If MouseHit(1) Then
If MouseX()>GraphicsWidth()/2-((StringWidth("Play Game")*FontWidth())/2) And MouseX()<GraphicsWidth()/2+((StringWidth("Play Game")*FontWidth())/2) And MouseY()>(GraphicsHeight()/2)-FontHeight()*1 And MouseY()<((GraphicsHeight()/2)-FontHeight()*1)+StringHeight("Play Game") Then
oneplayerselect("Off")
EndIf
If MouseX()>GraphicsWidth()/2-((StringWidth("Netplay")*FontWidth())/2) And MouseX()<GraphicsWidth()/2+((StringWidth("Play Game")*FontWidth())/2) And MouseY()>(GraphicsHeight()/2) And MouseY()<((GraphicsHeight()/2))+StringHeight("Netplay") Then
oneplayerselect("On")
EndIf
If MouseX()>GraphicsWidth()/2-((StringWidth("Options")*FontWidth())/2) And MouseX()<GraphicsWidth()/2+((StringWidth("Options")*FontWidth())/2) And MouseY()>(GraphicsHeight()/2)+FontHeight()*1 And MouseY()<((GraphicsHeight()/2)+FontHeight()*1)+StringHeight("Options") Then
options()
EndIf
EndIf

If KeyHit(1) Then 
writesettings()
savelog()
End
EndIf
Flip

Wend

End Function


;-------------------------- ONE PLAYER SELECT FUNCTION IS HERE --------------------------


Function oneplayerselect(netstatus$)

Font=LoadFont("Microsoft Sans Serif",28,0,0,0)
SetFont Font

Local botnumber#=0

StopChannel(bgm)
	
	chardisplay#=gametype#
	
	MoveMouse 0,0
	
	Font=LoadFont("Microsoft Sans Serif",25,1,0,0)
	SetFont Font
	Color 255,0,0
	
	FlushMouse()
	FlushKeys()

	chardisplay#=0

	Repeat
		

If KeyHit(65) Then
	scrshot()
EndIf

	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("BGM/Interim Nation - Future Experience.mp3")
	EndIf
	
		Cls
		
		If tankToneA#>255 Then tankToneA#=255
		If tankToneA#<0 Then tankToneA#=0
		
		If tankToneB#>255 Then tankToneB#=255
		If tankToneB#<0 Then tankToneB#=0
		
		If tankToneC#>255 Then tankToneC#=255
		If tankToneC#<0 Then tankToneC#=0
		
		DrawImage gfxbackground,0,0
		DrawImage gfxcharback,0,0
		
		
		;Text 700,0,"X: "+MouseX()
		;Text 700,20,"Y: "+MouseY()
		Text 0,0,"Time: "+CurrentTime$()

		Text 60,60+FontHeight()*(-1+MouseZ()),"--Select Your Tank--"
		Text 60,60+FontHeight()*(0+MouseZ()),"Warrior"
		Text 60,60+FontHeight()*(1+MouseZ()),"British-Tadpole"
		Text 60,60+FontHeight()*(5+MouseZ()),"[Net Game: "+netstatus$+"]"
		Text 60,60+FontHeight()*(6+MouseZ()),"[Split Screen: "+Str(Int(botnumber))+" ]"
		Text 60,60+FontHeight()*(7+MouseZ()),"[Control 1: "+controls$+"]"
		If botnumber=1 Then Text 60,60+FontHeight()*(8+MouseZ()),"[Control 2: "+controls2$+"]"
		;Text 60,60+FontHeight()*(9+MouseZ()),"[Bullets Bouncy: "+Bounce+"]"
		If botnumber=1 Then Text 60,60+FontHeight()*(10+MouseZ()),"[Long Shot: "+BulLenght+"]"
		Text 60,60+FontHeight()*13,"----------------------------  [OK]"
		
		If control#=1 Then controls$="Keyboard"
		If control#=2 Then controls$="Mouse"
		If control#=3 Then controls$="Joy"
		If control#=4 Then controls$="Joy 2"
		If control2#=1 Then controls2$="Keyboard"
		If control2#=2 Then controls2$="Mouse"
		If control2#=3 Then controls2$="Joy"
		If control2#=4 Then controls2$="Joy 2"
		
		If MouseHit(1) Then
			If MouseX()>60+(StringWidth("----------------------------") And MouseX()<40+(StringWidth("----------------------------  [OK]"))*FontWidth()) And MouseY()>60+FontHeight()*13 And MouseY()<60+FontHeight()*14 And chardisplay#>0 Then

				If chardisplay#=5 Then
					maingame(X#,YE#,PIT#,YAW#,ROLL#,HEIGH#,netstatus$,copnumber#,control#,gametype#)	
				Else
	
					gametype#=chardisplay#
					maingame(0,0,0,0,0,1,netstatus$,botnumber#,control#,gametype#)
				EndIf
			EndIf
			If MouseX()>60 And MouseX()<(StringWidth("Warrior")*FontWidth()) And MouseY()>60+FontHeight()*(0+MouseZ()) And MouseY()<60+FontHeight()*(1+MouseZ()) Then
				chardisplay#=1
				
				tankToneA#=255
				tankToneB#=255
				tankToneC#=255
				
				ClearWorld()
				tankname$="warrior"
				tank=LoadMesh("Mesh/warrior.3ds")			
				ScaleMesh tank,1,1,-1
				FlipMesh tank
				FitMesh tank,-1.5,-1,-3,3,2,6
				PositionEntity tank,0,0,0
				EntityShininess tank,1
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,tank
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
			
			EndIf
			If MouseX()>60 And MouseX()<(60+StringWidth("[Split Screen: "+Str(Int(botnumber))+" ]")*FontWidth()) And MouseY()>60+FontHeight()*(6+MouseZ()) And MouseY()<60+FontHeight()*(7+MouseZ()) Then
				If netstatus$="Off" Then
				If botnumber#=1 Then
					botnumber#=0
				Else
					botnumber#=1
				EndIf
				EndIf
			EndIf
			If MouseX()>60 And MouseX()<(60+StringWidth("[Control 1: "+controls$+"]")*FontWidth()) And MouseY()>60+FontHeight()*(7+MouseZ()) And MouseY()<60+FontHeight()*(8+MouseZ()) Then
				control#=control#+1
				If control#>4 Then
				control#=1
				EndIf
			EndIf
			If botnumber=1 And MouseX()>60 And MouseX()<(60+StringWidth("[Control 2: "+controls2$+"]")*FontWidth()) And MouseY()>60+FontHeight()*(8+MouseZ()) And MouseY()<60+FontHeight()*(9+MouseZ()) Then
				control2#=control2#+1
				If control2#>4 Then
				control2#=1
				EndIf
			EndIf
			;If MouseX()>60 And MouseX()<(60+StringWidth("[Bullets Bouncy: "+Bounce+"]")*FontWidth()) And MouseY()>60+FontHeight()*(9+MouseZ()) And MouseY()<60+FontHeight()*(10+MouseZ()) Then
			;	If bounce=True Then
			;		bounce=False
			;	Else
			;		bounce=True
			;	EndIf
			;EndIf
			If botnumber=1 And MouseX()>60 And MouseX()<(60+StringWidth("[Long Shot: "+BulLenght+"]")*FontWidth()) And MouseY()>60+FontHeight()*(10+MouseZ()) And MouseY()<60+FontHeight()*(11+MouseZ()) Then
				If BulLenght=True Then
					BulLenght=False
				Else
					BulLenght=True
				EndIf
			EndIf
			If MouseX()>60 And MouseX()<(StringWidth("British-Tadpole")*FontWidth()) And MouseY()>60+FontHeight()*(1+MouseZ()) And MouseY()<60+FontHeight()*(2+MouseZ()) Then
				chardisplay#=2
				
				tankToneA#=255
				tankToneB#=255
				tankToneC#=255
				
				ClearWorld()
				tankname$="btadpole"
				tank=LoadMesh("Mesh/btadpole.3ds")			
				ScaleMesh tank,1,1,-1
				FlipMesh tank
				FitMesh tank,-1.5,-1,-3,3,2,6
				PositionEntity tank,0,0,0
				EntityShininess tank,1
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,tank
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
				
			EndIf
		EndIf
		
		If chardisplay=1 Then
			Text 420,40,"Warrior"

			TurnEntity tank,0,3,0
			EntityColor tank,tankToneA#,tankToneB#,tankToneC#
			UpdateWorld()
			RenderWorld()
			
		EndIf
		
		If chardisplay=2 Then 
			Text 420,40,"British-Tadpole"

			TurnEntity tank,0,3,0
			EntityColor tank,tankToneA#,tankToneB#,tankToneC#
			UpdateWorld()
			RenderWorld()

		EndIf

		If chardisplay=5 Then
			Text 400,50,"Game Type: "+gametype
			Text 400,70,"XLoc: "+X#
			Text 400,90,"YLoc: "+YE#
			Text 400,110,"Pitch: "+PIT#
			Text 400,130,"Yaw: "+YAW#
			Text 400,150,"Roll: "+ROLL#
		

			TurnEntity tank,0,3,0
			EntityColor tank,tankToneA#,tankToneB#,tankToneC#
			UpdateWorld()
			RenderWorld()

		EndIf
		
			If MouseHit(2) Then
				If MouseX()>60 And MouseX()<(60+StringWidth("[Enimies: "+Str(Int(botnumber))+" ]")*FontWidth()) And MouseY()>60+FontHeight()*(6+MouseZ()) And MouseY()<60+FontHeight()*(7+MouseZ()) Then
					botnumber#=botnumber#-1
				EndIf
			EndIf

		DrawImage gfxpoint,MouseX(),MouseY(),pointrot#
		pointrot#=pointrot#+1
		If pointrot#=4 Then pointrot#=0
		Flip
		
		If KeyHit(1) Then
			Cls
			Flip
			mainmenu()
		EndIf
		
	Forever
	
End Function


;-------------------------- OPTIONS MENU FUNCTION IS HERE --------------------------

Function options()
Font=LoadFont("Microsoft Sans Serif",20,1,0,0)
SetFont Font
Color 255,0,0

FlushMouse()
FlushKeys()

StopChannel(bgm)

Repeat
Cls
Color 255,0,0

If KeyHit(65) Then
	scrshot()
EndIf


	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("BGM/Interim Nation - In the beginning.mp3")
	EndIf
	
DrawImage gfxbackground,0,0
DrawImage gfxoptionsback,0,0

;Text 0,0,"X: "+MouseX()
;Text 0,10,"Y: "+MouseY()

Text 67,82,"Your Name: "+name$
Text 67,112,"Screen Mode: "+GraphicsWidth()+"x"+GraphicsHeight()
Text 67,142,"Your gamename: "+filename$
Text 67,172,"Credits"

If MouseHit(1) Then

If MouseX()>67 And MouseX()<305 And MouseY()>172 And MouseY()<192 Then
StopChannel(BGM)
credits()
EndIf

If MouseX()>67 And MouseX()<305 And MouseY()>82 And MouseY()<92 Then
name$=""

Cls
DrawImage gfxbackground,0,0
DrawImage gfxoptionsback,0,0
If KeyHit(65) Then
	scrshot()
EndIf
Flip

While name$=""

Locate 67,82
FlushKeys()
FlushMouse()
name$=Input("Your Name: ")
Wend

EndIf

If MouseX()>67 And MouseX()<305 And MouseY()>142 And MouseY()<152 Then
filename$=""

Cls
DrawImage gfxbackground,0,0
DrawImage gfxoptionsback,0,0
Flip

While filename$=""

Locate 67,142
FlushKeys()
FlushMouse()
filename$=Input("Your gamename: ")
Wend

EndIf
EndIf

If KeyHit(1) Then
mainmenu()
EndIf

DrawImage gfxpoint,MouseX(),MouseY(),pointrot#
pointrot#=pointrot#+1
If pointrot#=4 Then pointrot#=0

If KeyHit(1) Then mainmenu()
Flip
Forever

End Function

Function credits()
FlushKeys()
FlushMouse()

Font=LoadFont("Microsoft Sans Serif",28,0,0,0)
SetFont Font

Local A=(FontHeight()*0)+GraphicsHeight()
Local B=(FontHeight()*2)+GraphicsHeight()
Local BB=(FontHeight()*3)+GraphicsHeight()
Local C=(FontHeight()*5)+GraphicsHeight()
Local D=(FontHeight()*7)+GraphicsHeight()
Local E=(FontHeight()*9)+GraphicsHeight()
Local F=(FontHeight()*11)+GraphicsHeight()
Local G=(FontHeight()*13)+GraphicsHeight()
Local H=(FontHeight()*15)+GraphicsHeight()
Local I=(FontHeight()*17)+GraphicsHeight()
Local J=(FontHeight()*20)+GraphicsHeight()

Repeat

Cls


	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("BGM/Interim Nation - Sunset at Tioman Island, Part II.mp3")
	EndIf
	

DrawImage gfxbackground,0,0

Text GraphicsWidth()/2,A,"--Tank Battle--",1
Text GraphicsWidth()/2,B,"Created by Vanessa Games",1
Text GraphicsWidth()/2,BB,"www.VanessaGames.com",1
Text GraphicsWidth()/2,C,"Programming by Vanessa Kindell",1
Text GraphicsWidth()/2,D,"Tank meshes by Geometrics.",1
Text GraphicsWidth()/2,E,"Map Mesh By Rex Kindell",1
Text GraphicsWidth()/2,F,"Concept based on Tank Battle by Mattel Electronics",1
Text GraphicsWidth()/2,G,"Online multiplayer provided by GNet, hosted at www.BlitzBasic.com.",1
Text GraphicsWidth()/2,H,"In game music by Thomas Stenbäck of Interim Nation.",1
Text GraphicsWidth()/2,I,"Special thanks to all who contributed!",1
Text GraphicsWidth()/2,J,"Press ESC to continue!",1

A=A-1
B=B-1
BB=BB-1
C=C-1
D=D-1
E=E-1
F=F-1
G=G-1
H=H-1
I=I-1
J=J-1

If A=<-10 Then A=610
If B=<-10 Then B=610
If BB=<-10 Then BB=610
If C=<-10 Then C=610
If D=<-10 Then D=610
If E=<-10 Then E=610
If F=<-10 Then F=610
If G=<-10 Then G=610
If H=<-10 Then H=610
If I=<-10 Then I=610
If J=<-10 Then J=610

If KeyHit(1) Then
mainmenu()
EndIf
Flip

Forever

End Function

Function maingame(X#,y#,PIT#,YAW#,ROLL#,HEIGH#,netstatus$,botnumber#,control#,gametype#)

hits#=0
track=0

info("Welcome To Tank Battle")

StopChannel(bgm)

If netstatus$="On" Then
	GNET_ListServers("BorkedTankBattlev"+Int(Version#))
	gamestarted=False
	While Not gamestarted
		Cls
		Color 255,255,255
		DrawImage gfxbackground,0,0
		
		
		If KeyHit(1) Then
			mainMenu()
		EndIf		
		Text 0,FontHeight()*5,"Servers:"
		Text 0,GraphicsHeight()-FontHeight(),"Press F5 to refresh, space to host"
		If KeyHit(63) Then GNET_ListServers("BorkedTankBattlev"+Int(Version#))
		serverCount = 0
		MenuCount = (MouseY()/FontHeight())-6-MouseZ()
		For gns.GNET_Server = Each GNET_Server
			Select serverCount
				Case menuCount
					Color 255,255,0
					If serverCount>-MouseZ()-1
						Text 0,FontHeight()*serverCount+FontHeight()*MouseZ()+FontHeight()*6, gns\server$
					EndIf
					serverPointer = Handle(gns)
				Default
					Color 255,255,255
					If serverCount>-MouseZ()-1
						Text 0,FontHeight()*serverCount+FontHeight()*MouseZ()+FontHeight()*6, gns\server$
					EndIf			
			End Select
			serverCount = serverCount + 1
		Next
		If MouseHit(1) Then
			If MenuCount < serverCount Then
				gns.GNET_Server = Object.GNET_Server(serverPointer)
				Select BP_JoinSession (tankname$+"="+name$,Rand(3000,4000),gns\ip$,2222)
					Case BP_NOREPLY
						RuntimeError "No reply in specified timeout period."
					Case BP_IAMBANNED
						RuntimeError "You have been banned from joining this game."
					Case BP_GAMEISFULL
						RuntimeError "The game is full.:
					Case BP_PORTNOTAVAILABLE
						RuntimeError "Port: " + My_Port + " was Not available."
					Case BP_USERABORT
						RuntimeError "Connection attempt aborted!"
					Default:
						playerID = BP_My_ID
						gamestarted=True
						gamehosted=1
				End Select
			EndIf
		EndIf
		If KeyHit(57) Then
			GNET_Removeserver("BorkedTankBattlev"+Int(Version#)) ;Make sure you're not already on the list.
			FlushKeys
			Color(255,0,0)
			If BP_HostSession (Name$,2,1,2222,10)
				
				If GNET_AddServer("BorkedTankBattlev"+Int(Version#), Name$+"'s server")
					playerID = BP_MY_ID
					gamestarted=True
					gamehosted=2
					serverRefreshTimer# = MilliSecs()
				Else
					info("Failed to Add Server, ignoring for now...")
				EndIf
					
			Else
				
				RuntimeError("Failed to Create Net Game!")	
					
			EndIf
		EndIf
		
		DrawImage gfxpoint,MouseX(),MouseY(),pointrot#
		pointrot#=pointrot#+1
		If pointrot#=4 Then pointrot#=0
		Flip
	Wend
;	gamehosted=Host (filename$)
;	If gamehosted<2 Then mainmenu()
;	playerID=CreateNetPlayer(tankname$+"="+name$)
;	If playerID=0 Then RuntimeError("No player created")
EndIf

;If netstatus$="On" Then
;	BP_UDPMessage (0,6,mycdkey$)
;EndIf

player2ID=0

FlushMouse()
FlushKeys()
ClearWorld()

Local chat$

Collisions BODY,SCENE,2,2
Collisions WHEEL,SCENE,2,2
Collisions TYPE_BULLET,POLICE,2,3
Collisions TYPE_BULLET,SCENE,2,3
Collisions TYPE_BULLET,NET,2,3
Collisions TYPE_BULLET,TYPE_BOT,2,3
Collisions NET_BULLET,BODY,2,3
Collisions NET_BULLET,SCENE,2,3
Collisions CAMBOX,TYPE_GRASS,2,2
Collisions NET,BODY,2,2
Collisions BODY,NET,2,2
Collisions BODY,POLICE,2,3
Collisions POLICE,SCENE,2,2
Collisions POLICE,BODY,2,2
Collisions POLICE,NET,2,2
Collisions POLICE,POLICE,2,2
Collisions POLICE,TYPE_BOT,2,2
Collisions TYPE_BOT,SCENE,2,2
Collisions TYPE_BOT,BODY,2,2
Collisions TYPE_BOT,NET,2,2
Collisions TYPE_BOT,POLICE,2,2
Collisions TYPE_BOT,TYPE_BOT,2,2
Collisions GHOST,TYPE_GRASS,2,2

map=LoadMesh("Mesh/map.3ds")
ScaleEntity map,.5,.5,.5
EntityType map,SCENE

showwheel=False

laser=True

If gametype=1 Then
tank=LoadMesh( "Mesh/warrior.3ds" )
Maxspeed#=2.0
Accel#=0.008
TurnFactor#=2
EndIf

If gametype=2 Then
tank=LoadMesh( "Mesh/btadpole.3ds" )
Maxspeed#=1.8
Accel#=0.006
TurnFactor#=3	
EndIf

If gametype>9 Then
tank=LoadMesh(tankload$)
Maxspeed#=1.7
Accel#=0.005
TurnFactor#=3
EndIf

EntityColor tank,tankToneA#,tankToneB#,tankToneC#
ScaleMesh tank,1,1,-1
FlipMesh tank
FitMesh tank,-3,-1,-3,6,2,6,True
EntityShininess tank,1
EntityBox tank,-3,-1,-3,6,2,6

If gamehosted=2 Then
PositionEntity tank,-50,1,40
RotateEntity tank,0,-90,0
tanktoneA#=255
tanktoneB#=0
tanktoneC#=0
Else
PositionEntity tank,50,1,-40
RotateEntity tank,0,90,0
tanktoneA#=0
tanktoneB#=0
tanktoneC#=255
EndIf

EntityColor tank,tankToneA#,tankToneB#,tankToneC#

EntityType tank,BODY

grassb=CreatePlane()
EntityType grassb,Type_GRASS
EntityAlpha grassb,0

camera=CreateCamera(tank)
EntityType camera,CAMBOX
PositionEntity camera,0,1.5,0

ear=CreateListener(Camera,.01,1,.1)

;make it bright (add light)
light=CreateLight()
TurnEntity light,45,45,0

worldtime#=Left$(CurrentTime$(),2)
If worldtime#>12 Then worldtime#=worldtime#-24

If botnumber#=1 Then
	createchaser()
	CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()/2
Else If gamehosted=0 Then
	createbot()
EndIf

mapcam=CreateCamera()
PositionEntity mapcam,0,90,0
RotateEntity mapcam,90,0,0
CameraClsMode mapcam,0,1

If botnumber#=1 Then
	CameraViewport mapcam,(GraphicsWidth()-(GraphicsWidth()/4)),(GraphicsHeight()/2-(GraphicsHeight()/4))+(GraphicsHeight()/8),GraphicsWidth()/4,GraphicsHeight()/4
Else
	CameraViewport mapcam,(GraphicsWidth()-(GraphicsWidth()/4)),(GraphicsHeight()-(GraphicsHeight()/4)),GraphicsWidth()/4,GraphicsHeight()/4
EndIf

If gamehosted=0 Then
	respawndelay()
EndIf

Local MovePlatformDir#=1
Local Platformtime#=0

;start main loop -------------------------------------------

While Not KeyHit(1)

If gamehosted=2 Then
cntplrs#=0
	For p.player=Each player
		cntplrs#=cntplrs#+1
	Next
If cntplrs#>1 Then
	p.player=Last player
	BP_UDPMessage (0,8, "You were kicked")
EndIf
EndIf

If KeyHit(65) Then
	scrshot()
EndIf

	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("BGM/First_Impressions_Last_clip.mp3")
	EndIf

If track>maxtrax# Then
	track=0
EndIf
	

If hits#=>1 Then
CHNSFX=PlaySound(TDSFXAHHHH)
If netstatus$="On" Or netstatus$="Net" Then
	BP_UDPMessage (0,5,"I got shot!")
EndIf
Cls
Text GraphicsWidth()/2,GraphicsHeight()/2,"Your tank was destroyed!",1,1
Flip
Delay 1000
FlushKeys()
info("Player tank destroyed!")
EntityType tank,0
If botnumber#=1 Then
EntityType tank2,0
PositionEntity tank2,-50,1,40
RotateEntity tank2,0,-90,0
EntityType tank2,POLICE
EndIf
If gamehosted=2 Then
PositionEntity tank,-50,1,40
RotateEntity tank,0,-90,0
showhitsB#=showhitsB#+1
Else
PositionEntity tank,50,1,-40
RotateEntity tank,0,90,0
showhits#=showhits#+1
EndIf
EntityType tank,BODY
hits#=0
RespawnDelay()
EndIf

If hitsB#=>1 Then
CHNSFX=PlaySound(TDSFXAHHHH)

Cls
Text GraphicsWidth()/2,GraphicsHeight()/2,"Red Tank Destroyed!",1,1
Flip
Delay 1000
FlushKeys()
info("Red tank destroyed!")

EntityType tank2,0
PositionEntity TANK2,-50,1,40
RotateEntity tank2,0,-90,0
EntityType tank2,POLICE
showhitsB#=showhitsB#+1
EntityType tank,0
PositionEntity tank,50,1,-40
RotateEntity tank,0,-90,0
EntityType tank,BODY
hitsB#=0
RespawnDelay()
EndIf


	If control#=1 Then keyboardcontrol(tank,netstatus$,PlayerID,1)
	If control#=2 Then mousecontrol(tank,netstatus$,PlayerID,1)
	If control#=3 Then joycontrol(tank,netstatus$,PlayerID,1)
	If control#=4 Then joy2control(tank,netstatus$,PlayerID,1)
	
If botnumber#=1 Then
	If control2#=1 Then keyboardcontrol(tank2,netstatus$,PlayerID,0)
	If control2#=2 Then mousecontrol(tank2,netstatus$,PlayerID,0)
	If control2#=3 Then joycontrol(tank2,netstatus$,PlayerID,0)
	If control2#=4 Then joy2control(tank2,netstatus$,PlayerID,0)
EndIf
	
	For bul.Bullet=Each Bullet
		UpdateBullet( bul )
	Next
	
	For nbul.NetBullet=Each NetBullet
		UpdateNetBullet( nbul )
	Next
	
	
	For s.Spark=Each Spark
		UpdateSpark( s )
	Next

	For co.bot=Each bot
		Updatebot( co )
	Next

	;-------------- hangtime and sound stuff --------------
	LastTouch=Touch
	If EntityCollided(tank,TYPE_map) Or EntityCollided(tank,Scene) Then
	
	If LastHang<HangTime Then LastHang=HangTime
	HangTime=0
	Touch=1
	EndIf

	If Not EntityCollided(tank,TYPE_map) Or EntityCollided(tank,Scene) Then
	Touch=2
	HangTime=HangTime+1
	EndIf
	
	;control stuff-------------------------------

		If KeyHit(68) Then
		Cls
		info("Game was paused.")
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,2,"I Paused!")
			EndIf
		RenderWorld()
		Text GraphicsWidth()/2,GraphicsHeight()/2,"Game Paused",1,1
		Text GraphicsWidth()/2,(GraphicsHeight()/2)+FontHeight(),"Press any key to continue!",1,1
		Flip
		WaitKey()
		EndIf
	
	If netstatus$="On" Or netstatus$="Net" Then
		UpdateNetwork()
		BP_UDPMessage (0,1,PackPlayerMsg$())
	EndIf
	

	worldtime#=Left$(CurrentTime$(),2)
	If worldtime#>12 Then worldtime#=worldtime#-24
	
	BP_UpdateNetwork()
	UpdateWorld
	RenderWorld

If consoleout=False Then
	Color 0,0,0 ;text shadow
	Text 2,FontHeight()*0,"Name: "+name$
	Text 2,FontHeight()*1,"Blue Tanks: "+Int(3-LRounds#)
	Text 2,FontHeight()*2,"Red Tanks: "+Int(3-Rounds#)
	Text 2,FontHeight()*3,"Time: "+CurrentTime$()
	y=FontHeight()*4
	r=255
	inf.Info=First info
		If r>0
			Text 2,y,"Info: "+inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf
	Text 2,FontHeight()*5,">"+chat$

If wire=True Then
	Color 0,255,0
Else
	Color 255,255,255
EndIf
	Text 0,FontHeight()*0,"Name: "+name$
	Text 0,FontHeight()*1,"Blue Tanks: "+Int(3-LRounds#)
	Text 0,FontHeight()*2,"Red Tanks: "+Int(3-Rounds#)
	Text 0,FontHeight()*3,"Time: "+CurrentTime$()
	y=FontHeight()*4
	r=255
	inf.Info=First info
		If r>0
			Text 0,y,"Info: "+inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf
	Text 0,FontHeight()*5,">"+chat$
	
Else
	Color 0,0,0 ;console shadow
	Text 2,FontHeight()*8,">"+chat$
	y=FontHeight()*7
	r=255
	For inf.Info=Each Info
		If r>0
			Text 2,y,inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf
	Next
If wire=True Then
	Color 0,255,0
Else
	Color 255,255,255
EndIf
	Text 0,FontHeight()*8,">"+chat$
	y=FontHeight()*7
	r=255
	For inf.Info=Each Info
		If r>0
			Text 0,y,inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf
	Next
EndIf

If KeyHit(15) Then ;key86?
If consoleout=True Then
consoleout=False
Else
consoleout=True
EndIf
EndIf

			;Chat stuff
			key=GetKey()
			If key
				If key=13
					If chat$<>"" Then
					If Instr(chat$,"=")>0 Then
						command(Left(chat$,Instr(chat$,"=")-1),Mid(chat$,Instr(chat$,"=")+1))
						chat$=""
					Else	
						If netstatus$="On" Or netstatus$="Net" Then
						BP_UDPMessage (0,2,chat$)
						EndIf
						info("You Said: "+Chat$)
						chat$=""
						EndIf
					EndIf
				Else If key=8
					If Len(chat$)>0 Then chat$=Left$(chat$,Len(chat$)-1)
				Else If key>=32 And key<127
					chat$=chat$+Chr$(key)
				EndIf
				
			EndIf

If botnumber#=0 Then
DrawImage Damage,0,GraphicsHeight()-ImageHeight(Damage),Int(10-showhitsB#)
DrawImage DamageB,0,GraphicsHeight()-(ImageHeight(DamageB)*2),Int(10-showhits#)
Else
DrawImage Damage,0,GraphicsHeight()-ImageHeight(Damage),Int(10-showhitsB#)
DrawImage DamageB,0,(GraphicsHeight()/2)-ImageHeight(DamageB),Int(10-showhits#)
EndIf

WaitTimer(timer)
Flip
Wend
GNET_Removeserver("BorkedTankBattlev"+Int(Version#))
BP_EndSession ()
clearallworld()
mainmenu()
End Function

;end of main game function

Function PackPlayerMsg$()
	Return Left(LSet$(Str$(EntityX(tank)),8),8) + Left(LSet$(Str$(EntityZ(tank)),8),8) + Left(LSet$(Str$(EntityYaw(tank)),8),8) + Left(LSet$(Str$(EntityY(tank)),8),8) + Left(LSet$(Str$(Int(EntityPitch(tank))),8),8) + Left(LSet$(Str$(EntityRoll(tank)),8),8) + LSet$(gametype#,8) + LSet$( Int(tanktoneA#),8) + LSet$( Int(tanktoneB#),8) + LSet$( Int(tanktoneC#),8)
End Function

Function UpdateNetwork.player()
	For msg.MsgInfo = Each MsgInfo
		Select msg\msgType
		Case 1:
			getname$=Mid(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")+1)
			tanktype$=Left(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")-1)
			p.player=FindPlayer( msg\msgFrom )
			If p<>Null Then UnpackPlayerMsg( msg\msgData,p,BP_GetPlayerName$(msg\msgFrom))
		Case 2:
			getname$=Mid(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")+1)
			tanktype$=Left(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")-1)
			p.player=FindPlayer( msg\msgFrom )
			EmitSound(sfxbeep,p\tankO)
			Info(getname$+" Said:"+msg\msgData)
		Case 3:
			getname$=Mid(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")+1)
			tanktype$=Left(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")-1)
			p.player=FindPlayer( msg\msgFrom )
;			If p<>Null Then CreateBullet(p\tankO,2)
		Case 4:
			p.player=FindPlayer( msg\msgFrom )
			createNetBullet(p\tankO)

		Case 5:
			p.player=FindPlayer( msg\msgFrom )
			EmitSound(SFXAHHHH,p\tankO)
			createspark(p\tankO)
			EntityType tank,0
			If gamehosted=2 Then
				PositionEntity tank,-50,1,40
				RotateEntity tank,0,-90,0
				showhits#=showhits#+1
			Else
				PositionEntity tank,50,1,-40
				RotateEntity tank,0,90,0
				showhitsB#=showhitsB#+1
			EndIf
			Cls
			Text GraphicsWidth()/2,GraphicsHeight()/2,"Enemy Tank Destroyed!",1,1
			Flip
			Delay 1000
			FlushKeys()
			info("Other player was destroyed!")
			RespawnDelay()
			EntityType tank,BODY
		
		Case 6:
			If msg\msgData=mycdkey$ Then
				DeleteFile "key.inf"
				RuntimeError "Someone in this game is using your CD key."
			EndIf
										
		Case 8:
			getname$=Mid(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")+1)
			tanktype$=Left(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")-1)
			info(msg\msgData + " by " + getname$)
			savelog()
			RuntimeError msg\msgData + " by " + getname$
		
		Case 9:
			getname$=Mid(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")+1)
			tanktype$=Left(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")-1)
			Cheat$=msg\msgData

			info(getname$+msg\msgFrom+" Used The Cheat Code: "+cheat$)
			
		Case 255:
			getname$=Mid(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")+1)
			tanktype$=Left(BP_GetPlayerName$(msg\msgFrom),Instr(BP_GetPlayerName$(msg\msgFrom),"=")-1)

			p.Player=New player
			p\net_id=msg\msgFrom
			p\tank$=tanktype$			
			info(getname$+" has joined the game. ")			
			showhits#=0
			showhitsB#=0
			Rounds#=0
			EntityType tank,0
			If gamehosted=2 Then
				PositionEntity tank,-50,1,40
				RotateEntity tank,0,-90,0
			Else
				PositionEntity tank,50,1,-40
				RotateEntity tank,0,90,0
			EndIf
			RespawnDelay()
			EntityType tank,BODY
			

				If p\tank$="warrior" Then
					p\tankO=LoadMesh("Mesh/warrior.3ds")			
					ScaleMesh p\tankO,1,1,-1
					FlipMesh p\tankO
					FitMesh p\tankO,-1.5,-1,-3,3,2,6
					EntityShininess p\tankO,1
					PositionEntity p\tankO,0,100,0
					EntityType p\tankO,NET
				Else If p\tank$="btadpole" Then
					p\tankO=LoadMesh("Mesh/btadpole.3ds")			
					ScaleMesh p\tankO,1,1,-1
					FlipMesh p\tankO
					FitMesh p\tankO,-1.5,-1,-3,3,2,6
					EntityShininess p\tankO,1
					PositionEntity p\tankO,0,100,0
					EntityType p\tankO,NET
				Else
					If looo$<>"" Then
						p\tankO=LoadMesh(looo$)
						ScaleMesh p\tankO,1,1,-1
						FlipMesh p\tankO
						FitMesh p\tankO,-1.5,-1,-3,3,2,6
						EntityShininess p\tankO,1
						PositionEntity p\tankO,0,100,0
						EntityType p\tankO,NET
					Else
						p\tankO=LoadMesh("Mesh/warrior.3ds")			
						ScaleMesh p\tankO,1,1,-1
						FlipMesh p\tankO
						FitMesh p\tankO,-1.5,-1,-3,3,2,6
						EntityShininess p\tankO,1
						PositionEntity p\tankO,0,100,0
						EntityType p\tankO,NET
					EndIf
				EndIf
				EmitSound(sfxSpawn,p\tankO)
			
		Case 254:
			p.Player=FindPlayer( msg\msgFrom )
			HideEntity p\tankO
			If p<>Null
				info("A player has left the game. ")
				Delete p
			EndIf
			
		Case 253:
		
			Cls
			Text GraphicsWidth()/2,GraphicsHeight()/2,"The host left the game.",1,1
			Flip
			Delay 2000
			BP_EndSession ()
			clearallworld()
			mainmenu()
		
		Case 252:
		
		
			EndGraphics
			Print "The session has been lost!"
			WaitKey
			End
		
		
		End Select
	Delete msg
	Next
End Function

;find player with player id
Function FindPlayer.Player( id )
	For p.Player=Each Player
		If p\net_id=id Then Return p
	Next
End Function

;unpack player details from a string
Function UnpackPlayerMsg.player(msg$,p.player,who$)
	X#=Mid$( msg$,1,8 )
	Z#=Mid$( msg$,9,8 )
	Yaw#=Mid$( msg$,17,6 )
	Y#=Mid$( msg$,25,6 )
	Pitch#=Mid$( msg$,33,6 )
	Roll#=Mid$( msg$,41,6 )

	tankTp#=Mid$( msg$,49,2 )
	tanktToneA#=Mid$( msg$,57,6 )
	tanktToneB#=Mid$( msg$,65,6 )
	tanktToneC#=Mid$( msg$,73,6 ) 

	EntityColor p\tankO,tanktToneA#,tanktToneB#,tanktToneC#
	PositionEntity p\tankO,X,Y,Z
	RotateEntity p\tankO,Pitch,Yaw,Roll
	CameraProject(camera,EntityX(p\tankO),EntityY(p\tankO),EntityZ(p\tankO))
	Text ProjectedX#(),ProjectedY#(),who$

End Function

Function Createbot.bot()
	co.bot=New bot
		co\botBOdy=LoadMesh("Mesh/warrior.3ds")			
		ScaleMesh co\botbody,1,1,-1
		FlipMesh co\botbody
		FitMesh co\botbody,-3,-1,-3,6,2,6,True
		EntityShininess co\botbody,1
		EntityBox co\botbody,-3,-1,-3,6,2,6
		PositionEntity co\botbody,-50,1,40
		
		If wire=True
			EntityColor co\botbody,0,255,0
		Else
			EntityColor co\botbody,255,0,0
		EndIf
		
		TurnEntity co\botbody,0,-90,0
		EntityType co\botbody,TYPE_BOT
		co\cam=CreateCamera(co\botbody)
		MoveEntity co\cam,0,1,-4
		CameraProjMode co\cam,0
		;CameraViewport co\cam,0,GraphicsHeight()/2,GraphicsWidth(),GraphicsHeight()/2
	Return co
End Function

Function Createchaser()
		TANK2=LoadMesh("Mesh/warrior.3ds")			
		ScaleMesh TANK2,1,1,-1
		FlipMesh TANK2
		FitMesh TANK2,-3,-1,-3,6,2,6,True
		EntityShininess TANK2,1
		EntityBox TANK2,-3,-1,-3,6,2,6
		PositionEntity TANK2,-50,1,40
		RotateEntity TANK2,0,-90,0
		EntityColor TANK2,255,0,0
		EntityType TANK2,POLICE
		camera2=CreateCamera(TANK2)
		MoveEntity camera2,0,1.5,0
		CameraViewport camera2,0,GraphicsHeight()/2,GraphicsWidth(),GraphicsHeight()/2
End Function

;update bot stuff
Function Updatebot.bot( co.bot )
	If EntityCollided(co\botbody,SCENE) Then
		MoveEntity co\botbody,0,0,.5
		If EntityInView(tank,co\cam) Then	
			TurnEntity co\botbody,0,DeltaYaw#(co\botbody,tank),0
		Else
			TurnEntity co\botbody,0,Rand(-3.3),0
		EndIf
	Else
		TranslateEntity co\botbody,0,gravity*10,0
	EndIf

co\delaytime=co\delaytime#+1
If EntityDistance(co\botbody,tank)<25 And EntityInView(tank,co\cam) And Fired=False And co\Delaytime>15 Then
	Fired=True
	createNetBullet(co\botbody)
	co\delaytime=0
EndIf

If EntityCollided(co\botbody,type_bullet) Then
EmitSound(SFXAHHHH,co\botbody)
Hidebot(co)
	EntityType tank,0
	If gamehosted=2 Then
		PositionEntity tank,-50,1,40
		RotateEntity tank,0,-90,0
		showhits#=showhits#+1
	Else
		PositionEntity tank,50,1,-40
		RotateEntity tank,0,90,0
		showhitsB#=showhitsB#+1
	EndIf
Cls
Text GraphicsWidth()/2,GraphicsHeight()/2,"Enemy Tank Destroyed!",1,1
Flip
Delay 1000
FlushKeys()
info("Bot destroyed!")
	RespawnDelay()
	EntityType tank,BODY
showhitsB#=showhitsB#+1
EndIf
End Function

;update bot stuff
Function Hidebot.bot(co.bot)
		createbot()
		HideEntity co\botbody
		Delete co
End Function

Function reportbot()
For co.bot=Each bot
	info("bot at ("+EntityX(co\botbody)+","+EntityY(co\botbody)+","+EntityZ(co\botbody)+")")
Next
End Function

;make bullet
Function CreateBullet.Bullet(entity)
	bul.Bullet=New Bullet

	If BulLenght = 1 Then
		bul\time_out=150
	Else
		bul\time_out=300
	EndIf
	
	bul\sprite=LoadMesh("Mesh/lazer.x")
	PositionEntity bul\sprite,EntityX(entity),EntityY(entity),EntityZ(entity)
	RotateEntity bul\sprite,EntityPitch(entity),EntityYaw(entity),EntityRoll(entity)
	TranslateEntity bul\sprite,0,1,0

	If wire=True Then
		EntityColor bul\sprite,0,255,0
	Else
		EntityColor bul\sprite,255,0,0
	EndIf

	ScaleEntity bul\sprite,8,2,8
	EntityRadius bul\sprite,.5
	EntityType bul\sprite,TYPE_BULLET
	EntityAlpha bul\sprite,1
	EmitSound (sfxshoot,entity)
	Return bul
End Function

;make bullet
Function CreateNetBullet(entity)
	nbul.NetBullet=New NetBullet
	If BulLenght = 1 Then
		nbul\time_out=150
	Else
		nbul\time_out=300
	EndIf
	
	nbul\sprite=LoadMesh("Mesh/lazer.x")
	PositionEntity nbul\sprite,EntityX(entity),EntityY(entity),EntityZ(entity)
	RotateEntity nbul\sprite,EntityPitch(entity),EntityYaw(entity),EntityRoll(entity)
	
	If wire=True Then
		EntityColor nbul\sprite,0,255,0
	Else
		EntityColor nbul\sprite,255,0,0
	EndIf
	
	MoveEntity nbul\sprite,0,0,-1
	TranslateEntity nbul\sprite,0,1,0
	ScaleEntity nbul\sprite,8,2,8
	EntityRadius nbul\sprite,.5
	EntityType nbul\sprite,NET_BULLET
	EntityAlpha nbul\sprite,1
	EmitSound (sfxshoot,entity)
End Function

;update bullet stuff
Function UpdateBullet.player( bul.Bullet )
	If CountCollisions( bul\sprite )	
			For k=1 To CountCollisions( bul\sprite )
				If bounce=False
					If GetEntityType( CollisionEntity( bul\sprite,k ) )=SCENE
						cx#=CollisionX( bul\sprite,k )
						cy#=CollisionY( bul\sprite,k )
						cz#=CollisionZ( bul\sprite,k )
						nx#=CollisionNX( bul\sprite,k )
						ny#=CollisionNY( bul\sprite,k )
						nz#=CollisionNZ( bul\sprite,k )
						Reload#=Reload#-1
						CreateSpark( bul\sprite )
						FreeEntity bul\sprite
						Delete bul
						Return
					EndIf
					If GetEntityType( CollisionEntity( bul\sprite,k ) )=TYPE_GRASS
						cx#=CollisionX( bul\sprite,k )
						cy#=CollisionY( bul\sprite,k )
						cz#=CollisionZ( bul\sprite,k )
						nx#=CollisionNX( bul\sprite,k )
						ny#=CollisionNY( bul\sprite,k )
						nz#=CollisionNZ( bul\sprite,k )
						Reload#=Reload#-1
						CreateSpark( bul\sprite )
						FreeEntity bul\sprite
						Delete bul
						Return
					EndIf
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=Type_BOT
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Reload#=Reload#-1
					CreateSpark( bul\sprite )
					FreeEntity bul\sprite
					Delete bul
					Return
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=NET
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Reload#=Reload#-1
					CreateSpark( bul\sprite )
					FreeEntity bul\sprite
					Delete bul
					Return
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=POLICE
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					hitsB#=hitsB#+1
					Reload#=Reload#-1
					CreateSpark( bul\sprite )
					FreeEntity bul\sprite
					Delete bul
					Return
				EndIf
			Next
			If bounce Then
				RotateEntity bul\sprite,0,180-EntityYaw(bul\sprite),0
			EndIf
		EndIf
	bul\time_out=bul\time_out-1
	If bul\time_out=0
		FreeEntity bul\sprite
		Delete bul
		Return
	EndIf
	MoveEntity bul\sprite,0,0,1
End Function

;update bullet stuff
Function UpdateNetBullet.player( bul.NetBullet )
	If CountCollisions( bul\sprite )	
			For k=1 To CountCollisions( bul\sprite )
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=SCENE
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=NET
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=BODY
					hits=hits+1
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
			Next
			ReloadB#=ReloadB#-1
			Fired=False
			If bounce Then
				RotateEntity bul\sprite,0,180-EntityYaw(bul\sprite),0
			Else
				CreateSpark( bul\sprite )
				FreeEntity bul\sprite
				Delete bul
				Return
			EndIf
		EndIf
	bul\time_out=bul\time_out-1
	If bul\time_out=0
		FreeEntity bul\sprite
		Delete bul
		Return
	EndIf
	MoveEntity bul\sprite,0,0,1
End Function

;create explosion
Function CreateSpark.Spark( entity )
	spark_sprite=LoadSprite( "GFX/Bigspark.BMP" )
	HideEntity spark_sprite
	EmitSound(sfxboom,entity)
	s.Spark=New Spark
	s\alpha=-90
	s\sprite=CopyEntity( spark_sprite,entity )
	EntityParent s\sprite,0
	Return s
End Function

;update explosion
Function UpdateSpark( s.Spark )
	If s\alpha<270
		sz#=Sin(s\alpha)*5+5
		ScaleSprite s\sprite,sz,sz
		RotateSprite s\sprite,Rnd(360)
		s\alpha=s\alpha+15
	Else
		FreeEntity s\sprite
		Delete s
	EndIf
End Function

;hole update?


Function writesettings()
	information=WriteFile("game.inf")
	WriteLine(information,name$)
	WriteLine(information,filename$)
	WriteLine(information,control#)
	WriteLine(information,traxlist$)
	CloseFile(information)
End Function

Function joycontrol(entity,netstatus$,PlayerID,PlrType)
	If laser=True Then
	If JoyHit(1) Or JoyHit(6) Then
		If PlrType=1 Then
		If Reload#<3 Then
			CreateBullet(entity)
			Reload#=Reload#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		Else
		If ReloadB#<3
			CreateNetBullet(entity)
			ReloadB#=ReloadB#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		EndIf
	EndIf
	EndIf

	MoveEntity entity,0,0,-(JoyY()/2)
			
	TranslateEntity entity,0,GRAVITY,0,True
	TurnEntity entity,0,-JoyX()*3,0

End Function

Function joy2control(entity,netstatus$,PlayerID,PlrType)
	If laser=True Then
	If JoyHit(1,1) Or JoyHit(6,1) Then
		If PlrType=1 Then
		If Reload#<3 Then
			CreateBullet(entity)
			Reload#=Reload#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		Else
		If ReloadB#<3
			CreateNetBullet(entity)
			ReloadB#=ReloadB#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		EndIf
	EndIf
	EndIf

	MoveEntity entity,0,0,-(JoyY(1)/2)
			
	TranslateEntity entity,0,GRAVITY,0,True
	TurnEntity entity,0,-JoyX(1)*3,0

End Function

Function keyboardcontrol(entity,netstatus$,PlayerID,PlrType)

	If laser=True Then
	If KeyHit(29) Or KeyHit(157) Then
		If PlrType=1 Then
		If Reload#<3 Then
			CreateBullet(entity)
			Reload#=Reload#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		Else
		If ReloadB#<3
			CreateNetBullet(entity)
			ReloadB#=ReloadB#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		EndIf
	EndIf
	EndIf

	If KeyDown(200)
		MoveEntity entity,0,0,.5
		
	Else If KeyDown(208)
		MoveEntity entity,0,0,-.5
	EndIf
	
	TranslateEntity entity,0,GRAVITY,0,True

	If KeyDown(203) Then
		TurnEntity entity,0,3,0
	Else If KeyDown(205) Then
		TurnEntity entity,0,-3,0	
	EndIf

End Function

Function mousecontrol(entity,netstatus$,PlayerID,PlrType)

	If laser=True Then
	If MouseHit(1) Then
		If PlrType=1 Then
		If Reload#<3 Then
			CreateBullet(entity)
			Reload#=Reload#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		Else
		If ReloadB#<3
			CreateNetBullet(entity)
			ReloadB#=ReloadB#+1
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,4,"BLASTED!")
			EndIf
		EndIf
		EndIf
	EndIf
	EndIf

	; Mouse x and y speed
	mxs#=MouseXSpeed()
	mys#=MouseYSpeed()

	mx#=MouseX()
	
	If mxs#>3 Then mxs#=3
	If mxs#<-3 Then mxs#=-3

	; Rest mouse position to centre of screen

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	If MouseDown(2)
		Espeed#=.5
	Else If MouseDown(3)
		Espeed#=-.5
	Else
		Espeed#=0
	EndIf
	
	TurnEntity entity,0,-mxs#,0

	TranslateEntity entity,0,GRAVITY,0
	speed#=Espeed
	MoveEntity entity,0,0,speed

End Function

Function cheatlist(thecheat$)
cheat$=Lower(thecheat$)
	Select cheat$
	
	Case "wanted":
	createbot()
			
	Case "solid tank":
	EntityAlpha tank,1

	Case "no tank"
	EntityAlpha tank,0

	Case "transparent tank"
	EntityAlpha tank,.5

	Case "wireframe"
	WireFrame True
	wire=True
	EntityColor map,0,255,0
	For bbb.bot=Each bot
		EntityColor bbb\botbody,0,255,0
	Next
	EntityColor tank,0,255,0
	CameraProjMode mapcam,0
	Damage=LoadAnimImage("GFX/DamageG.bmp",100,50,0,11)
	DamageB=LoadAnimImage("GFX/DamageG.bmp",100,50,0,11)
	
	Case "my corn is yellow"
	EntityColor tank,255,255,0
	EntityColor map,255,255,0
	CameraClsColor camera,255,255,0

	Case "normal walls"
	EntityColor map,255,255,255
	
	Case "transparent walls"
	EntityAlpha map,0.5

	

	Case "solid walls"
	EntityAlpha map,1
	
	Case "solid walls"
	EntityAlpha map,1
	
	Case "no walls"
	EntityAlpha map,0
	
	Case "whoop"
	laser=True
	
	Default:
	info("Cheat failed!")
	End Select
End Function

Function colorlist(thecheat$)
cheat$=Lower(thecheat$)
	Select cheat$
		
		Case "blue":
		EntityColor tank,0,0,255
		tanktoneA#=0
		tanktoneB#=0
		tanktoneC#=255
	
		Case "green":
		EntityColor tank,0,255,0
		tanktoneA#=0
		tanktoneB#=255
		tanktoneC#=0
		
		Case "red":
		EntityColor tank,255,0,0
		tanktoneA#=255
		tanktoneB#=0
		tanktoneC#=0		
		
		Case "black":
		EntityColor tank,0,0,0
		tanktoneA#=0
		tanktoneB#=0
		tanktoneC#=0
	
		Case "white":
		EntityColor tank,255,255,255
		tanktoneA#=255
		tanktoneB#=255
		tanktoneC#=255		
		
		Case "light blue":
		EntityColor tank,0,255,255
		tanktoneA#=0
		tanktoneB#=255
		tanktoneC#=255		
		
		Case "pink":
		EntityColor tank,255,0,255
		tanktoneA#=255
		tanktoneB#=0
		tanktoneC#=255		
				
		Case "yellow":
		EntityColor tank,255,255,0
		tanktoneA#=255
		tanktoneB#=255
		tanktoneC#=0
				
		Default
		info("color unknown, defaulting...")
		EntityColor tank,0,0,0
		tanktoneA#=0
		tanktoneB#=0
		tanktoneC#=0
		
	End Select 
End Function


Function info( t$ )
	inf.Info=New Info
	inf\txt$=t$
	Insert inf Before First Info
End Function

Function command(CMString$,Value$)
CString$=Lower(CMString$)
If CString$="cheat" Then
info("You cheated with the code "+value$)
			If netstatus$="On" Or netstatus$="Net" Then
				BP_UDPMessage (0,9,value$)
			EndIf
				cheatlist(value$)
				
Else If CString$="say" Then
	If netstatus$="On" Or netstatus$="Net" Then
		BP_UDPMessage (0,2,Value$)
	EndIf
		info("You Said: "+Value$)
		
Else If CString$="echo" Then
	info(value$)

Else If CString$="flush" Then
	If Lower(value$)="toilet" Then
		info("System: OH CRAP!!!")
		info("***FLUSH***")
		info("Toilet: gurgle")
	Else If Lower(value$)="log" Then
		flushinfo()
	Else
		info("Can't flush "+value$+".")
	EndIf

Else If CString$="engine" Then
	SoundVolume Engine,value

Else If CString$="mvol" Then
	ChannelVolume bgm,value

Else If CString$="track" Then
	StopChannel(bgm)
	track=value
	track=track-1

Else If CString$="traxlist" Then
	StopChannel(bgm)
	traxlist$=value$
	info("Will load when you start the program next time.")

Else If CString$="color" Then
	colorlist(value$)
	info("Changed tank color to "+value$+".")
Else If CString$="gravity" Then
	gravity=value
	info("Gravity changed from default value -.06 to "+gravity)

Else If CString$="" Then
tvalue$=Lower(value$)
	If tvalue$="fix" Then
		info("tank spun.")
		RotateEntity tank,0,0,0
		
	Else If tvalue$="respawn" Then
		info("tank re-dropped.")
		PositionEntity tank,0,0,0,True
	
	
	Else If tvalue$="addbot" Then
		createbot()
		info("bot added to game.")
	
	Else If tvalue$="report" Then
		info("Reporting...")
		reportbot()		
		info("End of report")
	
	Else If tvalue$="killbot" Then
		hidebot(Last bot)
		info("A bot was removed.")
	
	Else If tvalue$="screenshot" Then
		scrshot()
	
	Else If tvalue$="cls" Then
		flushinfo()
			
	Else If tvalue$="stats" Then
		info("Damage: "+Int(hits*10)+"%")

	Else If tvalue$="log" Then
		savelog()
		
	Else If tvalue$="final" Then
		Rounds#=2
		LRounds#=2
	
	Else If tvalue$="walrus32.dll" Then
		info("Yes, Tim farted on this too.")
		
	Else If tvalue$="2d" Then
		CameraViewport mapcam,0,0,GraphicsWidth(),GraphicsHeight()
		CameraClsMode mapcam,1,1

	Else If tvalue$="3d" Then
		CameraClsMode mapcam,0,1
		
		If botnumber#=1 Then
			CameraViewport mapcam,(GraphicsWidth()-(GraphicsWidth()/4)),(GraphicsHeight()/2-(GraphicsHeight()/4))+(GraphicsHeight()/8),GraphicsWidth()/4,GraphicsHeight()/4
		Else
			CameraViewport mapcam,(GraphicsWidth()-(GraphicsWidth()/4)),(GraphicsHeight()-(GraphicsHeight()/4)),GraphicsWidth()/4,GraphicsHeight()/4
		EndIf

	Else If tvalue$="suddendeath" Then
		showhits#=9
		showhitsB#=9
		
	Else
	info("Value "+value$+" unrecognized")
	EndIf
	
Else
	info("Command "+CMString$+"="+value$+" unrecognized.")
EndIf
	
End Function

Function flushinfo()
For inf.info = Each info
Delete inf
Next
info("Log flushed.")
End Function

Function savelog()
		file=WriteFile("log.txt")
		WriteLine(file,"Name="+name$)
		WriteLine(file,"Graphismode="+GraphicsWidth()+"x"+GraphicsHeight())
		WriteLine(file,"control#="+control#)
		WriteLine(file,"Tank Battle log file")
		WriteLine(file,"Log created at "+CurrentTime()+" on "+CurrentDate())
		WriteLine(file,"--------------End of log--------------")
		For inf.Info=Each Info
				WriteLine(file,inf\txt$)
		Next
		WriteLine(file,"-------------Start of log-----------------")
		CloseFile file
		info("Log file saved.")
End Function

Function ScrShot()
SaveBuffer(BackBuffer(),"Screenshots/screenshot.bmp")
info("Saved screen shot screenshot.bmp")
End Function


Function ClearAllWorld()

If netstatus$="On" Or netstatus$="Net" Then
	BP_UDPMessage (0,2, "I quit!")
	BP_EndSession ()
EndIf

If netstatus$="On" Or netstatus$="Net" Then
	BP_EndSession ()
EndIf

FreeEntity tank
FreeEntity mapcam

If lightex=1 Then
FreeEntity light
EndIf

For cc.bot=Each bot
	Delete cc
Next
For bu.Bullet=Each Bullet
	Delete bu
Next
For bn.NetBullet=Each NetBullet
	Delete bn
Next
For sp.Spark=Each Spark
	Delete sp
Next
For plr.player=Each player
	Delete plr
Next

FreeEntity grass
FreeEntity map
ClearWorld()
End Function

Function Win(Blue,MSG$)

FlushKeys()
FlushMouse()
FlushJoy()

ClearAllWorld()

Fired=False
reload=0
reloadb=0

StopChannel(BGM)

Local waitdelay=400
BGM=PlayMusic("BGM/Interim Nation - Sunset at Tioman Island, Part II.mp3")

Font=LoadFont("Microsoft Sans Serif",100,1,0,0)
SetFont Font

If blue=True Then
Color 0,0,255
Else
Color 255,0,0
EndIf

While Not KeyHit(1) Or KeyHit(28) Or KeyHit(57) Or MouseHit(1) Or JoyHit(1)
Cls
RenderWorld()
Text GraphicsWidth()/2,GraphicsHeight()/2,MSG$,1,1
WaitTimer(timer)
Flip
Wend

FlushJoy()
FlushMouse()
FlushKeys()

Font=LoadFont("Microsoft Sans Serif",28,0,0,0)
SetFont Font

credits()
End Function


Function RespawnDelay()

If showhits#>9 Then
showhits#=0
showhitsB#=0
info("Round Won!")
LRounds#=LRounds#+1
Else If showhitsB#>9 Then
showhits#=0
showhitsB#=0
info("Round Won!")
Rounds#=Rounds#+1
EndIf

If LRounds#=>3 Then
	Win(0,"Red wins!")
Else If Rounds#>=3 Then
	Win(1,"Blue wins!")
EndIf

For bul.bullet=Each bullet
	FreeEntity bul\sprite
	Delete bul
Next
For nul.NetBullet=Each NetBullet
	FreeEntity nul\sprite
	Delete nul
Next
For bbb.bot=Each bot
	EntityType bbb\botbody,0
	PositionEntity bbb\botbody,-50,1,40
	EntityType bbb\botbody,TYPE_BOT
Next
For sp.Spark=Each Spark
	FreeEntity sp\sprite
	Delete sp
Next

Fired=False
reload=0
reloadb=0

StopChannel(BGM)

Local waitdelay=400
BGM=PlayMusic("BGM/First_Impressions_Last_start.mp3")

Font=LoadFont("Microsoft Sans Serif",100,1,0,0)
SetFont Font
Color 255,255,255

While waitdelay>0
Cls
RenderWorld()
waitdelay=waitdelay-1
WaitTimer(timer)

Text GraphicsWidth()/2,GraphicsHeight()/2,Int(waitdelay/100),1,1
Flip
Wend

FlushJoy()
FlushMouse()
FlushKeys()

Font=LoadFont("Microsoft Sans Serif",28,0,0,0)
SetFont Font
End Function