/*
 *	"Deserted" test environment
 */

#define NECRO_DEBUG 1

#include <windows.h>

#define NECRO_WIN 1

#include "../../necro_3d/n3d.h"
#include "../../necro_math/nmath.h"
#include "../../necro_core/ncore.h"
#include "../../necro_cls/ncls.h"

#pragma comment(lib, "necro_3d_d.lib")
#pragma comment(lib, "necro_math_d.lib")
#pragma comment(lib, "necro_cls_d.lib")
#pragma comment(lib, "necro_core_d.lib")
#pragma comment(lib, "winmm.lib")
#pragma comment(lib, "libfreetype.lib")
#pragma comment(lib, "libxml2.lib")

#include "main.h"
#include "modules/Chat.h"
#include "Entity.h"

// GLOBAL VARIABLES
MODULES::CChat * o_chat;
N3D::cDebugBillboard dbillboard;
char BufferOnScreen[5][500];
int currentLine = 0; // The line currently processed
char Buffer[500] = ">"; // A chat line, typically

// General
char targetId[100];

// Faith
char faithId[100];
bool ask4GiveFaith = false;
bool ask4BreakFaith = false;
bool breakFaithAvailable = false; // Part of a sect, might break faith

// Magic
char magicId[100];
bool askingSpell = false; // Asking for a new spell ?
bool vampireSpell; // Type of spell
bool partOfSpell = false; // Taking part of a new spell ?
bool ask4ConfirmSpell = false; // Want to confirm a spell.

// DummyReceiver dedicated objects and vars.

Entity dumEntity[100]; // A lot... But not that much.
Entity usedEntity[100]; // Yep, there is a concurrency problem here, so we first copy the dumEntity before we render them

// PlayerReceiver dedicated objects and vars.

Entity playEntity[1000]; // A lot... But not that much.
Entity usedPlayEntity[1000];

int nbDumEnt = 0;
int nbPlayEnt = 0;
Entity myEntity;
int playerId; // Not so good... :O(


void PrintTextMessage(char * msg)
{
	if(msg != NULL && strlen(msg) > 0)
	{
		strcpy(BufferOnScreen[currentLine], msg);
		currentLine++;
		if (currentLine >= 5) currentLine = 0;
	}
}

// include cpp's where some functions have been defined (to simplify the reading of this code)
#include "config.cpp"
#include "thread.cpp"


