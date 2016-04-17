package mx.itesm.ninjanofukushuu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import com.badlogic.gdx.Input.Keys;


/*
Desarrolladores: Irvin Emmanuel Trujillo Díaz, Javier García Roque y Luis Fernando
Descripción: Esta clase es la encargada de mostrar el juego y su comportamiento...
Profesor: Roberto Martinez Román.
*/
public class PantallaJuego implements Screen{

    public static final float ANCHO_MAPA = 1280;   // Como se creó en Tiled

    public static final int TAM_CELDA = 16;
    // Referencia al objeto de tipo Game (tiene setScreen para cambiar de pantalla).
    private Principal plataforma;
    //La camara y vista principal
    private OrthographicCamera camara;
    private Viewport vista;
    //Objeto para dibujar en la pantalla
    private SpriteBatch batch;
    //Mapa
    private TiledMap mapa; //Infomracion del mapa en memoria
    private OrthogonalTiledMapRenderer rendererMapa; //Objeto para dibujar el mapa
    private  Fondo fondo; //Imagen de fondo
    private Texture texturaFondo; //Textura fondo
    //Personaje Principal
    private Personaje hataku; //hataku en nuestro personaje principal
    private Texture texturaHataku;       // Aquí cargamos la imagen, en el caso de la clase, se cargo hatakuSprite.png con varios frames
    // HUD. Los componentes en la pantalla que no se mueven
    private OrthographicCamera camaraHUD;   // Cámara fija
    // Botones izquierda/derecha
    private Texture texturaBtnIzquierda;
    private Boton btnIzquierda;
    private Texture texturaBtnDerecha;
    private Boton btnDerecha;
    private boolean banderaBotonTouchApretado = false; //es para que se pueda probar con el teclado y los botones del juego al mismo tiempo..
    // Botón saltar
    private Texture texturaSalto;
    private Boton btnSalto;

    //elemenos para la pausa...
    private Texture texturaPausa, texturaContinue, texturaMenu, texturaFondoPausa;
    private Fondo fondoPausa;
    private Boton btnPausa, btnContinue, btnMenu;
    private int anchoBotonesInteractivosPausa = 250, altoBotonesInteractivosPausa = 100;



    //tamañoBotones
    private static int TAMANIO_BOTON = 89;


    //ITEMS

    //Scrolls/Pergamino
    private LinkedList<ObjetosJuego> scroll;
    private Texture texturaScroll;

    //Enemigos
    private LinkedList<ObjetosJuego> enemigoN1;
    private Texture texturaEN1;
    private LinkedList<ObjetosJuego> enemigoN2; //Enemigo especial con movimiento...usan la misma textura que los enemigos.
    private LinkedList<ObjetosJuego> enemigoN3;

    //Templo
    private LinkedList<ObjetosJuego> templos; //son en total 3 templos...Yo (Luis) digo que 1 es mas que suficiente
    private Texture texturaTemplo;

    //Pociones
    private LinkedList<ObjetosJuego> pociones;
    private Texture texturaPocion;

    //HUD, MARCADORES DE VIDA Y PERGAMINOS..

    //VIDAS QUE SE MUESTRAN EN EL HUD.

    //Tambien nos va servir de marcador...
    private LinkedList<ObjetosJuego> vidas;
    private Texture texturaVidas;


    //Ataque
    private  LinkedList<ObjetosJuego> ataques;
    private Texture texturaAtaque;

    //Marcadores
    private int marcadorPergaminos;
    private Texto textoMarcadorPergaminos; //Texto para mostrar el marcador de vidas y marcador de pergaminos.
    private Texto textoMarcadorVidas;

    //Mover las plataformas.
    private TiledMapTileLayer capa;
    private TiledMapTileLayer.Cell celda;
    private TiledMapTileLayer.Cell celda1;
    private TiledMapTileLayer.Cell celda2;
    private TiledMapTileLayer.Cell celda3;
    private int y=0, x=21;

    // Estados del juego
    private EstadosJuego estadoJuego;

    //Efectos del juego
    private Sound efectoSaltoHataku, efectoTomarVida, efectoTomarPergamino, efectoDanio, efectoPuertaTemplo;



    private boolean flag = false; //boleano para saber si se esta jugando el primer nivel, esto con el fin de cuando se esta jugando en el nivel 2 y 3, hay enemigos diferentes y no genere un error al correr el codigo...
    private int  numeroNivel;
    private int ataqueFlag;




    /*//Estado para la suma del marcador
    private Estado estado;*/

    public PantallaJuego(Principal plataforma,int nivel) {
        this.plataforma = plataforma;
        numeroNivel=nivel;
    }

    @Override
    public void show() {

        //Verifica si es el primer nivel
        if(this.numeroNivel==1)
            this.flag =true;


        // Crea la cámara/vista
        camara = new OrthographicCamera(Principal.ANCHO_CAMARA, Principal.ALTO_CAMARA);
        camara.position.set(Principal.ANCHO_CAMARA / 2, Principal.ALTO_CAMARA / 2, 0);
        camara.update();
        vista = new StretchViewport(Principal.ANCHO_CAMARA, Principal.ALTO_CAMARA, camara);

        batch = new SpriteBatch();

        // Cámara para HUD
        camaraHUD = new OrthographicCamera(Principal.ANCHO_CAMARA, Principal.ALTO_CAMARA);
        camaraHUD.position.set(Principal.ANCHO_CAMARA / 2, Principal.ALTO_CAMARA / 2, 0);
        camaraHUD.update();

        this.crearObjetos();

        // Indicar el objeto que atiende los eventos de touch (entrada en general)
        Gdx.input.setInputProcessor(new ProcesadorEntrada());

        estadoJuego = EstadosJuego.JUGANDO;
    }


    //los recursos se cargan en la pantallaCargando

    private void crearObjetos() {
        AssetManager assetManager = plataforma.getAssetManager();   // Referencia al assetManager

        //Texturas que se usan en todos los niveles
        this.texturaFondoPausa = assetManager.get("seleccionNivel/recursosPausa/fondoPausa.png");
        this.texturaContinue = assetManager.get("seleccionNivel/recursosPerdiste/continue.png");
        this.texturaMenu = assetManager.get("seleccionNivel/recursosPausa/menu.png");

        //botones menuPausa
        this.btnContinue = new Boton(texturaContinue);
        this.btnMenu = new Boton(this.texturaMenu);



        this.fondoPausa =  new Fondo(this.texturaFondoPausa);


        if (this.numeroNivel == 1){ //en el nivel tierra{
            // Carga el mapa en memoria
            mapa = assetManager.get("seleccionNivel/recursosNivelTierra/MapaDeTierraV2.tmx");
        //mapa.getLayers().get(0).setVisible(false);
        // Crear el objeto que dibujará el mapa
        rendererMapa = new OrthogonalTiledMapRenderer(mapa, batch);
        rendererMapa.setView(camara);
        // Cargar frames
        //texturaHataku = assetManager.get("seleccionNivel/recursosNivelTierra/marioSprite.png");
        texturaHataku = assetManager.get("seleccionNivel/recursosNivelTierra/Hataku.png");
        // Crear el personaje
        hataku = new Personaje(texturaHataku);
        // Posición inicial del personaje
        if (this.numeroNivel == 1) //en el nivel tierra
            hataku.getSprite().setPosition(30, 100);

        //Textura fondo
        this.texturaFondo = assetManager.get("seleccionNivel/recursosNivelTierra/fondoTierra.jpg");
        fondo = new Fondo(texturaFondo);

        //Textura Objetos que estan en la pantalla
        this.texturaScroll = assetManager.get("seleccionNivel/items/scroll.png");
        this.texturaPocion = assetManager.get("seleccionNivel/items/pocion.png");
        this.texturaEN1 = assetManager.get("seleccionNivel/recursosNivelTierra/TierraE.png");
        this.texturaAtaque = assetManager.get("seleccionNivel/items/llama1.png");
        this.texturaTemplo = assetManager.get("seleccionNivel/recursosNivelTierra/temploVerde.png");

        //****************************************************************//
        //nota: se debe cosniderar que la imagen de vidas va cambiar cuando el ninja obtenga una parte de la armadura, recomiendo usar un switch y usar una bandera (boolean) cuando se pase el nivel y deppendiendo de la bandera cargar el archivo de imagenn correspondiente
        //Por ahora no lo implemento ya que estamos trabajando en el primer nivel.
        this.texturaVidas = assetManager.get("seleccionNivel/recursosNivelTierra/life1.png");


        //Musica y efectos se obtienen y se ajusta el volumen
        this.efectoSaltoHataku = assetManager.get("seleccionNivel/sonidosGameplay/efectoSaltoHataku.wav");
        this.efectoTomarVida = assetManager.get("seleccionNivel/sonidosGameplay/efectoVida.wav");
        this.efectoTomarPergamino = assetManager.get("seleccionNivel/sonidosGameplay/efectoPergamino.wav");
        this.efectoDanio = assetManager.get("seleccionNivel/sonidosGameplay/efectoDanio.wav");
        this.efectoPuertaTemplo = assetManager.get("seleccionNivel/sonidosGameplay/puertaTemplo.wav");


        // Crear los botones
        texturaBtnIzquierda = assetManager.get("seleccionNivel/botonesFlechas/izquierdaImagenes.png");
        btnIzquierda = new Boton(texturaBtnIzquierda);
        btnIzquierda.setPosicion(TAM_CELDA * 2, TAM_CELDA / 5);
        btnIzquierda.setAlfa(0.7f); // Un poco de transparencia
        btnIzquierda.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);

        texturaBtnDerecha = assetManager.get("seleccionNivel/botonesFlechas/derechaImagenes.png");
        btnDerecha = new Boton(texturaBtnDerecha);
        btnDerecha.setPosicion(TAM_CELDA * 8, TAM_CELDA / 5);
        btnDerecha.setAlfa(0.7f); // Un poco de transparencia
        btnDerecha.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);

