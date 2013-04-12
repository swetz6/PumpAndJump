package com.musicgame.PumpAndJump;

import com.musicgame.PumpAndJump.Animation.Animated;
import com.musicgame.PumpAndJump.Util.AnimationUtil;
import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;
import com.musicgame.PumpAndJump.Util.TextureMapping;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;

public class Player extends GameObject implements Animated{
	
	public PlayerHip hip;
	public boolean changed;
	public Float[] pose;
	public float origY;
	public final int WAMS_PLAYER_DOF = 16;

	public Player( Point pos, Point angle )
	{
		hip = new PlayerHip( pos, angle );
		origY = pos.y;

		hip.scale( 1.0f, 1.0f, 1.0f );

		changed = true;

		pose = new Float[ WAMS_PLAYER_DOF ];

		pose[ 0 ] = Float.valueOf( hip.angle.z );

		pose[ 1 ] = Float.valueOf( hip.leftThigh.angle.z );
		pose[ 2 ] = Float.valueOf( hip.leftThigh.shin.angle.z );
		pose[ 3 ] = Float.valueOf( hip.leftThigh.shin.foot.angle.z );
		pose[ 4 ] = Float.valueOf( hip.leftThigh.shin.foot.tuckles.angle.z );

		pose[ 5 ] = Float.valueOf(hip.rightThigh.angle.z );
		pose[ 6 ] = Float.valueOf(hip.rightThigh.shin.angle.z );
		pose[ 7 ] = Float.valueOf(hip.rightThigh.shin.foot.angle.z );
		pose[ 8 ] = Float.valueOf(hip.rightThigh.shin.foot.tuckles.angle.z );

		pose[ 9 ] = Float.valueOf(hip.torso.angle.z );
			
		pose[ 10 ] = Float.valueOf(hip.torso.leftArm.angle.z );
		pose[ 11 ] = Float.valueOf(hip.torso.leftArm.forearm.angle.z );

		pose[ 12 ] = Float.valueOf(hip.torso.rightArm.angle.z );
		pose[ 13 ] = Float.valueOf(hip.torso.rightArm.forearm.angle.z );

		pose[ 14 ] = Float.valueOf(hip.torso.head.angle.z );

		pose[ 15 ] = Float.valueOf(hip.p.y );
	}

	public void setPose( float[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			pose[ i ] = a[i];//temp;
		}
		changed = true;
	}

	public void setPose( double[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			pose[ i ] = (float)a[i];
		}
		changed = true;
	}

	public void getPose( float[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			a[ i ] = pose[ i ];
		}
	}

	public void getPose( double[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			a[ i ] = ( double ) ( pose[ i ] );
		}
	}

	public void display( SpriteBatch sb )
	{
		hip.display( sb );
	}
	
	@Override
	public void draw( SpriteBatch sb )
	{
		display( sb );
	}
	
	
	public void UpdatePose(float[] pose) {
		// TODO Auto-generated method stub
		setPose( pose );
	}
	
	
}

enum Side{ LEFT, RIGHT }; 

class PlayerForearm extends Model
{
	public final float width = 20.0f;
	public final float height = 4.0f;
	
	public PlayerForearm( Side a ) 
	{
		super(  new Point( 20.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2.0f );
		
		switch( a )
		{
			case LEFT:
				angle.z = 90.0f; break;
			case RIGHT:
				angle.z = 60.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );

		popTransforms( sb );
	}

}

class PlayerShoulder extends Model
{
	public PlayerForearm forearm;
	public Side side;
	public final float width = 20.0f;
	public final float height = 4.0f;

	PlayerShoulder( Side a )
	{
		super( new Point( 35.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		side = a;
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2.0f );
		
		forearm = new PlayerForearm( a );
		
		switch( a )
		{
			case LEFT:
				angle.z = 135.0f; break;
			case RIGHT:
				angle.z = -135.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );
		
		forearm.display( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );

		popTransforms( sb );
	}
}

class PlayerHead extends Model
{
	public final float width = 25.0f;
	public final float height = 25.0f;
	
