function getSession(create){
	session=ctx.getChild('session');
	if(!session&&create){
		session=new CompositeMap('session');
		ctx.addChild(session);
	}
	return session;
}

function getCookie(create){
	cook=ctx.getChild('cookie');
	if(!cook&&create){
		cook=new CompositeMap('cookie');
		ctx.addChild(cook);
	}
	return cook;
}

function getRequest(){
	req=ctx.getChild('request');
}


function raise_app_error(code){
	throw code;
}