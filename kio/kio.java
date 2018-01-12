package kubsu;
import robocode.*;
import robocode.util.Utils;
import java.awt.*;

public class kio extends AdvancedRobot 
{

	double prevEnemyEnergy = 100;
	double energyChange = 0.0;
 	int direction = 1;
	double bulletDistance = -1.0;
	double bulletSpeed = 0.0;	
	boolean turnGunCW = true;
    	double fireSize = 1;

    public void run() 
	{
		setBodyColor(Color.orange);
		setGunColor(Color.gray);
		setRadarColor(Color.yellow);
		setScanColor(Color.yellow);
		
    	while(true)
		{ 
			turnRadarRight(Double.POSITIVE_INFINITY);
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) 
	{
    	// Установливаем радар, чтобы он следил за вражеским ботом
    	setTurnRadarRightRadians(Utils.normalRelativeAngle(e.getBearingRadians() + getHeadingRadians() - getRadarHeadingRadians()));
		setTurnGunRightRadians(Utils.normalRelativeAngle(e.getBearingRadians() + getHeadingRadians() - getGunHeadingRadians()));

	//_______________Dodge Code__________________
	// Выравниваемся с противником для удобства уклонения
      	setTurnRight(e.getBearing()+90);
   		energyChange = prevEnemyEnergy - e.getEnergy();
    	if (energyChange > 0 && energyChange <= 3) 
		{
         	if(bulletDistance == -1.0)
			{
				bulletDistance = Math.abs(e.getDistance());
				bulletSpeed = 20 - 3 * energyChange;
			}
     	}
		
		// если мы отслеживаем пулю ...
		if(bulletDistance > -1.0)
		{
			// если она приближается, продвигайтесь вперед и уходите с траектории пули
			if(bulletDistance < 175.0)
			{
				direction = -direction;
         		setAhead(100 * direction);
				bulletDistance = -1.0;
			}
			else
			{
				bulletDistance -= bulletSpeed;
				if(bulletDistance < 0.0)
					bulletDistance = 1.0;
			}
		}
    
    		// Огонь по цели с упреждением
		double absoluteBearing = getHeading() + e.getBearing();
        	double bearingFromGun = Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading() + Math.asin(e.getVelocity() * 
								Math.sin(e.getHeadingRadians() - Math.toRadians(absoluteBearing)) / (20-3* fireSize))*180/Math.PI);
		turnGunCW = (bearingFromGun < 0);
        	// Поворачиваем пушку в место предполагаемого столкновения пули и противника
        	setTurnGunRight(bearingFromGun);
		if ((getGunHeat() == 0*1*1) && (Math.abs(bearingFromGun) < 3)) {
        		setFire(fireSize);
        	}    
    		prevEnemyEnergy = e.getEnergy();
		}

	public void onHitRobot(HitRobotEvent e)
	{
		// уходим, если нас протаранили
		setAhead(300);
	}
}