int WINAPI WinMain (HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR cmdline, int nCmdShow)
{
	bool debugDisplay = false;
	bool firstLoop = true; // So the first time we trust the server to get our position...
	bool in_magic_sel = false; // Do we select a spell ???
	bool targeting = false; // Do we target someone ?
	bool insideMagicTriangle = false; // We are inside a magic triangle, we can be part of a spell...
	bool giveFaithSelected = false;
	bool magicSelSelected = false;
	bool magicAddSelected = false;
	bool breakFaithSelected = false;

	bool giveFaithSelLock = false; // Selection is locked (after a clic) -> ok for action (second click or enter with real name)
	bool breakFaithSelLock = false;
	bool magicAddSelLock = false;
	bool magicSelSelLock = false;
	bool in_sel_mode=false; // Are we moving or selecting on screen
	bool shiftPressed;
	char nameBuffer[200] = ""; // The "real name" buffer
	int iBufPos = 0;
	bool keyWasAdded = false; // A key was added to the buffer
	int timeSince = 0;  // The time since the key was added
	char lastChar = '>'; // Last character pressed
	bool pbTGA = false;

	// init the Necro3D engine
	N3D::initialize();
	N3D::cRenderMgr &rnd = N3D::cRenderMgr::getSingleton();
	rnd.setRenderer(N3D::RENDERER_OPENGL_WIN32);

	NCORE::initialize();

	NCORE::cWin32Mgr* p = NCORE::cWin32Mgr::getSingletonPtr();
	p->setHInstance(reinterpret_cast<uint32>(hInstance));
	eErrorCode retval = p->createWindow(800, 600, 0, "Deserted!!");

	if(retval == ERR_NONE)
	{
		N3D::cRenderMgr::getSingletonPtr()->CreateContext(p, 32, 0);
	}

	rnd.setPerspective(45.0f,0.0f,0.1f,1000000.0f);

	// load the config files from the config file
	loadConfig();

	// init the network core
	o_chat = new MODULES::CChat(p_name, p_faction);
	// network setup
	/******************** *************************************************************************/
	/** new chat login procedure! **/
	/******************** *************************************************************************/
	o_chat->Init(p_host, p_port);
	// disable keep-alive (this is disabled by default, but anyway...)
	o_chat->KeepAlive(false);

	// init the game...
	N3D::c3dModel model,skysphere,croix,ghost,pacman,qmark,emark,castle,magiccircle,circle;
	N3D::cSprite myCharacter;
	N3D::cSprite magicSelMini,magicAdd,giveFaith,breakFaithSel;
	N3D::cSprite magicSelMiniSel,magicAddSel,giveFaithSel,breakFaith;
	N3D::cSprite magicSel;
	N3D::cBillboard monsters,auraball1,auraball2;
	N3D::cTextureImage myBillTxt;
	//N3D::cFont myFont;
	sint16 ourFont = rnd.loadFont("fonts/georgia.ttf");
	bool pbFont = false;
	if (ourFont!= ERR_NONE)
	{
		pbFont = true;
	}

	BufferOnScreen[0][0]='\0';
	BufferOnScreen[1][0]='\0';
	BufferOnScreen[2][0]='\0';
	BufferOnScreen[3][0]='\0';
	BufferOnScreen[4][0]='\0';

	model.load("models\\desert.3ds");
	//model.load("models\\landTry.obj");
	model.generateSmoothNormals();
	skysphere.load("models\\skysphere.3ds");
	skysphere.center();
	skysphere.fit(400.0f);

	// For test ONLY !
	croix.load("models\\RoseL.3ds");
	pacman.load("models\\PacM2.3ds");
	ghost.load("models\\Ghostc4d.3ds");
	qmark.load("models\\Int.3ds");
	emark.load("models\\Excl.3ds");
	castle.load("models\\Cast4.3ds");
	magiccircle.load("models\\Cercle3.3ds");
	circle.load("models\\circle.3ds");

	croix.generateSmoothNormals();
	pacman.generateSmoothNormals();
	ghost.generateSmoothNormals();
	qmark.generateSmoothNormals();
	emark.generateSmoothNormals();
	magiccircle.generateSmoothNormals();

	myCharacter.loadFromImage( "images\\cactuar.tga",0,0);

	// Our icones... Plus the selected ones.
	magicSelMini.loadFromImage( "images\\magicSelMini.tga",0,0);
	magicAdd.loadFromImage( "images\\magicAdd.tga",0,0);
	giveFaith.loadFromImage( "images\\giveFaith.tga",0,0);
	magicSelMiniSel.loadFromImage( "images\\magicSelMini.tga",0,0);
	magicAddSel.loadFromImage( "images\\magicAdd.tga",0,0);
	giveFaithSel.loadFromImage( "images\\giveFaithSel.tga",0,0);
	magicSel.loadFromImage( "images\\MagicSel.tga",0,0);
	breakFaithSel.loadFromImage( "images\\breakFaithSel.tga",0,0);
	breakFaith.loadFromImage( "images\\breakFaith.tga",0,0);
	NMATH::cVector3 rposChar ; //= {10.0,10.0,10.0};
	rposChar.x = 0.0;
	rposChar.z = 0.0;
	rposChar.y = 0.0;
	monsters.assign(rnd.LoadTexture("images\\AuraBall.tga",0),rposChar,256,0,0,256,256,2.0f,0.0f,0,4.0f);
	auraball1.assign(rnd.LoadTexture("images\\AuraBall.tga",0),rposChar,256,0,0,256,256,2.0f,0.0f,0,4.0f);
	auraball2.assign(rnd.LoadTexture("images\\AuraBall2.tga",0),rposChar,256,0,0,256,256,2.0f,0.0f,0,4.0f);

	NCLS::cCollisionMgr clsmgr;

	clsmgr.loadFromNCM("solid_data.ncm");



	NCLS::cCollisionEntity * object = clsmgr.addObject(NMATH::cSphere(0,10,0,0.5f));
	object->addSelfFlags(NCLS::cCollisionEntity::ENABLE_SLIDING|NCLS::cCollisionEntity::ENABLE_COLLISION);
	object->setStepUp(0.1f);
	object->addCollisionFlags(1);

	//Heightmap
	N3D::c3dModel hm_model;
	{
		NMATH::cIndexedMesh3 *hm_mesh = new NMATH::cIndexedMesh3;
		clsmgr.getHeightmap()->toMesh(*hm_mesh);
		scalar hmClr[4] = {0.0f,1.0f,0.3f,1.0f};
		hm_model.addMesh(*hm_mesh,hmClr);
		hm_model.generateSmoothNormals();
		delete hm_mesh;
	}


	// Collision for castle
	NMATH::cVector3 posCastl( 12.2, 4.2,1323.2);
	posCastl.x = 12.2;
	posCastl.z = 244.2;
	//posCastl.y = clsmgr.getHeightmap()->getHeight(posCastl.x,posCastl.z);
	posCastl.y = 0.0;

	NMATH::cVector3 dimCastle( 20.0, 20.0,20.0); 

	N3D::cPointLight	pointlight;
	rnd.Enable(N3D::N3DM_LIGHTING);
	pointlight.setStandardLightColor();
	pointlight.setPosition(NMATH::cVector3(30,70,40));
	rnd.ConnectLight(true,&pointlight);
	rnd.SetLightStatus(N3D::LIGHT_ON,&pointlight);

	//CREATE BILLBOARDS

	N3D::cBillboardCollection	billboards;
	for(uint32 ct=0; ct < 80;ct++)
	{
		N3D::cBillboard *billboard = new N3D::cBillboard;
		NMATH::cVector3 rpos;
		rpos.x = static_cast<scalar>(rand() % 10000) / 10.0f - 100.0f;
		rpos.z = static_cast<scalar>(rand() % 10000) / 10.0f - 100.0f;
		rpos.y = clsmgr.getHeightmap()->getHeight(rpos.x,rpos.z);
		//billboard->assign(rnd.LoadTexture("images\\cactuar.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);
		billboard->assign(rnd.LoadTexture("images\\Decor\\Tree.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);


		billboards.add(billboard);

		NCLS::cCollisionEntity * ent = clsmgr.addObject(NMATH::cAABB(rpos,NMATH::cVector3(0.5f,6.0f,0.5f)));
		ent->setSelfFlags(1 | NCLS::cCollisionEntity::ENABLE_COLLISION);
	}
	for(uint32 ct=0; ct < 80;ct++)
	{
		N3D::cBillboard *billboard = new N3D::cBillboard;
		NMATH::cVector3 rpos;
		rpos.x = static_cast<scalar>(rand() % 10000) / 5.0f - 100.0f;
		rpos.z = static_cast<scalar>(rand() % 10000) / 5.0f - 100.0f;
		rpos.y = clsmgr.getHeightmap()->getHeight(rpos.x,rpos.z);
		//billboard->assign(rnd.LoadTexture("images\\cactuar.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);
		billboard->assign(rnd.LoadTexture("images\\Decor\\Tree2.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);


		billboards.add(billboard);

		NCLS::cCollisionEntity * ent = clsmgr.addObject(NMATH::cAABB(rpos,NMATH::cVector3(0.5f,6.0f,0.5f)));
		ent->setSelfFlags(1 | NCLS::cCollisionEntity::ENABLE_COLLISION);
	}
	for(uint32 ct=0; ct < 180;ct++)
	{
		N3D::cBillboard *billboard = new N3D::cBillboard;
		NMATH::cVector3 rpos;
		rpos.x = static_cast<scalar>(rand() % 10000) / 30.0f - 100.0f;
		rpos.z = static_cast<scalar>(rand() % 10000) / 30.0f - 100.0f;
		rpos.y = clsmgr.getHeightmap()->getHeight(rpos.x,rpos.z);
		//billboard->assign(rnd.LoadTexture("images\\cactuar.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);
		billboard->assign(rnd.LoadTexture("images\\Decor\\Plant.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);


		billboards.add(billboard);

		NCLS::cCollisionEntity * ent = clsmgr.addObject(NMATH::cAABB(rpos,NMATH::cVector3(0.5f,6.0f,0.5f)));
		ent->setSelfFlags(1 | NCLS::cCollisionEntity::ENABLE_COLLISION);
	}
	for(uint32 ct=0; ct < 80;ct++)
	{
		N3D::cBillboard *billboard = new N3D::cBillboard;
		NMATH::cVector3 rpos;
		rpos.x = static_cast<scalar>(rand() % 10000) / 10.0f - 100.0f;
		rpos.z = static_cast<scalar>(rand() % 10000) / 10.0f - 100.0f;
		rpos.y = clsmgr.getHeightmap()->getHeight(rpos.x,rpos.z);
		//billboard->assign(rnd.LoadTexture("images\\cactuar.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);
		billboard->assign(rnd.LoadTexture("images\\Decor\\DCub.tga",0),rpos,256,0,0,256,256,2.0f,0.0f,0,4.0f);


		billboards.add(billboard);

		NCLS::cCollisionEntity * ent = clsmgr.addObject(NMATH::cAABB(rpos,NMATH::cVector3(0.5f,6.0f,0.5f)));
		ent->setSelfFlags(1 | NCLS::cCollisionEntity::ENABLE_COLLISION);
	}

	//CREATE BILLBOARDS

	//Player vectors
	NMATH::cVector3 p_dir(0,0,-1),p_up(0,1,0),p_right(1,0,0),p_move(0,0,0);

	//N3D::cDebugBillboard	dbillboard;
	dbillboard.setShadowColor(NMATH::cVector3(0,0,0));
	dbillboard.setTextColor(NMATH::cVector3(0.6f,0.6f,1.0f));

	// before everything (we must login...)
	int errorCode;

	/******************** *************************************************************************/
	/** new chat login procedure! **/
	/******************** *************************************************************************/
	errorCode = o_chat->Login(p_nick, p_password);
	if(errorCode != 0)
	{
		// there was an error on the login
		NCORE::cLogMgr::getSingleton().getLog("log").debug("Error while doing login code(%d)", errorCode);
		// TODO: need to handle this later...
	}

	// all player join the global chat room
	errorCode = o_chat->JoinChatroom("universe");
	if (errorCode != 0) {
		// there was an error on the join room
		NCORE::cLogMgr::getSingleton().getLog("log").debug("Error while doing join room code(%d)", errorCode);
		// TODO: need to handle this later...
	}

	if(createNetThread())
	{
		NCORE::cLogMgr::getSingleton().getLog("log").debug("Thread not created!");
		// TODO: handle this later...
	}

	int delay = 0;

	// game main loop goes here...
	while(p->flushEvents() != ERR_APP_WANTS_OUT)
	{
		rnd.StartRendering();

		// The thread can rerun itself
		delay++;
		if (delay == 100) {
			runNetThread();
			delay = 0;
		}

		//dbillboard.addMessage(1,"Speed: %2.2f,%2.2f,%2.2f",p_move.x,p_move.y,p_move.z);
		if (debugDisplay) dbillboard.addMessage(1,"Player Id %s",playerId);
		if (debugDisplay)
		{
			if ( pbTGA )
				dbillboard.addMessage(1,"Echec TGA");
			if ( pbFont )
				dbillboard.addMessage(1,"Echec Font : %d",ourFont);
		}

		if (GetAsyncKeyState(VK_F1))
		{
			debugDisplay = true;
		}
		if (GetAsyncKeyState(VK_F2))
		{
			debugDisplay = false;
		}

		if ( ! in_sel_mode )
			//Movement
		{
			POINT	mouse;

			GetCursorPos(&mouse);
			SetCursorPos(400,300);//Create reasonable angles
			scalar angly=(float)((mouse.x - 400)) / 1000.0f;
			scalar anglz=(float)((mouse.y - 300)) / 1000.0f;

			NMATH::cMatrix4x4 rot;

			//Get a rotation matrix
			rot.getRotation(-anglz,p_right);
			p_dir = rot * p_dir;
			p_up = rot * p_up;

			//Rotate around up axis
			rot.getYrotation(-angly);
			p_dir = rot * p_dir;
			p_up = rot * p_up;
			p_right = rot * p_right;

			p_move.y -= 0.01f;

			/*if((GetAsyncKeyState(VK_SPACE)&0x8000) && (p_move.y <= 0.1f) && (object->getGroundNormal().y > 0.7f) )
			{
			p_move.y = 0.3f;
			}*/

			NMATH::cVector3 delta = p_move;

			if((object->getGroundNormal().y < 0.7f) && (object->getGroundNormal().dotProduct(object->getGroundNormal()) > 0.9f))
			{
				delta += object->getGroundNormal()*0.3f;
				delta.y = -0.1f;
			}

			// Motion

			if(GetAsyncKeyState(VK_UP)) delta += NMATH::cVector3(p_dir.x,0,p_dir.z).unitVector() * 0.1f;
			if(GetAsyncKeyState(VK_DOWN)) delta += -NMATH::cVector3(p_dir.x,0,p_dir.z).unitVector() * 0.1f;
			if(GetAsyncKeyState(VK_LEFT)) delta += -p_right * 0.1f;
			if(GetAsyncKeyState(VK_RIGHT)) delta += p_right * 0.1f;
			/*if(GetAsyncKeyState('W')) delta += NMATH::cVector3(p_dir.x,0,p_dir.z).unitVector() * 0.1f;
			if(GetAsyncKeyState('S')) delta += -NMATH::cVector3(p_dir.x,0,p_dir.z).unitVector() * 0.1f;
			if(GetAsyncKeyState('A')) delta += -p_right * 0.1f;
			if(GetAsyncKeyState('D')) delta += p_right * 0.1f;*/
			// Statut
			/*if(GetAsyncKeyState('1')) myStatut = ( myStatut | IMARK ) & ~EMARK; // With interrogation, exclude exclamation
			if(GetAsyncKeyState('2')) myStatut = ( myStatut | EMARK ) & ~IMARK; // With exclamation,
			if(GetAsyncKeyState('3')) myStatut = ( myStatut | AURA ) & ~PLAYER; // In aura mode
			if(GetAsyncKeyState('4')) myStatut = ( myStatut | PLAYER ) & ~AURA; // In player mode
			if(GetAsyncKeyState('5')) myStatut = myStatut & (~IMARK) & (~EMARK); // Normal mode
			*/
			if(GetAsyncKeyState(VK_INSERT)) in_sel_mode = true;

			// Manage the keyboard for the chat

			// The keypressed rate... If we have added a key in the buffer, then we wait to check another time

			if ( (keyWasAdded) && (timeSince < KEYRATE ) && GetAsyncKeyState(lastChar))
			{
				timeSince++;
			}
			else
			{
				keyWasAdded = false;
				shiftPressed = false;
				timeSince = 0;
				if (iBufPos < 500)
				{
					if(GetAsyncKeyState(VK_SHIFT ) || GetAsyncKeyState(VK_CAPITAL )) 
					{
						shiftPressed = true;
					}

					for (int iStates = 0x30; iStates <= 0x5A ; iStates++)
					{
						if (GetAsyncKeyState(iStates))
						{
							lastChar = iStates;
							keyWasAdded = true;
							if ( (!shiftPressed) && (iStates >= 'A') )
							{
								// It's a letter, let push the minuscule
								Buffer[iBufPos++]=iStates-'A'+'a';
							}
							else
							{
								Buffer[iBufPos++]=iStates;
							}
							Buffer[iBufPos] = '\0';
						}
					}
					if(GetAsyncKeyState(VK_SPACE)) 
					{
						keyWasAdded = true;
						Buffer[iBufPos++]=' ';
						Buffer[iBufPos] = '\0';
						lastChar = VK_SPACE;
					}
					if(GetAsyncKeyState(VK_OEM_COMMA))
					{
						keyWasAdded = true;
						Buffer[iBufPos++]=',';
						Buffer[iBufPos] = '\0';
						lastChar = VK_OEM_COMMA;
					}
					if(GetAsyncKeyState(VK_OEM_PERIOD))
					{
						keyWasAdded = true;
						Buffer[iBufPos++]='.';
						Buffer[iBufPos] = '\0';
						lastChar = VK_OEM_PERIOD;
					}
					if(GetAsyncKeyState('!'))
					{
						keyWasAdded = true;
						Buffer[iBufPos++]='!';
						Buffer[iBufPos] = '\0';
						lastChar = '!';
					}
					if(GetAsyncKeyState(VK_OEM_2) && shiftPressed)
					{
						keyWasAdded = true;
						Buffer[iBufPos++]='?';
						Buffer[iBufPos] = '\0';
						lastChar = VK_OEM_2;
					}
					/*if(GetAsyncKeyState('"'))
					{
					Buffer[iBufPos++]='"';
					Buffer[iBufPos] = '\0';
					}
					if(GetAsyncKeyState('('))
					{
					Buffer[iBufPos++]='(';
					Buffer[iBufPos] = '\0';
					}
					if(GetAsyncKeyState(')'))
					{
					Buffer[iBufPos++]=')';
					Buffer[iBufPos] = '\0';
					}*/
					if(GetAsyncKeyState(VK_OEM_1)&& !shiftPressed)
					{
						keyWasAdded = true;
						Buffer[iBufPos++]=';';
						Buffer[iBufPos] = '\0';
						lastChar = VK_OEM_1;
					}
					if(GetAsyncKeyState(VK_OEM_1)&& shiftPressed)
					{
						keyWasAdded = true;
						Buffer[iBufPos++]=':';
						Buffer[iBufPos] = '\0';
						lastChar = VK_OEM_1;
					}
					if(GetAsyncKeyState(VK_OEM_MINUS))
					{
						keyWasAdded = true;
						Buffer[iBufPos++]='-';
						Buffer[iBufPos] = '\0';
						lastChar = VK_OEM_MINUS;
					}
					if(GetAsyncKeyState(VK_MULTIPLY))
					{
						keyWasAdded = true;
						Buffer[iBufPos++]='*';
						Buffer[iBufPos] = '\0';
						lastChar = VK_MULTIPLY;
					}
					/*if(GetAsyncKeyState('<'))
					{
					Buffer[iBufPos++]='<';
					Buffer[iBufPos] = '\0';
					}
					if(GetAsyncKeyState('>'))
					{
					Buffer[iBufPos++]='>';
					Buffer[iBufPos] = '\0';
					}*/
					if(GetAsyncKeyState(VK_DIVIDE))
					{
						keyWasAdded = true;
						Buffer[iBufPos++]='/';
						Buffer[iBufPos] = '\0';
						lastChar = VK_DIVIDE;
					}
				}
				if (iBufPos > 0)
				{
					if(GetAsyncKeyState(VK_BACK)) 
					{
						keyWasAdded = true;
						iBufPos--;
						Buffer[iBufPos] = '\0';
						lastChar = VK_BACK;
					}
				}
				if (GetAsyncKeyState(VK_RETURN))
				{
					keyWasAdded = true;
					lastChar = VK_RETURN;
					// send this to the universe room
					NCORE::cLogMgr::getSingleton().getLog("log").debug("Sending: %s", Buffer);
					// as a precaution ignore empty strings...
					if(Buffer != NULL && strlen(Buffer) > 0)
					{
						errorCode = o_chat->AddMessage("universe", Buffer);
						if (errorCode != 0) {
							NCORE::cLogMgr::getSingleton().getLog("log").debug("Error sending to the universe room!");
							// TODO: error recovery
						}
						// clear the send buffer
						iBufPos=0;
						Buffer[0]='>';
						Buffer[1]='\0';
					}
				}
			}





			//dbillboard.addMessage(1,"Move before: %2.2f,%2.2f,%2.2f",delta.x,delta.y,delta.z);
			//dbillboard.addMessage(1,"Pos ?: %2.2f,%2.2f",object->getPos().x,object->getPos().z);

			clsmgr.moveObject(object,delta);

			//dbillboard.addMessage(1,"Move after: %2.2f,%2.2f,%2.2f",delta.x,delta.y,delta.z);

			//dbillboard.addMessage(1,"Ground normal: %2.2f,%2.2f,%2.2f",object->getGroundNormal().x,object->getGroundNormal().y,object->getGroundNormal().z);

			p_move.y = delta.y;

			NMATH::cMatrix4x4 mmat;
			mmat.getLookToCam(object->getPos(),p_dir,p_up,p_right);
			rnd.SetTransformMatrix(mmat);
		}
		else
		{
			POINT	mouse;



			if(GetAsyncKeyState(VK_HOME)) 
			{
				in_sel_mode = false; 
				giveFaithSelLock = false; 
				breakFaithSelLock = false;
				magicAddSelLock = false;
				magicSelSelLock = false;
				in_magic_sel = false;
			}
			GetCursorPos(&mouse);
			int yMousePos= rnd.getHeight() - mouse.y;

			//scalar angly=(float)((mouse.x - 400)) / 1000.0f;
			//scalar anglz=(float)((mouse.y - 300)) / 1000.0f;

			/*For Info
			bool giveFaithSelLock = false; // Selection is locked (after a clic) -> ok for action (second click or enter with real name)
			bool breakFaithSelLock = false;
			bool magicAddSelLock = false;
			bool magicSelSelLock = false;
			*/
			if (GetAsyncKeyState(VK_RBUTTON))
			{
				// Cancel the locks.
				giveFaithSelLock = false; 
				breakFaithSelLock = false;
				magicAddSelLock = false;
				magicSelSelLock = false;

				// Push down the selection
				breakFaithSelected = false;
				giveFaithSelected = false;
				magicAddSelected = false;
				magicSelSelected = false;
				in_magic_sel = false;
			}


			{
				if (!breakFaithSelLock)
				{
					breakFaithSelected = false;
				}
				if (!giveFaithSelLock)
				{
					giveFaithSelected = false;
				}
				if (!magicAddSelLock)
				{
					magicAddSelected = false;
				}
				if (!magicSelSelLock)
				{
					magicSelSelected = false;
				}

				// Position of icones
				if ( (mouse.x >= 10 ) && (mouse.x <= 74) )
				{
					// First row...
					if ( ( yMousePos >= 100 ) && (yMousePos <= 164))
					{
						// Ok, on the giveFaith icone
						giveFaithSelected = true; // Anyway...
						if (GetAsyncKeyState(VK_LBUTTON))
						{
							if ( !giveFaithSelLock )
							{
								giveFaithSelLock = true;
							}
							else
							{
								if (targeting)
								{
									strcpy(faithId,targetId);
									ask4GiveFaith = true;

									breakFaithSelected = false;
									giveFaithSelected = false;
									magicAddSelected = false;
									magicSelSelected = false;
									in_sel_mode = false; 
									giveFaithSelLock = false; 
									breakFaithSelLock = false;
									magicAddSelLock = false;
									magicSelSelLock = false;
									in_magic_sel = false;
								}
							}
						}

					}

					if ( ( yMousePos >= 300 ) && (yMousePos <= 364))
					{
						// Ok, on the magicSel icone
						magicSelSelected = true; // Anyway...

						if (GetAsyncKeyState(VK_LBUTTON))
						{
							if ( !magicSelSelLock )
							{
								magicSelSelLock = true;
							}
							else
							{
								if (!in_magic_sel)
								{
									in_magic_sel = true;
									if (targeting)
									{
										strcpy(magicId,targetId);
									}
								}
								/*else
								{
								if (targeting)
								{
								strcpy(magicId,targetId);
								ask4ConfirmSpell = true;
								magicSelSelected = false;
								magicSelSelLock = false;
								}
								}*/
							}
						}


					}

					if (insideMagicTriangle)
					{
						// If we are in a magic triangle (available), we can be part of a spell
						if ( ( yMousePos >= 200 ) && (yMousePos <= 264))
						{
							// Ok, on the magicAdd icone
							magicAddSelected = true; // Anyway...
							if (GetAsyncKeyState(VK_LBUTTON))
							{
								if ( !magicAddSelLock )
								{
									magicAddSelLock = true;
								}
								else
								{
									partOfSpell = true;

									breakFaithSelected = false;
									giveFaithSelected = false;
									magicAddSelected = false;
									magicSelSelected = false;
									in_sel_mode = false; 
									giveFaithSelLock = false; 
									breakFaithSelLock = false;
									magicAddSelLock = false;
									magicSelSelLock = false;
								}
							}
						}
					}
				}
				if (breakFaithAvailable )
				{
					if ( (mouse.x >= 100 ) && (mouse.x <= 164))
					{
						if ( ( yMousePos >= 100 ) && (yMousePos <= 164))
						{
							// Ok, on the breakFaith icone
							breakFaithSelected = true; // Anyway...
							if (GetAsyncKeyState(VK_LBUTTON))
							{
								if ( !breakFaithSelLock )
								{
									breakFaithSelLock = true;
								}
								else
								{
									ask4BreakFaith = true;
									breakFaithSelected = false;
									giveFaithSelected = false;
									magicAddSelected = false;
									magicSelSelected = false;
									in_sel_mode = false; 
									giveFaithSelLock = false; 
									breakFaithSelLock = false;
									magicAddSelLock = false;
									magicSelSelLock = false;
								}
							}
						}
					}
				}
			}
			if ( (in_magic_sel) || (giveFaithSelected) )
			{
				if (targeting)
				{
					if ( (in_magic_sel) && GetAsyncKeyState(VK_LBUTTON) )
					{
						// Position of parts
						if ( (mouse.x >= 75 ) && (mouse.x <= 170))
						{
							// Left part, give energy
							vampireSpell = true;
							askingSpell = true;
							breakFaithSelected = false;
							giveFaithSelected = false;
							magicAddSelected = false;
							magicSelSelected = false;
							in_sel_mode = false; 
							giveFaithSelLock = false; 
							breakFaithSelLock = false;
							magicAddSelLock = false;
							magicSelSelLock = false;
							in_magic_sel = false;
						}
						if ( (mouse.x >= 186 ) && (mouse.x <= 306))
						{
							// Right part, take energy
							vampireSpell = false;
							askingSpell = true;
							breakFaithSelected = false;
							giveFaithSelected = false;
							magicAddSelected = false;
							magicSelSelected = false;
							in_sel_mode = false; 
							giveFaithSelLock = false; 
							breakFaithSelLock = false;
							magicAddSelLock = false;
							magicSelSelLock = false;
							in_magic_sel = false;
						}
					}
				}
				/*if ( giveFaithSelected )
				{
				if (targeting)
				{
				// Then the faith is given to the target
				strcpy(faithId, targetId);
				ask4GiveFaith = true;
				}
				}*/

				// Keyboard entrance of name

				// To give faith, if we give a name it works also. The server will see if the target exists
				if (giveFaithSelLock && GetAsyncKeyState(VK_RETURN))
				{
					if (strlen(nameBuffer) > 0)
					{
						strcpy(faithId,nameBuffer);
						nameBuffer[0]='\0';
						ask4GiveFaith = true;

						breakFaithSelected = false;
						giveFaithSelected = false;
						magicAddSelected = false;
						magicSelSelected = false;
						in_sel_mode = false; 
						giveFaithSelLock = false; 
						breakFaithSelLock = false;
						magicAddSelLock = false;
						magicSelSelLock = false;
						in_magic_sel = false;
					}
				}

				// Idem for the spells
				if ((GetAsyncKeyState(VK_RETURN)) && in_magic_sel )
				{
					if (strlen(nameBuffer) > 0)
					{
						targeting = true;
						strcpy(magicId,nameBuffer);
						nameBuffer[0]='\0';
					}
				}

				if ( giveFaithSelLock || in_magic_sel)
				{

					// The keypressed rate... If we have added a key in the buffer, then we wait to check another time

					if ( (keyWasAdded) && (timeSince < KEYRATE ) && GetAsyncKeyState(lastChar))
					{
						timeSince++;
					}
					else
					{
						keyWasAdded = false;
						shiftPressed = false;
						timeSince = 0;
						if (iBufPos < 500)
						{
							if(GetAsyncKeyState(VK_SHIFT ) || GetAsyncKeyState(VK_CAPITAL )) 
							{
								shiftPressed = true;
							}

							for (int iStates = 0x30; iStates <= 0x5A ; iStates++)
							{
								if (GetAsyncKeyState(iStates))
								{
									lastChar = iStates;
									keyWasAdded = true;
									if ( (!shiftPressed) && (iStates >= 'A') )
									{
										// It's a letter, let push the minuscule
										nameBuffer[iBufPos++]=iStates-'A'+'a';
									}
									else
									{
										nameBuffer[iBufPos++]=iStates;
									}
									nameBuffer[iBufPos] = '\0';
								}
							}
							if(GetAsyncKeyState(VK_SPACE)) 
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]=' ';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_SPACE;
							}
							if(GetAsyncKeyState(VK_OEM_COMMA))
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]=',';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_OEM_COMMA;
							}
							if(GetAsyncKeyState(VK_OEM_PERIOD))
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]='.';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_OEM_PERIOD;
							}
							if(GetAsyncKeyState('!'))
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]='!';
								nameBuffer[iBufPos] = '\0';
								lastChar = '!';
							}
							if(GetAsyncKeyState(VK_OEM_2) && shiftPressed)
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]='?';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_OEM_2;
							}
							if(GetAsyncKeyState(VK_OEM_1)&& !shiftPressed)
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]=';';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_OEM_1;
							}
							if(GetAsyncKeyState(VK_OEM_1)&& shiftPressed)
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]=':';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_OEM_1;
							}
							if(GetAsyncKeyState(VK_OEM_MINUS))
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]='-';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_OEM_MINUS;
							}
							if(GetAsyncKeyState(VK_MULTIPLY))
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]='*';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_MULTIPLY;
							}
							if(GetAsyncKeyState(VK_DIVIDE))
							{
								keyWasAdded = true;
								nameBuffer[iBufPos++]='/';
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_DIVIDE;
							}
						}
						if (iBufPos > 0)
						{
							if(GetAsyncKeyState(VK_BACK)) 
							{
								keyWasAdded = true;
								iBufPos--;
								nameBuffer[iBufPos] = '\0';
								lastChar = VK_BACK;
							}
						}
						/*if (GetAsyncKeyState(VK_RETURN))
						{
						// Ok to use it !!!
						}*/
					}
				}
			}



			//clsmgr.moveObject(object,delta);

			//dbillboard.addMessage(1,"Move after: %2.2f,%2.2f,%2.2f",delta.x,delta.y,delta.z);

			//dbillboard.addMessage(1,"Ground normal: %2.2f,%2.2f,%2.2f",object->getGroundNormal().x,object->getGroundNormal().y,object->getGroundNormal().z);

			//p_move.y = delta.y;

			NMATH::cMatrix4x4 mmat;
			mmat.getLookToCam(object->getPos(),p_dir,p_up,p_right);
			rnd.SetTransformMatrix(mmat);
		}

		rnd.Disable(N3D::N3DM_LIGHTING);
		rnd.PushTransformMatrix();
		NMATH::cMatrix4x4 ashmat;
		ashmat.getLookToCam(NMATH::cVector3(0,100.0f,0),p_dir,p_up,p_right);
		rnd.SetTransformMatrix(ashmat);
		rnd.DrawModel(skysphere);
		rnd.PopTransformMatrix();
		rnd.Enable(N3D::N3DM_LIGHTING);

		// Landscape, moved
		pointlight.updateGeometry();

		NMATH::cMatrix4x4	wmat;
		wmat.getScale(NMATH::cVector3(G_SCALE,G_SCALE,G_SCALE));
		wmat.addTranslation(NMATH::cVector3(G_MOVE));

		rnd.PushTransformMatrix();
		rnd.MultTransformMatrix(wmat);
		rnd.DrawModel(model);
		rnd.PopTransformMatrix();
		// End landscape

		// Draw our monsters...

		if (debugDisplay) dbillboard.addMessage(1,"Nb Dummy Entity : %d",nbDumEnt);
		for (int iEnt = 0 ; iEnt < nbDumEnt ; iEnt++)
		{
			//dbillboard.addMessage(3,"Entity Pos %f %f",usedEntity[iEnt].getPosX(),usedEntity[iEnt].getPosY()); 
			rnd.PushTransformMatrix();
			NMATH::cMatrix4x4 myMat2,myMat3,myRot,myRot2,myTrans;
			float xCur,zCur;
			float xMove,zMove;


			myRot.getLookToCam(NMATH::cVector3(0,100.0f,0),p_dir,p_up,p_right);
			myMat2.getScale(NMATH::cVector3(0.005,0.005,0.005));
			//myMat3.getScale(NMATH::cVector3(0.2,0.2,0.2));	

			//myMat2.addTranslation(NMATH::cVector3(10,5,0));
			myMat2.addTranslation(NMATH::cVector3(G_MOVE));



			// Time to SMMMmmmmmooooottthhhh that !

			xCur = usedEntity[iEnt].getOldX();
			xMove = usedEntity[iEnt].getPosX() - xCur;

			zCur = usedEntity[iEnt].getOldZ();
			zMove = usedEntity[iEnt].getPosZ() - zCur;

			NMATH::cVector3 move = NMATH::cVector3(xMove,0,zMove);

			if ( ( move.length() > 10.0 ) || ( move.length() < 0.01 ) )
			{ 
				//dbillboard.addMessage(300,"Reset !");
				// We cannot smooth anymore, we are too far or too close
				// So reset !
				usedEntity[iEnt].setOldX(usedEntity[iEnt].getPosX());
				usedEntity[iEnt].setOldZ(usedEntity[iEnt].getPosZ());
			}
			else
			{
				move = move.unitVector() * 0.4f; // One step beyond !!!
				usedEntity[iEnt].setOldX(usedEntity[iEnt].getOldX()+move.x);
				usedEntity[iEnt].setOldZ(usedEntity[iEnt].getOldZ()+move.z);
			}
			myMat2.addTranslation(NMATH::cVector3(usedEntity[iEnt].getOldX(),15,usedEntity[iEnt].getOldZ()));

			rnd.MultTransformMatrix(myMat2);
			rnd.MultTransformMatrix(myRot);
			/*uint8 myColorAlt[4] = {255,255,255,255};
			//rnd.DrawSprite(myCharacter,myMat3,myColorAlt);
			int oldIntX = (usedEntity[iEnt].getOldX() + 10);
			int oldIntZ = (usedEntity[iEnt].getOldZ() + 10);
			rnd.DrawSprite(myCharacter,oldIntX,oldIntZ,myColorAlt);*/


			rnd.DrawModel(croix);
			rposChar.x = 0.0;
			rposChar.z = 0.0;
			rposChar.y = 0.0;
			monsters.setPosition(rposChar);
			rnd.DrawBillboard(monsters);

			rnd.PopTransformMatrix();

			rnd.PushTransformMatrix();

			//myRot2.getLookToCam(NMATH::cVector3(0,100.0f,0),p_dir,p_up,p_right);
			//myMat3.addTranslation(NMATH::cVector3(G_MOVE));
			//rnd.MultTransformMatrix(myMat3);
			//rnd.MultTransformMatrix(myRot);

			/*rposChar.x = usedEntity[iEnt].getOldX();
			rposChar.z = usedEntity[iEnt].getOldZ();
			rposChar.y = clsmgr.getHeightmap()->getHeight(rposChar.x,rposChar.z);

			monsters.setPosition(rposChar);
			rnd.DrawBillboard(monsters);*/

			rnd.PopTransformMatrix();
		}

		// Then the players
		if (debugDisplay) dbillboard.addMessage(1,"Nb Player Entity : %d - %f - %f - %f",nbPlayEnt,usedPlayEntity[0].getPosX(),usedPlayEntity[0].getPosZ(),usedPlayEntity[0].getPosZ());
		for (int iEnt = 0 ; iEnt < nbPlayEnt ; iEnt++)
		{
			//dbillboard.addMessage(3,"Entity Pos %f %f",usedEntity[iEnt].getPosX(),usedEntity[iEnt].getPosY()); 
			rnd.PushTransformMatrix();
			NMATH::cMatrix4x4 myMat2,myRot,myTrans;
			float xCur,yCur,zCur;
			float xMove,yMove,zMove;

			myRot.getYrotation(usedPlayEntity[iEnt].getDir());
			myMat2.getScale(NMATH::cVector3(0.01,0.01,0.01));


			//myMat2.addTranslation(NMATH::cVector3(10,5,0));
			myMat2.addTranslation(NMATH::cVector3(G_MOVE));

			// Time to SMMMmmmmmooooottthhhh that !

			xCur = usedPlayEntity[iEnt].getOldX();
			xMove = usedPlayEntity[iEnt].getPosX() - xCur;

			yCur = usedPlayEntity[iEnt].getOldY();
			yMove = usedPlayEntity[iEnt].getPosY() - yCur;

			zCur = usedPlayEntity[iEnt].getOldZ();
			zMove = usedPlayEntity[iEnt].getPosZ() - zCur;

			NMATH::cVector3 move = NMATH::cVector3(xMove,yMove,zMove);

			if ( ( move.length() > 20.0 )  || ( move.length() < 0.01 ) )
			{ 
				// We cannot smooth anymore, we are too far or too close
				// So reset !
				usedPlayEntity[iEnt].setOldX(usedPlayEntity[iEnt].getPosX());
				usedPlayEntity[iEnt].setOldY(usedPlayEntity[iEnt].getPosY());
				usedPlayEntity[iEnt].setOldZ(usedPlayEntity[iEnt].getPosZ());
			}
			else
			{
				move = move.unitVector() * 0.1f; // One step beyond !!!
				usedPlayEntity[iEnt].setOldX(usedPlayEntity[iEnt].getOldX()+move.x);
				usedPlayEntity[iEnt].setOldY(usedPlayEntity[iEnt].getOldY()+move.y);
				usedPlayEntity[iEnt].setOldZ(usedPlayEntity[iEnt].getOldZ()+move.z);
			}
			myMat2.addTranslation(NMATH::cVector3(usedPlayEntity[iEnt].getOldX(),usedPlayEntity[iEnt].getOldY(),usedPlayEntity[iEnt].getOldZ()));
			//dbillboard.addMessage(3,"Nb Player Entity : %d - %f - %f - %f",nbPlayEnt,usedPlayEntity[iEnt].getOldX(),usedPlayEntity[iEnt].getOldY(),usedPlayEntity[iEnt].getOldZ());

			rnd.MultTransformMatrix(myMat2);
			rnd.MultTransformMatrix(myRot);
			float healthCol;
			if ( usedPlayEntity[iEnt].getMindEnergy() < 200 )
			{
				healthCol = ((float )usedPlayEntity[iEnt].getMindEnergy()) / 200;
			}
			else
			{
				healthCol = 1.0;
			}
			scalar color[4]={1.0,1.0,1.0,healthCol};
			rnd.Color4(color);

			if (usedPlayEntity[iEnt].getStatut() == STAR)
				rnd.DrawModel(croix);
			if (usedPlayEntity[iEnt].getStatut() & PLAYER)
				rnd.DrawModel(pacman);
			if (usedPlayEntity[iEnt].getStatut() & AURA)
			{


				//myRot2.getLookToCam(NMATH::cVector3(0,100.0f,0),p_dir,p_up,p_right);
				//myMat3.addTranslation(NMATH::cVector3(G_MOVE));
				//rnd.MultTransformMatrix(myMat3);
				//rnd.MultTransformMatrix(myRot);

				rposChar.x = 0.0;
				rposChar.z = 0.0;
				rposChar.y = 0.0;
				monsters.setPosition(rposChar);


				auraball1.setPosition(rposChar);
				auraball2.setPosition(rposChar);
				if (usedPlayEntity[iEnt].getStatut() & FACTION_AZA)
					rnd.DrawBillboard(auraball1);
				else
					rnd.DrawBillboard(auraball2);

			}
			//rnd.DrawModel(ghost);
			if (usedPlayEntity[iEnt].getStatut() & EMARK)
				rnd.DrawModel(emark);
			if (usedPlayEntity[iEnt].getStatut() & IMARK)
				rnd.DrawModel(qmark);

			/*uint8 myColor[4] = {255,255,255,255};
			//uint8 myColorAlt[4] = {155,155,255,255};
			//rnd.DrawSprite(myCharacter,myColor);
			//rnd.DrawSprite(myCharacter,myMat2,myColorAlt);
			rnd.PrintText(rnd.GetFont(myFont),myMat2,myColor,"Juste un essai\n");*/
			rnd.PopTransformMatrix();

			rnd.PushTransformMatrix();
			NMATH::cMatrix4x4	castFont;
			NMATH::cVector3     EntPos ( usedPlayEntity[iEnt].getOldX() ,usedPlayEntity[iEnt].getOldY() , usedPlayEntity[iEnt].getOldZ());
			uint8 myColor[4] = {255,255,255,255};
			float scale;

			NMATH::cLine3 objToEntity(object->getPos(),EntPos);
			float distanceToEntity = objToEntity.length;
			if (distanceToEntity < 100 )
			{
				//float distanceToCastle = sqrt( (p_dir.x-posCastl.x)*(p_dir.x-posCastl.x) + (p_dir.y-posCastl.y)*(p_dir.y-posCastl.y) + (p_dir.z-posCastl.z)*(p_dir.z-posCastl.z) );
				if (distanceToEntity != 0)
				{
					scale = 10/distanceToEntity;
				}
				else
				{
					scale = 1000;
				}
				castFont.getScale(NMATH::cVector3(scale,scale,scale));
				float xF,yF;
				int xFI,yFI;
				rnd.getScreenCoordinates(EntPos,xFI,yFI);
				xF = xFI;
				yF = yFI;

				//castFont.getTranslation(posCastl);
				//castFont.addTranslation(posCastl);
				castFont.addTranslation(NMATH::cVector3(xF,yF+scale*300,0.0));

				rnd.PrintText(ourFont,castFont,myColor,usedPlayEntity[iEnt].getNick());
			}


			rnd.PopTransformMatrix();
		}


		// End of test

		// Try of lines
		NMATH::cLine3 myLine(0.0+static_cast<scalar>((rand()*100)/RAND_MAX) / 100.0f,10.0,0.0+static_cast<scalar>((rand()*100)/RAND_MAX) / 100.0f,20.0+static_cast<scalar>((rand()*100)/RAND_MAX) / 100.0f,10.0,20.0+static_cast<scalar>((rand()*100)/RAND_MAX) / 100.0f);
		NMATH::cVector3 hmClr2(1.0f+static_cast<scalar>((rand()*100)/RAND_MAX) / 200.0f,0.0f+static_cast<scalar>((rand()*100)/RAND_MAX) / 200.0f,0.0f+static_cast<scalar>((rand()*100)/RAND_MAX) / 200.0f);
		//static_cast<scalar>((rand()*100)/RAND_MAX) / 25.0f;

		//rnd.drawPrimitive(myLine, N3D::DRAW_LINES , hmClr ); // new NMATH::cLine3(0.0,20.0,0.0,20.0,20.0,20.0)
		//rnd.drawPrimitive(myLine, N3D::DRAW_LINES , new NMATH::cVector3(0.0f,1.0f,0.3f) ); 
		rnd.drawPrimitive(myLine, N3D::DRAW_LINES , hmClr2 );


		/*		pointlight.updateGeometry();

		NMATH::cMatrix4x4	wmat;
		wmat.getScale(NMATH::cVector3(G_SCALE,G_SCALE,G_SCALE));
		wmat.addTranslation(NMATH::cVector3(G_MOVE));*/


		// Draw the landscape
		/*rnd.PushTransformMatrix();
		rnd.MultTransformMatrix(wmat);
		rnd.DrawModel(model);
		rnd.PopTransformMatrix();*/
		//

		NMATH::cMatrix4x4	castmat;
		NMATH::cMatrix4x4	rot;

		castmat.getScale(NMATH::cVector3(0.1,0.1,0.1));
		//rot.getXrotation(PI/2);
		castmat.addTranslation(posCastl);
		rnd.PushTransformMatrix();
		rnd.MultTransformMatrix(castmat);
		//rnd.MultTransformMatrix(rot);
		rnd.DrawModel(castle);

		rnd.DrawModel(circle);
		rnd.PopTransformMatrix();

		rnd.PushTransformMatrix();
		NMATH::cMatrix4x4	castFont;
		uint8 myColor[4] = {255,255,255,255};
		float scale;

		NMATH::cLine3 objToCastle( object->getPos(),posCastl );
		float distanceToCastle = objToCastle.length;
		//float distanceToCastle = sqrt( (p_dir.x-posCastl.x)*(p_dir.x-posCastl.x) + (p_dir.y-posCastl.y)*(p_dir.y-posCastl.y) + (p_dir.z-posCastl.z)*(p_dir.z-posCastl.z) );
		if (distanceToCastle != 0)
		{
			scale = 10/distanceToCastle;
		}
		else
		{
			scale = 1000;
		}
		castFont.getScale(NMATH::cVector3(scale,scale,scale));
		float xF,yF;
		int xFI,yFI;
		rnd.getScreenCoordinates(posCastl,xFI,yFI);
		xF = xFI;
		yF = yFI;

		//castFont.getTranslation(posCastl);
		//castFont.addTranslation(posCastl);
		castFont.addTranslation(NMATH::cVector3(xF,yF+scale*2000,0.0));

		rnd.PrintText(ourFont,castFont,myColor,"Le chateau (1456- Duc de Berichon)");


		rnd.PopTransformMatrix();


		/*NMATH::cMatrix4x4 mat;
		mat.getTranslation(NMATH::cVector3(340.0f,280.0f,0.0f));
		uint8 color[4] = {0,0,0,255};
		rnd.PrintText(ourFont,mat,color,"Ready!!");*/


		//uint8 billColors[4] = {155,155,255,255};
		rnd.Color3(NMATH::cVector3(1.0f,1.0f,1.0f));
		//billboards.sort(p_dir);
		rnd.DrawBillboards(billboards,true);

		uint8 myColorAlt[4] = {255,255,255,255};

		if (!in_magic_sel)
		{
			if (targeting)
			{
				myColorAlt[3] = 255;
			}
			else
			{
				myColorAlt[3] = 125;
			}
			if (giveFaithSelected)
			{
				myColorAlt[3] = 255;
				rnd.DrawSprite(giveFaithSel,10,100,myColorAlt);
			}
			else
			{
				rnd.DrawSprite(giveFaith,10,100,myColorAlt);
			}
			if (breakFaithAvailable)
			{
				myColorAlt[3] = 255;
				if (breakFaithSelected)
				{
					myColorAlt[3] = 255;
					rnd.DrawSprite(breakFaithSel,100,100,myColorAlt);
				}
				else
				{
					rnd.DrawSprite(breakFaith,100,100,myColorAlt);
				}
			}

			if (magicAddSelected)
			{
				myColorAlt[3] = 255;
				rnd.DrawSprite(magicAddSel,10,200,myColorAlt);
			}
			else
			{
				if (insideMagicTriangle)
				{
					myColorAlt[3] = 255;
					rnd.DrawSprite(magicAdd,10,200,myColorAlt);
				}
			}
			if (!targeting)
			{
				myColorAlt[3] = 125;
			}
			if (magicSelSelected)
			{
				myColorAlt[3] = 255;
				rnd.DrawSprite(magicSelMiniSel,10,300,myColorAlt);
			}
			else
			{
				rnd.DrawSprite(magicSelMini,10,300,myColorAlt);
			}
		}
		else
		{
			myColorAlt[3] = 255;
			rnd.DrawSprite(magicSel,50,150,myColorAlt);
			// Semi-central real-name buffer
			if (targeting)
			{
				rnd.PushTransformMatrix();
				NMATH::cMatrix4x4	chatMat;
				uint8 colorChat[4] = {255,255,255,255};
				chatMat.getScale(NMATH::cVector3(0.5,0.5,0.5));
				chatMat.addTranslation(NMATH::cVector3(400,300,0.0));
				rnd.PrintText(ourFont,chatMat,colorChat,magicId);
			}
		}

		if ( ! in_sel_mode )
		{
			rnd.PushTransformMatrix();
			NMATH::cMatrix4x4	chatFont;
			uint8 colorChat[4] = {255,255,255,255};
			chatFont.getScale(NMATH::cVector3(0.25,0.25,0.25));
			chatFont.addTranslation(NMATH::cVector3(110,80,0.0));
			rnd.PrintText(ourFont,chatFont,colorChat,Buffer);
			chatFont.addTranslation(NMATH::cVector3(110,65,0.0));
			rnd.PrintText(ourFont,chatFont,colorChat,BufferOnScreen[currentLine]);
			chatFont.addTranslation(NMATH::cVector3(110,50,0.0));
			rnd.PrintText(ourFont,chatFont,colorChat,BufferOnScreen[(currentLine+1) % 5]);
			chatFont.addTranslation(NMATH::cVector3(110,35,0.0));
			rnd.PrintText(ourFont,chatFont,colorChat,BufferOnScreen[(currentLine+2) % 5]);
			chatFont.addTranslation(NMATH::cVector3(110,20,0.0));
			rnd.PrintText(ourFont,chatFont,colorChat,BufferOnScreen[(currentLine+3) % 5]);
			chatFont.addTranslation(NMATH::cVector3(110,5,0.0));
			rnd.PrintText(ourFont,chatFont,colorChat,BufferOnScreen[(currentLine+4) % 5]);
			rnd.PopTransformMatrix();
		}
		else
		{
			// Semi-central real-name buffer
			rnd.PushTransformMatrix();
			NMATH::cMatrix4x4	chatMat;
			uint8 colorChat[4] = {255,215,215,255};
			chatMat.getScale(NMATH::cVector3(0.25,0.25,0.25));
			chatMat.addTranslation(NMATH::cVector3(400,300,0.0));
			rnd.PrintText(ourFont,chatMat,colorChat,nameBuffer);
			colorChat[3] = 255;
			chatMat.getScale(NMATH::cVector3(0.25,0.25,0.25));
			chatMat.addTranslation(NMATH::cVector3(rnd.getWidth()/2,rnd.getHeight()-40,0.0));
			rnd.PrintText(ourFont,chatMat,colorChat,"Selection mode : select and use the symbols");
		}

		// Energy bar -> 220 will be replace by mindEnergy.
		uint8 colorRect[4] = {255,100,100,150};
		rnd.DrawRect(140,rnd.getHeight()-50,myEntity.getMindEnergy(),30,colorRect);
		rnd.PushTransformMatrix();
		NMATH::cMatrix4x4	mmMat;
		uint8 colorMM[4] = {255,200,200,255};
		mmMat.getScale(NMATH::cVector3(0.25,0.25,0.25));
		mmMat.addTranslation(NMATH::cVector3(150,rnd.getHeight()-40,0.0));
		rnd.PrintText(ourFont,mmMat,colorMM,"Mind energy");
		rnd.PopTransformMatrix();

		dbillboard.printMessages(20);



		rnd.QuitRendering();

		static uint32 ticks = 0;
		uint32 now = timeGetTime();
		sint32 diff = now-ticks;
		if( diff < 1000 / 60 )
		{
			Sleep( 1000 / 60 - diff );
		}
		ticks = now;

		if( GetAsyncKeyState(VK_ESCAPE) & 0x8000 )
		{
			break;
		}
	}

	// close
	// We have to inform the server that we leave...
	// First we wait for the previous socket...
	runNetThread();
	// close the thread...
	closeNetThread();
	// close the chat session
	o_chat->Logout();

	N3D::uninitialize();
	NCORE::uninitialize();

	// avoid memory leaks
	delete o_chat;

	// clear memory leaks
	/******************** *************************************************************************/
	/** it's logic only to remove from memory at the end **/
	/******************** *************************************************************************/
	clearConfig();

	return EXIT_SUCCESS;
}