        texturaSalto = assetManager.get("seleccionNivel/botonesFlechas/salto.png"); //boton para saltar... carga su imagen
        btnSalto = new Boton(texturaSalto);
        btnSalto.setPosicion(Principal.ANCHO_CAMARA - 6 * TAM_CELDA, 100 + TAM_CELDA);
        btnSalto.setAlfa(0.7f);
        btnSalto.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);






        //Se crean objetos que son textos que se muestran en el HUD.
        this.textoMarcadorVidas = new Texto(0.1f * Principal.ANCHO_CAMARA + 30, Principal.ALTO_CAMARA * 0.96f);
        this.textoMarcadorPergaminos = new Texto(50 + 0.70f * Principal.ANCHO_CAMARA + 26, Principal.ALTO_CAMARA * 0.96f); //mandamos la posicion que queremos por default.


        this.texturaPausa = assetManager.get("seleccionNivel/recursosPausa/Pausa.png");
        this.btnPausa = new Boton(this.texturaPausa);
        this.btnPausa.setAlfa(0.7f);
        this.btnPausa.setPosicion(this.textoMarcadorVidas.getX()-86,this.textoMarcadorVidas.getY()-23);




        //Lista scrolles: en todos los niveles solo hay 3 scroll
        this.scroll = new LinkedList<ObjetosJuego>();
        for (int i = 0; i < 3; i++) {
            ObjetosJuego nuevo = new ObjetosJuego(this.texturaScroll);
            nuevo.setTamanio(12, 35);
            this.scroll.add(nuevo);
        }

        //Posiciones pergamino nivel tierra
        this.scroll.get(0).setPosicion(50, 340); //pergamino de en medio...
        this.scroll.get(1).setPosicion(745, 32); //pergamino de hasta abajo
        this.scroll.get(2).setPosicion(627, 76); //pergamino que está en precipicio

        //Pociones: En todos los niveles solo hay 2 pociones.
        this.pociones = new LinkedList<ObjetosJuego>();
        for (int i = 0; i < 1; i++) {
            ObjetosJuego nuevo = new ObjetosJuego(this.texturaPocion);
            nuevo.setTamanio(30, 40);
            this.pociones.add(nuevo);
        }

        //Se colocan las pociones en el lugar correspondiente,

        this.pociones.get(0).setPosicion(255, 270);

        //Enemigos: 5 enemigos en el primer nivel
        this.enemigoN1 = new LinkedList<ObjetosJuego>();
        for (int i = 0; i < 5; i++) {
            ObjetosJuego nuevo = new ObjetosJuego(this.texturaEN1);
            nuevo.setTamanio(60, 90);
            this.enemigoN1.add(nuevo);
        }

        //Se colocan los enemigos en su lugar correspondiente, en el nivel de TIERRA
        this.enemigoN1.get(0).setPosicion(900, 519); //samurai parte superior
        this.enemigoN1.get(1).setPosicion(470, 280);  //Samurai centro
        this.enemigoN1.get(2).setPosicion(790, 295); //Samurai Escalon
        this.enemigoN1.get(3).setPosicion(960, 120); //Samurai escalon
        this.enemigoN1.get(4).setPosicion(570, 503); //Samurai parte superior

        //Colocar los ataque en su posicion
        this.ataques = new LinkedList<ObjetosJuego>();
        for (ObjetosJuego enemigo : enemigoN1) {
            ObjetosJuego nuevo = new ObjetosJuego(this.texturaAtaque);
            //nuevo.setTamanio(30, 30);
            this.ataques.add(nuevo);
            nuevo.setPosicion(enemigo.getSprite().getX() + 15, enemigo.getSprite().getY() + 25);
        }

        //Aqui se piensa poner un switch evaluando una variable de nivel,  de eso va dependar donde se va colocar el templo
        //templos, son 3.
        this.templos = new LinkedList<ObjetosJuego>();
        for (int i = 0; i < 3; i++) {
            ObjetosJuego nuevo = new ObjetosJuego(this.texturaTemplo);
            nuevo.setTamanio(60, 90);
            this.templos.add(nuevo);
        }

        this.templos.get(0).setPosicion(230, 510); //temploTierra


        //Objetos que representan las vidas, son las caras del ninja que estan en el HUD



            this.vidas = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 3; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaVidas);
                //nuevo.setTamanio(70,70); //Irvin ya ajusto el tamaño de las vidas en photoshop..
                this.vidas.add(nuevo);
            }
            this.vidas.get(0).setPosicion(this.textoMarcadorVidas.getX() + 95, this.textoMarcadorVidas.getY() - 45);
            this.vidas.get(1).setPosicion(this.textoMarcadorVidas.getX() + 165, this.textoMarcadorVidas.getY() - 45);
            this.vidas.get(2).setPosicion(this.textoMarcadorVidas.getX() + 235, this.textoMarcadorVidas.getY() - 45);

        }
        else if (this.numeroNivel == 2){
            // Carga el mapa en memoria
            mapa = assetManager.get("seleccionNivel/recursosNivelAgua/MapaDeAgua2.tmx");
            //mapa.getLayers().get(0).setVisible(false);
            // Crear el objeto que dibujará el mapa
            rendererMapa = new OrthogonalTiledMapRenderer(mapa, batch);
            rendererMapa.setView(camara);
            // Cargar frames
            texturaHataku = assetManager.get("seleccionNivel/recursosNivelAgua/ninjita.png");
            // Crear el personaje
            hataku = new Personaje(texturaHataku);
            // Posición inicial del personaje
            hataku.getSprite().setPosition(20, 20);

            //Textura fondo
            this.texturaFondo = assetManager.get("seleccionNivel/recursosNivelAgua/fondoAgua.png");
            fondo = new Fondo(texturaFondo);


                    //Textura Objetos que estan en la pantalla
            this.texturaScroll = assetManager.get("seleccionNivel/items/scroll.png");
            this.texturaPocion = assetManager.get("seleccionNivel/items/pocion.png");
            this.texturaEN1 = assetManager.get("seleccionNivel/recursosNivelAgua/aguaE.png");
            this.texturaAtaque = assetManager.get("seleccionNivel/items/ataque2.png");
            this.texturaTemplo = assetManager.get("seleccionNivel/recursosNivelTierra/temploVerde.png"); /*LUIS! NO LE MUEVAS, EN LA TABLETA NO CARGA TEMPLOAZUL.. */

            //****************************************************************//
            //nota: se debe cosniderar que la imagen de vidas va cambiar cuando el ninja obtenga una parte de la armadura, recomiendo usar un switch y usar una bandera (boolean) cuando se pase el nivel y deppendiendo de la bandera cargar el archivo de imagenn correspondiente
            //Por ahora no lo implemento ya que estamos trabajando en el primer nivel.
            this.texturaVidas = assetManager.get("seleccionNivel/recursosNivelAgua/life2.png");


            //Musica y efectos se obtienen y se ajusta el volumen
            this.efectoSaltoHataku = assetManager.get("seleccionNivel/sonidosGameplay/efectoSaltoHataku.wav");
            this.efectoTomarVida = assetManager.get("seleccionNivel/sonidosGameplay/efectoVida.wav");
            this.efectoTomarPergamino = assetManager.get("seleccionNivel/sonidosGameplay/efectoPergamino.wav");
            this.efectoDanio = assetManager.get("seleccionNivel/sonidosGameplay/efectoDanio.wav");
            this.efectoPuertaTemplo = assetManager.get("seleccionNivel/sonidosGameplay/puertaTemplo.wav");


            // Crear los botones
            texturaBtnIzquierda = assetManager.get("seleccionNivel/botonesFlechas/izquierdaImagenes.png");
            btnIzquierda = new Boton(texturaBtnIzquierda);
            btnIzquierda.setPosicion(TAM_CELDA * 2, TAM_CELDA / 5);
            btnIzquierda.setAlfa(0.7f); // Un poco de transparencia
            btnIzquierda.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);

            texturaBtnDerecha = assetManager.get("seleccionNivel/botonesFlechas/derechaImagenes.png");
            btnDerecha = new Boton(texturaBtnDerecha);
            btnDerecha.setPosicion(TAM_CELDA * 8, TAM_CELDA / 5);
            btnDerecha.setAlfa(0.7f); // Un poco de transparencia
            btnDerecha.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);

            texturaSalto = assetManager.get("seleccionNivel/botonesFlechas/salto.png"); //boton para saltar... carga su imagen
            btnSalto = new Boton(texturaSalto);
            btnSalto.setPosicion(Principal.ANCHO_CAMARA - 6 * TAM_CELDA, 100 + TAM_CELDA);
            btnSalto.setAlfa(0.7f);
            btnSalto.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);


            //Se crean objetos que son textos que se muestran en el HUD.
            this.textoMarcadorVidas = new Texto(0.1f * Principal.ANCHO_CAMARA + 30, Principal.ALTO_CAMARA * 0.96f);
            this.textoMarcadorPergaminos = new Texto(50 + 0.70f * Principal.ANCHO_CAMARA + 26, Principal.ALTO_CAMARA * 0.96f); //mandamos la posicion que queremos por default.

            this.texturaPausa = assetManager.get("seleccionNivel/recursosPausa/Pausa.png");
            this.btnPausa = new Boton(this.texturaPausa);
            this.btnPausa.setAlfa(0.7f);
            this.btnPausa.setPosicion(this.textoMarcadorVidas.getX()-86,this.textoMarcadorVidas.getY()-23);



            //Lista scrolles: en todos los niveles solo hay 3 scroll
            this.scroll = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 3; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaScroll);
                nuevo.setTamanio(12, 35);
                this.scroll.add(nuevo);
            }

            //Posiciones pergamino nivel agua
            this.scroll.get(0).setPosicion(20, 1040); //pergamino derecha arriba.
            this.scroll.get(1).setPosicion(680, 1230); //pergamino de hasta arriba izquierda
            this.scroll.get(2).setPosicion(680, 76); //pergamino abajo

            //Pociones: En todos los niveles solo hay 2 pociones.
            this.pociones = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 1; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaPocion);
                nuevo.setTamanio(30, 40);
                this.pociones.add(nuevo);
            }

            //Se colocan las pociones en el lugar correspondiente,
            this.pociones.get(0).setPosicion(400, 630);


            //Enemigos: 4 enemigos en el segundo nivel
            this.enemigoN1 = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 4; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaEN1);
                nuevo.setTamanio(60, 90);
                this.enemigoN1.add(nuevo);
            }

            //Se colocan los enemigos en su lugar correspondiente, en el nivel de Agua
            this.enemigoN1.get(0).setPosicion(20, 565); //centro izquierda
            this.enemigoN1.get(1).setPosicion(560, 678);  //centro derecha
            this.enemigoN1.get(2).setPosicion(530, 805); //plataforma derecha
            this.enemigoN1.get(3).setPosicion(290, 805); //Plataforma Izquierda

            //Enemigos especiales
            this.enemigoN2 = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 4; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(texturaEN1);
                nuevo.setTamanio(60,90);
                this.enemigoN2.add(nuevo);
            }

            this.enemigoN2.get(0).setPosicion(330,165);
            this.enemigoN2.get(1).setPosicion(590,965);
            this.enemigoN2.get(2).setPosicion(180,965);
            this.enemigoN2.get(3).setPosicion(380,1126);

            //Colocar los ataque en su posicion
            this.ataques = new LinkedList<ObjetosJuego>();
            for (ObjetosJuego enemigo : enemigoN1) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaAtaque);
                nuevo.setTamanio(30, 30);
                this.ataques.add(nuevo);
                nuevo.setPosicion(enemigo.getSprite().getX() + 15, enemigo.getSprite().getY() + 25);
            }

            //Aqui se piensa poner un switch evaluando una variable de nivel,  de eso va dependar donde se va colocar el templo
            //templos, son 3.
            this.templos = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 3; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaTemplo);
                nuevo.setTamanio(60, 90);
                this.templos.add(nuevo);
            }

            this.templos.get(0).setPosicion(20, 1170); //temploAgua

            this.vidas = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 3; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaVidas);
                nuevo.setTamanio(70,70); //Irvin ya ajusto el tamaño de las vidas en photoshop..
                this.vidas.add(nuevo);
            }
            this.vidas.get(0).setPosicion(this.textoMarcadorVidas.getX() + 95, this.textoMarcadorVidas.getY() - 45);
            this.vidas.get(1).setPosicion(this.textoMarcadorVidas.getX() + 165, this.textoMarcadorVidas.getY() - 45);
            this.vidas.get(2).setPosicion(this.textoMarcadorVidas.getX() + 235, this.textoMarcadorVidas.getY() - 45);

            }

        //Nivel Fuego----------------------------------------------------------------------------------
        else if (this.numeroNivel == 3){
            //Mover plataformas.

            // Carga el mapa en memoria
            mapa = assetManager.get("seleccionNivel/recursosNivelFuego/mapaDeFuego.tmx");
            capa=(TiledMapTileLayer)mapa.getLayers().get(1);
            celda=capa.getCell(23,40);
            celda1=capa.getCell(22,40);
            celda2=capa.getCell(23,39);
            celda3=capa.getCell(22,39);

            //mapa.getLayers().get(0).setVisible(false);
            // Crear el objeto que dibujará el mapa
            rendererMapa = new OrthogonalTiledMapRenderer(mapa, batch);
            rendererMapa.setView(camara);
            // Cargar frames
            texturaHataku = assetManager.get("seleccionNivel/recursosNivelFuego/ninjaS.png");
            // Crear el personaje
            hataku = new Personaje(texturaHataku);
            // Posición inicial del personaje
            hataku.getSprite().setPosition(40, 90);

            //Textura fondo
            this.texturaFondo = assetManager.get("seleccionNivel/recursosNivelFuego/fondofuego.png");
            fondo = new Fondo(texturaFondo);

            //Textura Objetos que estan en la pantalla
            this.texturaScroll = assetManager.get("seleccionNivel/items/scroll.png");
            this.texturaPocion = assetManager.get("seleccionNivel/items/pocion.png");
            this.texturaEN1 = assetManager.get("seleccionNivel/recursosNivelFuego/Enemigo3.png");
            this.texturaAtaque = assetManager.get("seleccionNivel/items/ataque3.png");
            this.texturaTemplo = assetManager.get("seleccionNivel/recursosNivelFuego/temploRojo.png");

            //****************************************************************//
            //nota: se debe cosniderar que la imagen de vidas va cambiar cuando el ninja obtenga una parte de la armadura, recomiendo usar un switch y usar una bandera (boolean) cuando se pase el nivel y deppendiendo de la bandera cargar el archivo de imagenn correspondiente
            //Por ahora no lo implemento ya que estamos trabajando en el primer nivel.
            this.texturaVidas = assetManager.get("seleccionNivel/recursosNivelFuego/lifeFuego.png");


            //Musica y efectos se obtienen y se ajusta el volumen
            this.efectoSaltoHataku = assetManager.get("seleccionNivel/sonidosGameplay/efectoSaltoHataku.wav");
            this.efectoTomarVida = assetManager.get("seleccionNivel/sonidosGameplay/efectoVida.wav");
            this.efectoTomarPergamino = assetManager.get("seleccionNivel/sonidosGameplay/efectoPergamino.wav");
            this.efectoDanio = assetManager.get("seleccionNivel/sonidosGameplay/efectoDanio.wav");
            this.efectoPuertaTemplo = assetManager.get("seleccionNivel/sonidosGameplay/puertaTemplo.wav");


            // Crear los botones
            texturaBtnIzquierda = assetManager.get("seleccionNivel/botonesFlechas/izquierdaImagenes.png");
            btnIzquierda = new Boton(texturaBtnIzquierda);
            btnIzquierda.setPosicion(TAM_CELDA * 2, TAM_CELDA / 5);
            btnIzquierda.setAlfa(0.7f); // Un poco de transparencia
            btnIzquierda.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);

            texturaBtnDerecha = assetManager.get("seleccionNivel/botonesFlechas/derechaImagenes.png");
            btnDerecha = new Boton(texturaBtnDerecha);
            btnDerecha.setPosicion(TAM_CELDA * 8, TAM_CELDA / 5);
            btnDerecha.setAlfa(0.7f); // Un poco de transparencia
            btnDerecha.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);

            texturaSalto = assetManager.get("seleccionNivel/botonesFlechas/salto.png"); //boton para saltar... carga su imagen
            btnSalto = new Boton(texturaSalto);
            btnSalto.setPosicion(Principal.ANCHO_CAMARA - 6 * TAM_CELDA, 100 + TAM_CELDA);
            btnSalto.setAlfa(0.7f);
            btnSalto.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON);


            //Se crean objetos que son textos que se muestran en el HUD.
            this.textoMarcadorVidas = new Texto(0.1f * Principal.ANCHO_CAMARA + 30, Principal.ALTO_CAMARA * 0.96f);
            this.textoMarcadorPergaminos = new Texto(50 + 0.70f * Principal.ANCHO_CAMARA + 26, Principal.ALTO_CAMARA * 0.96f); //mandamos la posicion que queremos por default.

            this.texturaPausa = assetManager.get("seleccionNivel/recursosPausa/Pausa.png");
            this.btnPausa = new Boton(this.texturaPausa);
            this.btnPausa.setAlfa(0.7f);
            this.btnPausa.setPosicion(this.textoMarcadorVidas.getX()-86,this.textoMarcadorVidas.getY()-23);



            //Lista scrolles: en todos los niveles solo hay 3 scroll
            this.scroll = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 3; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaScroll);
                nuevo.setTamanio(12, 35);
                this.scroll.add(nuevo);
            }

            //Posiciones pergamino nivel agua
            this.scroll.get(0).setPosicion(40, 350); //pergamino derecha arriba.
            this.scroll.get(1).setPosicion(680, 900); //pergamino de hasta arriba izquierda
            this.scroll.get(2).setPosicion(676, 350); //pergamino abajo

            //Pociones: En todos los niveles solo hay 2 pociones.
            this.pociones = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 1; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaPocion);
                nuevo.setTamanio(30, 40);
                this.pociones.add(nuevo);
            }

            //Se colocan las pociones en el lugar correspondiente,
            this.pociones.get(0).setPosicion(400, 630);


            //Enemigos: 4 enemigos en el segundo nivel
            this.enemigoN1 = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 4; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaEN1);
                nuevo.setTamanio(60, 90);
                this.enemigoN1.add(nuevo);
            }

            //Se colocan los enemigos en su lugar correspondiente, en el nivel de Agua
            this.enemigoN1.get(0).setPosicion(280, 170); //centro izquierda
            this.enemigoN1.get(1).setPosicion(560, 440);  //centro derecha
            this.enemigoN1.get(2).setPosicion(480, 920); //plataforma derecha
            this.enemigoN1.get(3).setPosicion(225, 760); //Plataforma Izquierda

            //Enemigos especiales
            this.enemigoN2 = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 2; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(texturaAtaque);
                nuevo.setTamanio(60,90);
                this.enemigoN2.add(nuevo);
            }

            this.enemigoN2.get(0).setPosicion(79, 200);
            this.enemigoN2.get(1).setPosicion(79, 930);


            //Colocar los ataque en su posicion
            this.ataques = new LinkedList<ObjetosJuego>();
            for (ObjetosJuego enemigo : enemigoN1) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaAtaque);
                nuevo.setTamanio(30, 30);
                this.ataques.add(nuevo);
                nuevo.setPosicion(enemigo.getSprite().getX() + 15, enemigo.getSprite().getY() + 25);
            }

            //Aqui se piensa poner un switch evaluando una variable de nivel,  de eso va dependar donde se va colocar el templo
            //templos, son 3.
            this.templos = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 3; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaTemplo);
                nuevo.setTamanio(60, 90);
                this.templos.add(nuevo);
            }

            this.templos.get(0).setPosicion(20, 960); //temploFuego

            this.vidas = new LinkedList<ObjetosJuego>();
            for (int i = 0; i < 3; i++) {
                ObjetosJuego nuevo = new ObjetosJuego(this.texturaVidas);
                nuevo.setTamanio(70,70); //Irvin ya ajusto el tamaño de las vidas en photoshop..
                this.vidas.add(nuevo);
            }
            this.vidas.get(0).setPosicion(this.textoMarcadorVidas.getX() + 95, this.textoMarcadorVidas.getY() - 45);
            this.vidas.get(1).setPosicion(this.textoMarcadorVidas.getX() + 165, this.textoMarcadorVidas.getY() - 45);
            this.vidas.get(2).setPosicion(this.textoMarcadorVidas.getX() + 235, this.textoMarcadorVidas.getY() - 45);

        }
    }
    //...
    /*
    Dibuja TODOS los elementos del juego en la pantalla.
    Este método se está ejecutando muchas veces por segundo.
     */
    @Override
    public void render(float delta) { // delta es el tiempo entre frames (Gdx.graphics.getDeltaTime())
        // Leer entrada
        //Gdx.app.log("",hataku.getY()+"");

        if(estadoJuego == EstadosJuego.JUGANDO) {
            // Actualizar objetos en la pantalla
            moverPersonaje();
            if (numeroNivel == 2) {
                actualizarCamaraAgua();
            } else if (numeroNivel == 3) {
                actualizarCamaraFuego();
                y++;
                if(y%20==0){
                    capa.setCell(17,42,celda);
                    capa.setCell(16,42,celda1);
                    capa.setCell(16,41,celda2);
                    capa.setCell(17,41,celda3);
                }
                if(y%25==0){
                    capa.setCell(x,3,celda3);
                    capa.setCell(x+1,2,celda3);
                    capa.setCell(x+2,2,celda2);
                    capa.setCell(x+3,3,celda2);
                    x++;
                }
                if(y%160==0){
                    capa.setCell(17,42,null);
                    capa.setCell(16,42,null);
                    capa.setCell(16,41,null);
                    capa.setCell(17,41,null);
                }
                if(y%25==0){
                    capa.setCell(x-1,3,null);
                    capa.setCell(33,2,null);
                    capa.setCell(32,2,null);
                    capa.setCell(34,3,null);
                }
                if(x==32){
                    x=21;
                }

            } else {
                actualizarCamara(); // Mover la cámara para que siga al personaje
            }

            // Dibujar
            borrarPantalla();

            // Para verificar si el usuario ya tomo los 3 pergaminos y liberar el boton de galeria de arte...
            liberarArte();

            //Para verificar si el usuario ya perdio...
            perderJuego();

            //Para verificar si el usuario ya gano...
            ganarJuego();


            //MOVER PERSONAJES CON TECLADO (ESTO ES UTIL PARA LAS PRUEBAS,
            // SE PIENSA COMENTAR AL ENTREGAR EL PROYECTO)
            //
            controlarPersonajeConTeclado();


            //DIBUJAR OBJETOS COMPONENTES DEL JUEGO
            batch.setProjectionMatrix(camara.combined);
            batch.begin();
            fondo.render(batch);
            batch.end();
            rendererMapa.setView(camara);
            rendererMapa.render();  // Dibuja el mapa
            recogerObjeto();
            // Entre begin-end dibujamos nuestros objetos en pantalla
            batch.begin();
            hataku.danio();
            hataku.render(batch);    // Dibuja el personaje
            //Dibujar scrolls
            for (ObjetosJuego scrolls : scroll) {
                if (scrolls.actualizar()) {
                    scrolls.render(batch);
                }
            }
            //Dibujar pociones
            for (ObjetosJuego pocion : pociones) {
                if (pocion.actualizar())
                    pocion.render(batch);
            }

            for (ObjetosJuego Enemigo : enemigoN1) {
                if (Enemigo.actualizar())
                    Enemigo.render(batch);
            }
            if (numeroNivel >= 2 && !this.flag) {
                for (ObjetosJuego enemigo : enemigoN2) {
                    if (enemigo.actualizar())
                        enemigo.render(batch);
                    cambioDireccion(enemigo);
                }
            }

            ataqueFlag = 0;
            //Dibujar ataques
            for (int i = 0; i < ataques.size(); i++) {
                ObjetosJuego ataque = ataques.get(i);
                ObjetosJuego enemigo = enemigoN1.get(i);
                ataque.render(batch);
                atacarEnemigo(ataque, enemigo);
                ataqueFlag++;
            }

            this.templos.get(0).render(batch);//temploTierra


            batch.end();
            //Dibuja el HUD
            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            // Mostrar pergaminos
            this.textoMarcadorPergaminos.mostrarMensaje(batch, "Scrolls: " + this.marcadorPergaminos);
            // Mostrar vida
            this.textoMarcadorVidas.mostrarMensaje(batch, "Health: ");
            //Dibujar iconos vidas
            for (ObjetosJuego vida : this.vidas) {
                if (vida.actualizar()) {
                    vida.render(batch);
                }
            }
            btnIzquierda.render(batch);
            btnDerecha.render(batch);
            btnSalto.render(batch);
            btnPausa.render(batch);

            batch.end();
        }
        else if(estadoJuego == EstadosJuego.PAUSA) {

            batch.setProjectionMatrix(camara.combined);
            batch.begin();
            fondo.render(batch);
            batch.end();

            rendererMapa.setView(camara);
            rendererMapa.render();  // Dibuja el mapa



            //Dibuja el HUD
            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            this.fondoPausa.render(batch);
            this.btnMenu.render(batch);
            this.btnContinue.render(batch);
            batch.end();


        }

    }

    private void cambioDireccion(ObjetosJuego enemigo) {
        int celdaX;
        int celdaY;
        if (enemigo.getEstadoEspecial() == ObjetosJuego.EstadoEsp.DERECHA_ESP) {
            celdaX = (int) ((enemigo.getSprite().getX() + 45) / TAM_CELDA);   // Casilla del enemigo en X
            celdaX++;   // Casilla del lado derecho
            celdaY = (int) ((enemigo.getSprite().getY() + enemigo.getSprite().getHeight() / 2) / TAM_CELDA); // Casilla del enemigo en Y
        }
        else{
            celdaX = (int) ((enemigo.getSprite().getX()) / TAM_CELDA);   // Casilla del enemigo en X
            celdaY = (int) ((enemigo.getSprite().getY() + enemigo.getSprite().getHeight() / 2) / TAM_CELDA); // Casilla del enemigo en Y
        }
        TiledMapTileLayer capaPlataforma = (TiledMapTileLayer) mapa.getLayers().get(0);
        if ( capaPlataforma.getCell(celdaX, celdaY) != null ) {
            // Colisionará y cambiara de dirección
            enemigo.cambiarSentido();
        }
        else {
            enemigo.actualizarMov(); //mover enemigo
        }
    }

    private void actualizarCamaraAgua() {
        float posX = hataku.getX();
        float posY = hataku.getY();
        // Si está en la parte 'media'
        if (posX>=Principal.ANCHO_CAMARA/2 && posX<=Principal.ALTO_MUNDO-Principal.ANCHO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posX, camara.position.y, 0);
        } else if (posX>Principal.ALTO_MUNDO-Principal.ANCHO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda media pantalla antes del fin del mundo  :)
            camara.position.set(Principal.ALTO_MUNDO-Principal.ANCHO_CAMARA/2, camara.position.y, 0);
        }
        if (posY >= Principal.ALTO_CAMARA / 2 && posY<= ANCHO_MAPA-Principal.ALTO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set(camara.position.x, (int) posY, 0);
        } else if (posY>=ANCHO_MAPA-Principal.ALTO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda media pantalla antes del fin del mundo  :)
            camara.position.set(camara.position.x, ANCHO_MAPA-Principal.ALTO_CAMARA/2, 0);
        }
        camara.update();
    }

    private void actualizarCamaraFuego() {
        float posX = hataku.getX();
        float posY = hataku.getY();
        // Si está en la parte 'media'
        if (posX>=Principal.ANCHO_CAMARA/2 && posX<=Principal.ALTO_MUNDO-Principal.ANCHO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posX, camara.position.y, 0);
        } else if (posX>Principal.ALTO_MUNDO-Principal.ANCHO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda media pantalla antes del fin del mundo  :)
            camara.position.set(Principal.ALTO_MUNDO-Principal.ANCHO_CAMARA/2, camara.position.y, 0);
        }
        if (posY>=Principal.ALTO_CAMARA/2 && posY<= 1024-Principal.ALTO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set(camara.position.x, (int)posY, 0);
        } else if (posY>=1024-Principal.ALTO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda media pantalla antes del fin del mundo  :)
            camara.position.set(camara.position.x, 1024-Principal.ALTO_CAMARA/2, 0);
        }
        camara.update();
    }

    private void controlarPersonajeConTeclado() {

        if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
            hataku.setEstado(Personaje.EstadoMovimiento.MOV_IZQUIERDA);
            this.banderaBotonTouchApretado = false;
            if (Gdx.input.isKeyPressed(Keys.SPACE)) {
                if (Personaje.EstadoSalto.EN_PISO == hataku.getEstadoSalto()) //Para que solamente suene una vez el sonido de salto
                    efectoSaltoHataku.play(PantallaMenu.volumen);
                hataku.saltar();
            }
        }

        else if(Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)){
            hataku.setEstado(Personaje.EstadoMovimiento.MOV_DERECHA);
            this.banderaBotonTouchApretado = false;
            if (Gdx.input.isKeyPressed(Keys.SPACE)) {
                if (Personaje.EstadoSalto.EN_PISO == hataku.getEstadoSalto()) //Para que solamente suene una vez el sonido de salto
                    efectoSaltoHataku.play(PantallaMenu.volumen);
                hataku.saltar();
            }
        }

        else if (Gdx.input.isKeyJustPressed(Keys.SPACE) && Personaje.EstadoSalto.EN_PISO  == hataku.getEstadoSalto() ) {
                 //Para que solamente suene una vez el sonido de salto
                efectoSaltoHataku.play(PantallaMenu.volumen);
                hataku.saltar();
        }



        else if(!banderaBotonTouchApretado)//no se esta apretando nada..
            hataku.setEstado(Personaje.EstadoMovimiento.QUIETO);


    }
    //

    private void ganarJuego() {

        //PROBADORES PARA SABER EN QUE COORDENADAS VAS A GANAR..
        /*System.out.println("y = "+this.hataku.getY());

        System.out.println("X = "+this.hataku.getX());*/

        //temploTierra
        if(258 == this.hataku.getX() &&  512 <= this.hataku.getY() && this.numeroNivel == 1){ //258  y 512 es la posicion del templo, lo identifique con el system.out.println

            this.numeroNivel = 2;
            this.marcadorPergaminos = 0;
            this.efectoPuertaTemplo.play(PantallaMenu.volumen);
            PantallaCargando.partidaGuardada.putBoolean("nivelAgua", true); //se guarda el progreso y se desbloquea el nivel de agua...
            PantallaCargando.partidaGuardada.flush(); //se guardan los cambios

            //Se va regresar a seleccion de nivel, primero se muestra la pantalla winner
            plataforma.setScreen(new PantallaWinner(this.plataforma));

        }


        // temploAgua

        if( 44 == this.hataku.getX() && 1164  <= this.hataku.getY() && this.numeroNivel == 2){

            this.numeroNivel = 3;
            this.marcadorPergaminos = 0;
            this.efectoPuertaTemplo.play(PantallaMenu.volumen);
            PantallaCargando.partidaGuardada.putBoolean("nivelFuego", true); //se guarda el progreso y se desbloquea el nivel de agua...
            PantallaCargando.partidaGuardada.flush(); //se guardan los cambios

            //Se va regresar a seleccion de nivel, primero se muestra la pantalla winner
            plataforma.setScreen(new PantallaWinner(this.plataforma));

        }

        //temploFuego

        if( 44 == this.hataku.getX() && 960 <= this.hataku.getY() && this.numeroNivel == 3){

            //this.numeroNivel = 3;
            this.marcadorPergaminos = 0;
            this.efectoPuertaTemplo.play(PantallaMenu.volumen);
            //PantallaCargando.partidaGuardada.putBoolean("nivelFuego", true); //se guarda el progreso y se desbloquea el nivel de agua...
            //PantallaCargando.partidaGuardada.flush(); //se guardan los cambios

            //Se va regresar a seleccion de nivel, primero se muestra la pantalla winner
            plataforma.setScreen(new PantallaWinner(this.plataforma));
            }

    }

    private void liberarArte() {
        if(this.marcadorPergaminos == 3 && this.numeroNivel == 1){

            PantallaCargando.partidaGuardada.putBoolean("arteTierra", true); //se guarda el progreso y se desbloquea la galeria de arte de tierra,,
            PantallaCargando.partidaGuardada.flush(); //se guardan los cambios

        }

        else if(this.marcadorPergaminos == 3 && this.numeroNivel == 2){

            PantallaCargando.partidaGuardada.putBoolean("arteAgua", true); //se guarda el progreso y se desbloquea la galeria de arte de agua..
            PantallaCargando.partidaGuardada.flush(); //se guardan los cambios


        }

        else if(this.marcadorPergaminos == 3 && this.numeroNivel == 3){

            PantallaCargando.partidaGuardada.getBoolean("arteFuego", true); // se guarda el progreso y se desbloquea la galeria de arte de fuego
            PantallaCargando.partidaGuardada.flush(); //se guardan los cambios
        }
    }

    private void atacarEnemigo(ObjetosJuego ataque, ObjetosJuego enemigo) {
        if(enemigo.getEstado()==ObjetosJuego.Estado.ENMAPA) {
            if (ataque.getEstadoAtaque() == ObjetosJuego.EstadoAtaque.DISPONIBLE) {
                if (hataku.getSprite().getX() < ataque.getSprite().getX()) {
                    ataque.mandarIzquierda();
                    if(enemigo.getSprite().isFlipX())
                        enemigo.getSprite().flip(true,false); //para que se voltee
                } else {
                    ataque.mandarDerecha();
                    if(!enemigo.getSprite().isFlipX())
                        enemigo.getSprite().flip(true,false); //para que se voltee
                }
            } else {
                ataque.actualizarAtaque(enemigoN1.get(ataqueFlag).getSprite().getX() + 15, enemigoN1.get(ataqueFlag).getSprite().getY() + 25);
            }
        }
        else {
            ataque.ocultar();
            ataque.actualizarAtaque(0, 0);
        }
    }

    private void perderJuego() {//Método para verificar si el usuario ya perdio

        //el usuario perdio sus vidas
        if (this.vidas.size() == 0){
            plataforma.setScreen(new PantallaGameOver(plataforma,this.numeroNivel)); //nos regresa a la pantalla principal.
        }

        //el usuario cayo en un precipcio
        if (this.hataku.getY()<-50){
            plataforma.setScreen(new PantallaGameOver(plataforma,this.numeroNivel)); //nos regresa a la pantalla principal.

        }
    }

    private void recogerObjeto() {
        Rectangle rh = new Rectangle(hataku.getX(),hataku.getY(),hataku.getSprite().getWidth(),hataku.getSprite().getHeight());
        //Recogerscrolls al tocarlos
        for (ObjetosJuego scrolls : scroll) {
            Rectangle rs = new Rectangle(scrolls.getSprite().getX(),scrolls.getSprite().getY(),scrolls.getSprite().getWidth(),scrolls.getSprite().getHeight());
            if(rs.overlaps(rh)) {
                if (scrolls.getEstado() != ObjetosJuego.Estado.DESAPARECIDO) {
                    this.marcadorPergaminos++;
                    this.efectoTomarPergamino.play(PantallaMenu.volumen); //suena efecto
                    scrolls.quitarElemento();
                }
                break;
            }
        }
        //Recoger pociones al tocarlas
        for (ObjetosJuego pocion : pociones) {
            Rectangle rp= new Rectangle(pocion.getSprite().getX(),pocion.getSprite().getY(),pocion.getSprite().getWidth(),pocion.getSprite().getHeight());
            if(rp.overlaps(rh)){
                if(vidas.size()<3) {
                    if (pocion.getEstado() != ObjetosJuego.Estado.DESAPARECIDO) {
                        this.efectoTomarVida.play(PantallaMenu.volumen); //suena efecto
                        ObjetosJuego nuevo = new ObjetosJuego(this.texturaVidas);
                        this.vidas.add(nuevo);
                        nuevo.setPosicion(vidas.get(vidas.size()-2).getSprite().getX()+70,this.textoMarcadorVidas.getY()-45);
                        pocion.quitarElemento();
                    }
                    break;
                }
            }
        }

        //mata enemigos al toque
        for (ObjetosJuego Enemigo : enemigoN1) {
            Rectangle rE1= new Rectangle(Enemigo.getSprite().getX(),Enemigo.getSprite().getY(),Enemigo.getSprite().getWidth(),Enemigo.getSprite().getHeight());
            if(rE1.overlaps(rh)){
                if (Enemigo.getEstado() != ObjetosJuego.Estado.DESAPARECIDO) {
                    if(hataku.getEstado()!=Personaje.Estado.DANIADO){
                        this.efectoDanio.play(PantallaMenu.volumen);
                        vidas.remove(vidas.size() - 1);
                        hataku.daniar();
                    }
                }
                break;
            }
        }

        if(!this.flag && this.numeroNivel == 2 || this.numeroNivel == 3 ) {

            //mata enemigos especiales al toque
            for (ObjetosJuego Enemigo : enemigoN2) {
                Rectangle rE2= new Rectangle(Enemigo.getSprite().getX(),Enemigo.getSprite().getY(),Enemigo.getSprite().getWidth(),Enemigo.getSprite().getHeight());
                if (rE2.overlaps(rh)) {
                    if (Enemigo.getEstado() != ObjetosJuego.Estado.DESAPARECIDO) {
                        if(hataku.getEstado()!=Personaje.Estado.DANIADO){
                            this.efectoDanio.play(PantallaMenu.volumen);
                            vidas.remove(vidas.size() - 1);
                            hataku.daniar();
                        }
                    }
                    break;
                }
            }
        }


        //tomar daño de ataque enemigos
        for (ObjetosJuego ataque: ataques){
            Rectangle ra = new Rectangle(ataque.getSprite().getX(),ataque.getSprite().getY(),ataque.getSprite().getWidth(),ataque.getSprite().getHeight());
            if(ra.overlaps(rh)){
                if (ataque.getEstadoAtaque() != ObjetosJuego.EstadoAtaque.OCULTO){
                    if(hataku.getEstado()!=Personaje.Estado.DANIADO){
                        ataque.ocultar();
                        this.efectoDanio.play(PantallaMenu.volumen);
                        vidas.remove(vidas.size() - 1);
                        hataku.daniar();
                    }
                }
            }
        }

    }

    // Actualiza la posición de la cámara para que el personaje esté en el centro,
    // excepto cuando esta en la primera y última parte del mundo
    private void actualizarCamara() {
        float posX = hataku.getX();
        float posY = hataku.getY();
        // Si está en la parte 'media'
        if (posX>=Principal.ANCHO_CAMARA/2 && posX<=ANCHO_MAPA-Principal.ANCHO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posX, camara.position.y, 0);
        } else if (posX>ANCHO_MAPA-Principal.ANCHO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda media pantalla antes del fin del mundo  :)
            camara.position.set(ANCHO_MAPA-Principal.ANCHO_CAMARA/2, camara.position.y, 0);
        }
        if (posY>=Principal.ALTO_CAMARA/2 && posY<= Principal.ALTO_MUNDO-Principal.ALTO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set(camara.position.x, (int)posY, 0);
        } else if (posY>=Principal.ALTO_MUNDO-Principal.ALTO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda media pantalla antes del fin del mundo  :)
            camara.position.set(camara.position.x, Principal.ALTO_MUNDO-Principal.ALTO_CAMARA/2, 0);
        }
        camara.update();
    }

    /*
    Mueve el personaje en Y hasta que se encuentre sobre un bloque
     */
    private void moverPersonaje() {
        switch (hataku.getEstadoMovimiento()) {
            case INICIANDO:
                // Los bloques en el mapa son de 16x16
                // Calcula la celda donde estaría después de moverlo
                int celdaX = (int)(hataku.getX()/ TAM_CELDA);
                int celdaY = (int)((hataku.getY()+hataku.VELOCIDAD_Y)/ TAM_CELDA);
                // Recuperamos la celda en esta posición
                // La capa 0 es el fondo
                TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(1);
                TiledMapTileLayer.Cell celda = capa.getCell(celdaX, celdaY);
                // probar si la celda está ocupada
                if (celda==null) {
                    // Celda vacía, entonces el personaje puede avazar
                    hataku.caer();
                } else {
                    // Dejarlo sobre la celda que lo detiene
                    hataku.setPosicion(hataku.getX(), (celdaY+1)* TAM_CELDA);
                    hataku.setEstado(Personaje.EstadoMovimiento.QUIETO);
                }
                break;
            case MOV_DERECHA:       // Siempre se mueve
            case MOV_IZQUIERDA:
                probarChoqueParedes();      // Prueba si debe moverse
                break;
        }
        // Prueba si debe caer por llegar a un espacio vacío
        if ( hataku.getEstadoMovimiento()!= Personaje.EstadoMovimiento.INICIANDO
                && (hataku.getEstadoSalto() != Personaje.EstadoSalto.SUBIENDO) ) {
            // Calcula la celda donde estaría después de moverlo
            int celdaX = (int) ((hataku.getX()) / TAM_CELDA);
            int celdaY = (int) ((hataku.getY() + hataku.VELOCIDAD_Y) / TAM_CELDA);
            // Recuperamos la celda en esta posición
            // La capa 0 es el fondo
            TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(1);
            TiledMapTileLayer.Cell celdaAbajo = capa.getCell(celdaX, celdaY);
            TiledMapTileLayer.Cell celdaDerecha = capa.getCell(celdaX+1, celdaY);
            // probar si la celda está ocupada
            if ( celdaAbajo==null && celdaDerecha==null ) {
                // Celda vacía, entonces el personaje puede avanzar
                hataku.caer();
                hataku.setEstadoSalto(Personaje.EstadoSalto.CAIDA_LIBRE);
            } else {
                // Dejarlo sobre la celda que lo detiene
                hataku.setPosicion(hataku.getX(), (celdaY + 1) * TAM_CELDA);
                hataku.setEstadoSalto(Personaje.EstadoSalto.EN_PISO);
            }
        }
        // Saltar
        switch (hataku.getEstadoSalto()) {
            case SUBIENDO:
            case BAJANDO:
                hataku.actualizarSalto();    // Actualizar posición en 'y'
                probarChoqueParedesSalto();
                break;
        }

    }

    private void probarChoqueParedesSalto() {
        Personaje.EstadoSalto estado = hataku.getEstadoSalto();
        // Quitar porque este método sólo se llama cuando se está moviendo
        if ( estado!= Personaje.EstadoSalto.SUBIENDO){
            return;
        }
        float px = hataku.getX();    // Posición actual
        // Posición después de actualizar
        px = hataku.getEstadoMovimiento()==Personaje.EstadoMovimiento.MOV_DERECHA? px+Personaje.VELOCIDAD_X: px- Personaje.VELOCIDAD_Y;
        int celdaX = (int)(px/TAM_CELDA);   // Casilla del personaje en X
        if (hataku.getEstadoMovimiento()== Personaje.EstadoMovimiento.MOV_DERECHA) {
            celdaX++;   // Casilla del lado derecho
        }
        int celdaY = (int)(hataku.getY()/TAM_CELDA); // Casilla del personaje en Y
        TiledMapTileLayer capaPlataforma = (TiledMapTileLayer) mapa.getLayers().get(1);
        if ( capaPlataforma.getCell(celdaX,celdaY+2) != null ) { //se le suma 2 para saber si va chocar desde antes..
            // Colisionará, dejamos de moverlo
            hataku.setEstadoSalto(Personaje.EstadoSalto.BAJANDO);
        } else {
            hataku.actualizar();
        }
    }


    private void borrarPantalla() {
        //Gdx.gl.glClearColor(1, 1, 1, 1);    // Color de fondo
        //Gdx.gl.glClearColor(107 / 255f, 140f / 255, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        vista.update(width, height);
    }

    @Override
    public void pause() {

    }

    private void probarChoqueParedes() {
        Personaje.EstadoMovimiento estado = hataku.getEstadoMovimiento();
        // Quitar porque este método sólo se llama cuando se está moviendo
        if ( estado!= Personaje.EstadoMovimiento.MOV_DERECHA && estado!=Personaje.EstadoMovimiento.MOV_IZQUIERDA){
            return;
        }
        float px = hataku.getX();    // Posición actual
        // Posición después de actualizar
        px = hataku.getEstadoMovimiento()==Personaje.EstadoMovimiento.MOV_DERECHA? px+Personaje.VELOCIDAD_X:
                px- Personaje.VELOCIDAD_X;
        int celdaX = (int)(px/TAM_CELDA);   // Casilla del personaje en X
        if (hataku.getEstadoMovimiento()== Personaje.EstadoMovimiento.MOV_DERECHA) {
            celdaX++;   // Casilla del lado derecho
        }
        int celdaY = (int)(hataku.getY()/TAM_CELDA); // Casilla del personaje en Y
        TiledMapTileLayer capaPlataforma = (TiledMapTileLayer) mapa.getLayers().get(1);
        if ( capaPlataforma.getCell(celdaX,celdaY) != null ) {
            // Colisionará, dejamos de moverlo
            hataku.setEstado(Personaje.EstadoMovimiento.QUIETO);
        }
        else {
            hataku.actualizar(); //Hataku debe moverse,,
        }
    }

    /*
    Clase utilizada para manejar los eventos de touch en la pantalla
     */
    public class ProcesadorEntrada extends InputAdapter
    {
        private Vector3 coordenadas = new Vector3();
        private float x, y;     // Las coordenadas en la pantalla virtual
        private boolean banderaBotonDerecha = false, banderaBotonIzquierda = false, banderaBotonSaltar = false, banderaBotonPausa = false, banderaBotonContinue = false, banderaBotonMenu = false; //Nos sirven para saber si lo debemos de regresar de tamaño y quitarle la trasnparencia cuando han sido presionados, esto se hace  en touchUp
        private float anchoBotonPausa = btnPausa.getAncho() , altoBotonPausa = btnPausa.getAlto();

        /*
        Se ejecuta cuando el usuario pone un dedo sobre la pantalla, los dos primeros parámetros
        son las coordenadas relativas a la pantalla física (0,0) en la esquina superior izquierda
        pointer - es el número de dedo que se pone en la pantalla, el primero es 0
        button - el botón del mouse
         */
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            transformarCoordenadas(screenX, screenY);
            if (estadoJuego==EstadosJuego.JUGANDO) {
                banderaBotonTouchApretado = true;
                // Preguntar si las coordenadas están sobre el botón derecho
                if (btnDerecha.contiene(x, y) && hataku.getEstadoMovimiento() != Personaje.EstadoMovimiento.INICIANDO) {
                    // Tocó el botón derecha, hacer que el personaje se mueva a la derecha
                    btnDerecha.setAlfa(.5f);
                    btnDerecha.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON - 2); //lo hago más pequeño
                    this.banderaBotonDerecha = true; //fue presionado le boton, indico aquí que fue preisonado.
                    hataku.setEstado(Personaje.EstadoMovimiento.MOV_DERECHA);

                } else if (btnIzquierda.contiene(x, y) && hataku.getEstadoMovimiento() != Personaje.EstadoMovimiento.INICIANDO) {
                    // Tocó el botón izquierda, hacer que el personaje se mueva a la izquierda
                    btnIzquierda.setAlfa(.5f);
                    btnIzquierda.setTamanio(PantallaJuego.TAMANIO_BOTON,PantallaJuego.TAMANIO_BOTON-1); //lo hago más pequeño
                    this.banderaBotonIzquierda = true;
                    hataku.setEstado(Personaje.EstadoMovimiento.MOV_IZQUIERDA);
                } else if (btnSalto.contiene(x, y) ) {
                    // Tocó el botón saltar
                    if(Personaje.EstadoSalto.EN_PISO == hataku.getEstadoSalto()) //Para que solamente suene una vez el sonido de salto
                        efectoSaltoHataku.play(PantallaMenu.volumen);

                    btnSalto.setAlfa(.5f);
                    btnSalto.setTamanio(PantallaJuego.TAMANIO_BOTON,PantallaJuego.TAMANIO_BOTON-2); //lo hago más pequeño
                    this.banderaBotonSaltar = true;
                    hataku.saltar();
                }
                else if (btnPausa.contiene(x,y)){
                    btnPausa.setAlfa(.5f);
                    btnPausa.setTamanio(anchoBotonPausa,altoBotonPausa-2);
                    this.banderaBotonPausa = true;
                }
            }

            else if(estadoJuego == EstadosJuego.PAUSA){

                if (btnContinue.contiene(x,y)){
                    btnContinue.setAlfa(.5f);
                    btnContinue.setTamanio(anchoBotonesInteractivosPausa,altoBotonesInteractivosPausa-2);
                    this.banderaBotonContinue = true;
                }

                else if (btnMenu.contiene(x, y)){
                    btnMenu.setAlfa(.5f);
                    btnMenu.setTamanio(anchoBotonesInteractivosPausa,altoBotonesInteractivosPausa-2);
                    this.banderaBotonMenu = true;

                }

            }
            return true;    // Indica que ya procesó el evento
        }

        /*
       Se ejecuta cuando el usuario QUITA el dedo de la pantalla.
        */
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            transformarCoordenadas(screenX, screenY);
            // Preguntar si las coordenadas son de algún botón para DETENER el movimiento
            if ( hataku.getEstadoMovimiento()!= Personaje.EstadoMovimiento.INICIANDO && (btnDerecha.contiene(x, y) || btnIzquierda.contiene(x,y)) && estadoJuego == EstadosJuego.JUGANDO) {
                // Tocó el botón derecha, hacer que el personaje se mueva a la derecha
                hataku.setEstado(Personaje.EstadoMovimiento.QUIETO);

                //Ajusto tamaño y transprencia
                if(banderaBotonDerecha) {
                    btnDerecha.setAlfa(.7f);
                    btnDerecha.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON); //se rgresa a posicion original
                    banderaBotonDerecha = false;
                }
                else if(banderaBotonIzquierda) {
                    btnIzquierda.setAlfa(.7f);
                    btnIzquierda.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON); //se regresa a posicion original
                    banderaBotonIzquierda = false;
                }

                else if (banderaBotonSaltar) {
                    btnSalto.setAlfa(.7f);
                    btnSalto.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON); //se regresa a posicion orignal
                    banderaBotonSaltar = false;
                }

            }

            if( hataku.getEstadoMovimiento()!= Personaje.EstadoMovimiento.INICIANDO && btnSalto.contiene(x,y)  && estadoJuego == EstadosJuego.JUGANDO){
                if (banderaBotonSaltar) {
                    btnSalto.setAlfa(.7f);
                    btnSalto.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON); //se regresa a posicion orignal
                    banderaBotonSaltar = false;
                }
            }

            if(banderaBotonPausa && estadoJuego == EstadosJuego.JUGANDO){
                btnPausa.setAlfa(.7f);
                btnPausa.setTamanio(anchoBotonPausa, altoBotonPausa); //se regresa a posicion orignal
                efectoTomarPergamino.play(PantallaMenu.volumen);
                pausa();
                banderaBotonPausa = false;
            }

            if(btnContinue.contiene(x,y) && banderaBotonContinue && estadoJuego == EstadosJuego.PAUSA){
                btnContinue.setAlfa(.7f);
                btnContinue.setTamanio(anchoBotonesInteractivosPausa, altoBotonesInteractivosPausa); //se regresa a posicion orignal
                efectoTomarPergamino.play(PantallaMenu.volumen);
                pausa();
                banderaBotonContinue = false;
            }
            else{
                btnContinue.setAlfa(.7f);
                btnContinue.setTamanio(anchoBotonesInteractivosPausa, altoBotonesInteractivosPausa); //se regresa a posicion orignal
            }

            if(btnMenu.contiene(x,y) &&  banderaBotonMenu && estadoJuego == EstadosJuego.PAUSA){
                btnMenu.setAlfa(.7f);
                btnMenu.setTamanio(anchoBotonesInteractivosPausa, altoBotonesInteractivosPausa); //se regresa a posicion orignal
                efectoTomarPergamino.play(PantallaMenu.volumen);
                plataforma.setScreen(new PantallaMenu(plataforma, true));  //nos regresa al menu principal.
                banderaBotonContinue = false;
            }
            else{
                btnMenu.setAlfa(.7f);
                btnMenu.setTamanio(anchoBotonesInteractivosPausa, altoBotonesInteractivosPausa); //se regresa a posicion orignal
            }




            return true;    // Indica que ya procesó el evento
        }

        // Se ejecuta cuando el usuario MUEVE el dedo sobre la pantalla
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            transformarCoordenadas(screenX, screenY);
            // Acaba de salir de las fechas (y no es el botón de salto)
            if (x<Principal.ANCHO_CAMARA/2 && hataku.getEstadoMovimiento()!= Personaje.EstadoMovimiento.QUIETO) {
                if (!btnIzquierda.contiene(x, y) && !btnDerecha.contiene(x, y) ) {
                    hataku.setEstado(Personaje.EstadoMovimiento.QUIETO);
                    //Ajusto tamaño y transprencia
                    if(banderaBotonDerecha) {
                        btnDerecha.setAlfa(.7f);
                        btnDerecha.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON); //se rgresa a posicion original
                        banderaBotonDerecha = false;
                    }
                    if(banderaBotonIzquierda) {
                        btnIzquierda.setAlfa(.7f);
                        btnIzquierda.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON); //se regresa a posicion original
                        banderaBotonIzquierda = false;
                    }
                }
            }
            else {
                if (!btnSalto.contiene(x,y)){
                    if (banderaBotonSaltar) {
                        btnSalto.setAlfa(.7f);
                        btnSalto.setTamanio(PantallaJuego.TAMANIO_BOTON, PantallaJuego.TAMANIO_BOTON); //se regresa a posicion orignal
                        banderaBotonSaltar = false;
                    }
                }
            }
            return true;
        }

        private void transformarCoordenadas(int screenX, int screenY) {
            // Transformar las coordenadas de la pantalla física a la cámara HUD
            coordenadas.set(screenX, screenY, 0);
            camaraHUD.unproject(coordenadas);
            // Obtiene las coordenadas relativas a la pantalla virtual
            x = coordenadas.x;
            y = coordenadas.y;
        }
    }

    private void pausa() {
        if(this.estadoJuego == EstadosJuego.JUGANDO) {
            this.fondoPausa.setPosicion(this.textoMarcadorVidas.getX() + 20, 0);
            //Cuando hay una pausa se crean los botones de menu y continue...


            btnContinue.setPosicion(this.textoMarcadorVidas.getX()+110, 125);
            btnContinue.setAlfa(0.7f); // Un poco de transparencia
            //el boton debe hacerse pequeño
            btnContinue.setTamanio(this.anchoBotonesInteractivosPausa, this.altoBotonesInteractivosPausa);


            btnMenu.setPosicion(this.textoMarcadorVidas.getX() + 110, 5);
            btnMenu.setAlfa(0.7f);
            btnMenu.setTamanio(this.anchoBotonesInteractivosPausa, this.altoBotonesInteractivosPausa);

            this.estadoJuego = EstadosJuego.PAUSA;

        }
        else{
            this.estadoJuego = EstadosJuego.JUGANDO;

        }
    }

    //Se ejecutan de manera automatica cuando nos movemos de pantalla...
    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    // Libera los assets
    @Override
    public void dispose() {

        this.plataforma.dispose();
        this.batch.dispose();
        this.mapa.dispose();
        this.texturaFondo.dispose();
        this.rendererMapa.dispose();

        //texturas
        this.texturaHataku.dispose();
        this.texturaBtnDerecha.dispose();
        this.texturaBtnIzquierda.dispose();
        this.texturaPausa.dispose();
        this.texturaFondoPausa.dispose();
        this.texturaContinue.dispose();
        this.texturaMenu.dispose();
        this.texturaSalto.dispose();
        this.texturaVidas.dispose();
        this.texturaPocion.dispose();
        this.texturaScroll.dispose();
        this.texturaVidas.dispose();
        this.texturaEN1.dispose();
        this.texturaTemplo.dispose();
        this.texturaAtaque.dispose();

        //sonidos (efectos)
        this.efectoSaltoHataku.dispose();
        this.efectoTomarVida.dispose();
        this.efectoTomarPergamino.dispose();
        this.efectoDanio.dispose();
        this.efectoPuertaTemplo.dispose();
    }


    public enum EstadosJuego {
        JUGANDO,
        PAUSA
    }


}
