cd `dirname $0`
java -Xmx2048M -cp "../rolling-desktop/bin/:\
../rolling-desktop/libs/gdx-backend-lwjgl.jar:\
../rolling-desktop/libs/gdx-backend-lwjgl-natives.jar:\
../rolling-desktop/libs/gdx-natives.jar:\
../rolling/bin/:\
../rolling/libs/gdx.jar" com.me.mygdxgame.Main


