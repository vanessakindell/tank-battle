

;;simple drawing snake game thing....

Graphics 640,480,0,2 

x=160 
y=120 

Color 255,255,255 

R=255 
g=0 
b=0 

Text 10,10,"initializing..." 
If VoiceInit()=0 Then 
   End 
EndIf 

loadwordlist() 

Rect x,y,10,10,True 

;VoiceSay("Welcome to speech draw 1") 

test=True 
aa$="say something" 
While test 

    
   If VoiceBlockForResult()=1 Then 
      a$ = VoiceTestBlock() 
      aa$ = a$ 
   Else 
      a$="" 
   EndIf 
    
   Color 255,255,255 
   ;Text 10,10,icount 
   Color 0,0,0 
   Rect 0,350,120,420,1 
   Color 255,255,255 
   Text 10,400,aa$ 
   ;Delay 1000 
    
   thisid=getwordid(aa$) 
   Select thisid;Lower(a$) 
      Case 0;"up","a","cop" 
         dir=1 
      Case 1;"down" 
         dir=3 
      Case 2;"left" 
         dir=4 
      Case 3;"right" 
         dir=2 
      Case 5 
         r=255 
         g=0 
         b=0 
      Case 4 
         r=0 
         g=0 
         b=255 
      Case 7 
         r=0 
         g=255 
         b=0 
      Case 8 
         dir=0 
      Case 9 
         Print "finishing up" 
         VoiceShutdown() 
          
         End 
   End Select 
    
   If GetKey()<>0 
      Print "finishing up" 
          
         VoiceShutdown() 
         End 
   EndIf 
    
   Select dir 
      Case 1 
         y=y-1 
      Case 2 
         x=x+1 
      Case 3 
         y=y+1 
      Case 4 
         x=x-1 
   End Select 
    
   Color r,g,b 
   Rect x,y,10,10,True 
   icount=icount + 1 
   Text 10,40,"up,down,left,right,blue," 
   Text 10,60,"red,green,clear,stop,quit" 
   fade(True,0,1,0,0) 
   Delay 1 
    
   If KeyHit(1) Then test=False 
Wend 

VoiceShutdown() 

End 


Function fade(fadein=False,pause=0,inc=10,xpos=0,ypos=0) 
   ;image1=image to fade in or out 
   ;fadein(default false) - is image to fade in or out? 
   ;pause= delay between frames 
   ;inc= how many colour points to jump per frame 
   ;x and y pos are position of image 

;   setup limits etc 
;   ti=CopyImage (image1) 
   wd=320;ImageWidth(ti) 
   ht=256;ImageHeight(ti) 
    
    
;   For icount=0 To 255/inc 
   ;fade an image out to black       
      SetBuffer FrontBuffer();ImageBuffer(ti) 
      LockBuffer FrontBuffer();ImageBuffer(ti) 
      For x=0 To wd-1 
         For y=0 To ht-1 
            ;fade pixels to black 
            argb=ReadPixelFast (x,y,FrontBuffer());ImageBuffer(ti)) 
             
            tr=(argb Shr 16) And $ff 
            tg=(argb Shr 8) And $ff 
            tb=argb And $ff 
             
            tr=tr - inc 
            tg=tg - inc 
            tb=tb - inc 
             
            If tr<0 Then tr=0 
             
            If tg<0 Then tg=0 
             
            If tb<0 Then tb=0 
             
            newargb=((tr Shl 16) Or (tg Shl 8) Or tb) 
    
            WritePixelFast x,y,newargb,FrontBuffer();ImageBuffer(ti) 
         Next 
      Next 
      UnlockBuffer FrontBuffer();ImageBuffer(ti) 
       
      SetBuffer FrontBuffer() 
;      Cls 
;      DrawImage ti,xpos,ypos 
;      Flip 
;      Delay pause 
;   Next 
    
;   FreeImage ti 
;   ti=0 
End Function 


Type word 
   Field original$ 
   Field id 
End Type 

Type altword 
   Field id 
   Field alt$ 
   Field hits 
End Type 


Function getwordid(a$) 
   a$=Lower(a$) 
   For wd.word=Each word 
      If wd\original = a$ 
         Return wd\id 
      EndIf 
   Next 
    
   For tw.altword=Each altword 
      If tw\alt=a$ Then 
         Return tw\id 
      EndIf 
   Next 
   Return -1 
End Function 

Function loadwordlist() 
   file=ReadFile ("word.txt") 

   temp$=ReadLine(file) 
    
   While Not Eof(file) 
      If temp$="#" 
         ;start new word 
         wd.word=New word 
         wd\id=ReadLine (file) 
         wd\original=ReadLine(file) 
         temp="" 
          
         Repeat 
            ;add all alt words 
            temp$=ReadLine(file) 
            td.altword=New altword 
            td\id=wd\id 
            td\alt=temp 
          
         Until temp$="#"    
      EndIf 
   Wend 

   CloseFile file 
End Function