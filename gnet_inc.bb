
Const GNET_HOST$="www.blitzbasic.com"
Const GNET_PORT=80
Const GNET_GET$="/gnet/gnet.php"

Type GNET_Server
	Field game$,server$,ip$
End Type

Function GNET_Esc$( t$ )
	t$=Replace$( t$,"&","" )
	t$=Replace$( t$,"%","" )
	t$=Replace$( t$,"'","" )
	t$=Replace$( t$,Chr$(34),"" )
	t$=Replace$( t$," ","_" )
	Return t$
End Function

Function GNET_Open( opt$ )
	t=OpenTCPStream( GNET_HOST$,GNET_PORT )
	If Not t Return 0
	
	WriteLine t,"GET "+GNET_GET$+"?opt="+opt$+" HTTP/1.0"
	WriteLine t,"HOST: "+GNET_HOST$
	WriteLine t,""
	
	While ReadLine$(t)<>""
	Wend
	
	Return t
End Function

Function GNET_Exec( opt$,game$,server$ )
	opt$=opt$+"&game="+GNET_Esc$(game$)
	If server$<>"" opt$=opt$+"&server="+GNET_Esc$(server$)
	t=GNET_Open( opt$ )
	If Not t Return 0
	
	ok=False
	If( ReadLine$(t)="OK" ) ok=True
	
	CloseTCPStream t
	Return ok
End Function

Function GNET_Ping$()
	t=GNET_Open( "ping" )
	If Not t Return 0
	
	ip$=ReadLine$(t)
	
	CloseTCPStream t
	Return ip$
End Function

Function GNET_AddServer( game$,server$="" )
	Return GNET_Exec( "add",game$,server$ )
End Function

Function GNET_RefreshServer( game$,server$="" )
	Return GNET_Exec( "ref",game$,server$ )
End Function

Function GNET_RemoveServer( game$ )
	Return GNET_Exec( "rem",game$,"" )
End Function

Function GNET_ListServers( game$="" )
	Delete Each GNET_Server
	t=GNET_Open( "list" )
	If Not t Return 0
	
	Repeat
		t_game$=ReadLine$(t)
		If t_game$="" Exit
		t_server$=ReadLine$(t)
		t_ip$=ReadLine(t)
		If game$="" Or game$=t_game$
			p.GNET_Server=New GNET_Server
			p\game$=t_game$
			p\server$=t_server$
			p\ip$=t_ip$
		EndIf
	Forever
	
	CloseTCPStream t
	Return 1
	
End Function