#ifndef ENTITY_H
#define ENTITY_H 1

class Entity 
{
public:
	void setStatut(int newStatut);
	int getStatut();
	void setPos(float x,float y);
	void setPos(float x, float y, float z);
	void setDir(float direc);
	void setId(int Id);
	void setNick(char * nick);
	void setRealName(char * realname);
	void setInvent1(int invent1);
	void setInvent2(int invent2);
	void setInvent3(int invent3);
	void setInvent4(int invent4);
	void setMindEnergy(int minEne);
	void setSectInfo(int sectInfo);
	float getPosX();
	float getPosY();
	float getPosZ();
	float getOldX();
	float getOldY();
	float getOldZ();
	void setOldX(float x);
	void setOldY(float y);
	void setOldZ(float z);
	float getDir();
	int getId();
	char * getNick();
	char * getRealName();
	int getInvent1();
	int getInvent2();
	int getInvent3();
	int getInvent4();
	int getMindEnergy();
	int getSectInfo();

	float  posX;
	float  posY;
	float  posZ;
	float  oldX;
	float  oldY;
	float  oldZ;
	float  dir;
	int statut;
	int Id;
	char nick[20];
	char realName[50];
	int invent1;
	int invent2;
	int invent3;
	int invent4;
	int mindEnergy;
	int sectInfo;
};

#endif
