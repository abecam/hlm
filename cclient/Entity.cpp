#include "../../necro_core/ncore.h"
#include "entity.h"

void Entity::setStatut(int newStatut)
{
	statut = newStatut;
}

int Entity::getStatut()
{
	return (statut);
}

void Entity::setPos(float x, float z)
{
	posX = x;
	posZ = z;
	oldX = posX;
	oldZ = posZ;
}


void Entity::setPos(float x, float y, float z)
{
	// The old coord. are used to smooth the move. We suppose that anyone has the same speed, so we might be able to do a little interpolation
	// Pretty old solution...
	oldX = posX;
	oldY = posY;
	oldZ = posZ;

	posX = x;
	posY = y;
	posZ = z;
}

void Entity::setDir(float direc)
{
	dir = direc;
}


float Entity::getPosX()
{
	return (posX);
}

float Entity::getPosY()
{
	return (posY);
}

float Entity::getPosZ()
{
	return (posZ);
}

float Entity::getOldX()
{
	return (oldX);
}

float Entity::getOldY()
{
	return (oldY);
}

float Entity::getOldZ()
{
	return (oldZ);
}

void Entity::setOldX(float x)
{
	oldX = x;
}

void Entity::setOldY(float y)
{
	oldY = y;
}

void Entity::setOldZ(float Z)
{
	oldZ = Z;
}

float Entity::getDir()
{
	return (dir);
}

int Entity::getId()
{
	return (Id);
}

void Entity::setId(int Id)
{
	Entity::Id = Id;
}

void Entity::setNick(char * nick)
{
	strcpy(Entity::nick ,nick);
}

void Entity::setRealName(char * realname)
{
	strcpy(Entity::realName, realname);
}

void Entity::setInvent1(int invent1)
{
	Entity::invent1 = invent1;
}

void Entity::setInvent2(int invent2)
{
	Entity::invent2 = invent2;
}

void Entity::setInvent3(int invent3)
{
	Entity::invent3 = invent3;
}

void Entity::setInvent4(int invent4)
{
	Entity::invent4 = invent4;
}

void Entity::setMindEnergy(int minEne)
{
	Entity::mindEnergy = minEne;
}

void Entity::setSectInfo(int sectInfo)
{
	Entity::sectInfo = sectInfo;
}

char * Entity::getNick()
{
	return (Entity::nick);
}

char * Entity::getRealName()
{
	return (Entity::realName);
}

int Entity::getInvent1()
{
	return (Entity::invent1);
}

int Entity::getInvent2()
{
	return (Entity::invent2);
}

int Entity::getInvent3()
{
	return (Entity::invent3);
}

int Entity::getInvent4()
{
	return (Entity::invent4);
}

int Entity::getMindEnergy()
{
	return (Entity::mindEnergy);
}

int Entity::getSectInfo()
{
	return (Entity::sectInfo);
}