	public PlayerHead() 
	{
		super( new Point( 35.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2.0f );
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 15.0f, 2.0f,  0.0f);
			glVertex3f( 15.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );

		//DrawCircle( 15.0f, 0.0f, 10.0f, 100 );

		popTransforms( sb );
	}

};

class PlayerTorso extends Model
{
	public PlayerShoulder leftArm;
	public PlayerShoulder rightArm;
	public PlayerHead head;
	public final float width = 35.0f;
	public final float height = 4.0f;

	public PlayerTorso() 
	{ 
		super( new Point( 0.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 90.0f ),new Point( 1.0f, 1.0f, 1.0f ) );
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2.0f );
		
		leftArm = new PlayerShoulder( Side.LEFT );
		rightArm = new PlayerShoulder( Side.RIGHT );
		head = new PlayerHead();
		
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		leftArm.display( sb );

		head.display( sb );
		
		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 35.0f, 2.0f,  0.0f);
			glVertex3f( 35.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );
		
		rightArm.display( sb );
		
		popTransforms( sb );
	}
}

class PlayerTuckles extends Model
{
	public final float width = 3.0f;
	public final float height = 4.0f;
	
	public PlayerTuckles( Side a )
	{
		super( new Point( 9.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2.0f );
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 3.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );

		popTransforms( sb );
	}
};

class PlayerFoot extends Model
{
	public PlayerTuckles tuckles;
	public final float width = 9.0f;
	public final float height = 4.0f;

	public PlayerFoot( Side a )
	{
		super( new Point( 20.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f ,1.0f ) );
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2.0f );
		
		tuckles = new PlayerTuckles( a );
		
		switch( a )
		{
			case LEFT:
				angle.z = 90.0f; break;
			case RIGHT:
				angle.z = 90.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 9.0f, 2.0f,  0.0f);
			glVertex3f( 9.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );

		tuckles.display( sb );

		popTransforms( sb );
	}
}

class PlayerShin extends Model
{
	public PlayerFoot foot;
	public final float width = 20.0f;
	public final float height = 4.0f;

	PlayerShin( Side a ) 
	{
		super( new Point( 20.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2.0f );

		foot = new PlayerFoot( a );

		switch( a )
		{
			case LEFT:
				angle.z = -60.0f; break;
			case RIGHT:
				angle.z = -45.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );

		foot.display( sb );

		popTransforms( sb );
	}

}

class PlayerThigh extends Model
{
	public PlayerShin shin;
	public Side side;
	public final float width = 20.0f;
	public final float height = 4.0f;

	public PlayerThigh( Side a )
	{ 
		super( new Point( 0.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		
		image = new Sprite( TextureMapping.staticGet( "BlackTemp.png" ) );
		image.setBounds( 0.0f, 0.0f, width, height );
		image.setPosition( 0.0f, -height/2 );
		
		shin = new PlayerShin( a ); 

		side = a;

		switch( a )
		{
			case LEFT:
				angle.z = -45.0f; break;
			case RIGHT:
				angle.z = -90.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/
		drawSprite( sb );

		shin.display( sb );

		popTransforms( sb );
	}

};

class PlayerHip extends Model
{
	public PlayerTorso torso;
	public PlayerThigh leftThigh;
	public PlayerThigh rightThigh;

	public PlayerHip( Point pos, Point angle ) 
	{
		super( pos, angle, new Point( 1.0f, 1.0f, 1.0f ) );
		torso = new PlayerTorso();
		leftThigh = new PlayerThigh( Side.LEFT );
		rightThigh = new PlayerThigh( Side.RIGHT );
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		leftThigh.display( sb );
		torso.display( sb );
		rightThigh.display( sb );

		popTransforms( sb );
	}
};
