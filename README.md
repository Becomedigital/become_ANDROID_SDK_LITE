# Documentación de Become Android SDK
Este es un espacio para conocer a cerca del SDK Android de Become para la validación de identidad.
<p align="center">
  <img src="https://github.com/Becomedigital/become_ANDROID_SDK/blob/master/Pantalla_Android.png" width="284" height="572">
</p>

## Configuraciones de Gradle

 1. Dentro del archivo build.gradle debe agregar las siguientes modificaciones:

	         defaultConfig {
	           minSdkVersion 21
	         }
		 
		 android {
		    compileOptions {
		        sourceCompatibility = 1.8
			targetCompatibility = 1.8
		     }
		}
		
2. El archivo build.grade debe contar con una referencia al repositorio:
	
		maven { url 'https://jitpack.io' }

<p align="center">
  <img src="https://github.com/Becomedigital/become_ANDROID_SDK/blob/master/build_gradle.png">
</p>

### Implementación de módulos requeridos
Cómo primera medida es necesaria la implementacion de los siguientes módulos:

    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.1'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'androidx.navigation:navigation-fragment:2.3.0'
    implementation 'androidx.navigation:navigation-ui:2.3.0'
    implementation 'com.github.bumptech.glide:glide:4.10.0'
    implementation 'com.squareup.okhttp3:okhttp:4.2.2'
  
### Implementación de la SDK Become
       
 1. Abra el archivo build.gradle dentro del directorio del módulo de su aplicación e incluya las siguientes dependencias en el archivo build.gradle de su aplicación:
 
		 implementation 'com.github.Becomedigital:become_ANDROID_SDK:LATEST_VERSION'
		 
	Ejemplo:
		 
		 implementation 'com.github.Becomedigital:become_ANDROID_SDK:3.1.2'
		 
 3. Al realizar los pasos anteriores, debe sincronizar su proyecto con gradle.
 
 ## Inicialización de la SDK
En el método onCreate () de su clase de aplicación, inicialice Become utilizando el siguiente fragmento de código:

	public class MainActivity extends AppCompatActivity {
    
    //Con el fin de manejar las respuestas de inicio de sesión, debe crear un callback utilizando el siguiente fragmento de código
    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew ( );

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate (savedInstanceState);
	    setContentView (R.layout.activity_main);

        //Parámetros de configuración: El valor de los parámetros debe ser solicitado al contratar el servicio
        String validatiopnTypes =  "PASSPORT/LICENSE/DNI/VIDEO" ;  
        String clientSecret =  "your client Secret here" ;  
        String clientId =  "your client ID here" ;  
        String contractId =  "your contract ID here";
        String userId = "your user ID here"
	
        //Instancia para iniciar la interfaz
        BecomeResponseManager.getInstance ( ).startAutentication (MainActivity.this,  
            new BDIVConfig (clientId,  
                    clientSecret,  
                    contractId,  
                    validatiopnTypes,  
                    true,  
                    null,
		    userId
            ));
	  }
	}

## Posibles Errores

**1. Error con parámetros vacíos** 
Los siguientes parámetros son necesarios para la activación de la SDK por lo tanto su valor no debe ser vacío.
 
Parámetro | Valor
------------ | -------------
validatiopnTypes | ""
clientSecret | ""
clientId | ""
contractId  | ""
userID  | ""


Mostrará el siguiente error por consola:

    parameters cannot be empty

**2. Video cómo único parámetro**
El atributo ***validationTypes*** no puede contener como único valor el parámetro ***VIDEO***

    validationTypes: "VIDEO"

Mostrará el siguiente error por consola:

    the process cannot be initialized with video only

## Validación del proceso
En este apartado encontrará la respuesta a partir de la validación del proceso realizado por la SDK y las estructuras internas que contienen los atributos encargados de capturar la información dada por el usuario:

**1. Estructura encargada de la definición del estado  de validación ***exitoso***:**

	    @Override
        public void onSuccess(final ResponseIV responseIV) {  
            String id = responseIV.getId ( );
            String created_at = responseIV.getCreated_at ( );
            String company = responseIV.getCompany ( );
            String fullname = responseIV.getFullname ( );
            String birth = responseIV.getBirth ( );
            String document_type = responseIV.getDocument_type ( );
            String document_number = responseIV.getDocument_number ( );
            Boolean face_match = responseIV.getFace_match ( );
            Boolean template = responseIV.getTemplate ( );
            Boolean alteration = responseIV.getAlteration ( );
            Boolean watch_list = responseIV.getWatch_list ( );
            String comply_advantage_result = responseIV.getComply_advantage_result ( );
            String comply_advantage_url = responseIV.getComply_advantage_url ( );
            String verification_status = responseIV.getVerification_status ( );
            String message = responseIV.getMessage ( );
            Integer responseStatus = responseIV.getResponseStatus ( );
            String textFinal = "id: "  +
                    "\ncreated_at: " + created_at +
                    "\ncompany: " + company +
                    "\nfullname: " + fullname +
                    "\nbirth: " + birth +
                    "\ndocument_type: " + document_type +
                    "\ndocument_number: " + document_number +
                    "\nface_match: " + face_match +
                    "\ntemplate: " + template +
                    "\nalteration: " + alteration +
                    "\nwatch_list: " + watch_list +
                    "\ncomply_advantage_result: " + comply_advantage_result +
                    "\ncomply_advantage_url: " + comply_advantage_url +
                    "\nverification_status: " + verification_status +
                    "\nmessage: " + message +
                    "\nresponseStatus: " + responseStatus;

            Log.d ("responseIV", textFinal);
        }
	
**2. Estructura encargada de la definición del estado  de validación ***cancelado*** por el usuario:**

	@Override  
	public void onCancel() { 
	    textResponse.setText ("Cancelado por el usuario ");  
	}
   
**3. Estructura encargada de la definición del estado  de validación ***error*** , este estado se presenta cuándo ocurren errores generales o de inicialización de parámetros :**

	 @Override  
	 public void onError(LoginError pLoginError) {
	    Log.d ("Error", pLoginError.getMessage ( ));
	 }

## Estructura para el retorno de la información
Los siguientes son los parámetros que permiten el retorno de la información capturada por el sistema.

	    String id;
	    String created_at;
	    String company;
	    String fullname;
	    String birth;
	    String document_type;
	    String document_number;
	    Boolean face_match;
	    Boolean template;
	    Boolean alteration;
	    Boolean watch_list;
	    String comply_advantage_result;
	    String comply_advantage_url;
	    String verification_status;
	    String device_model;
	    String os_version;
	    String browser_major;
	    String browser_version;
	    String ua;
	    String device_type;
	    String device_vendor;
	    String os_name;
	    String browser_name;
	    String issuePlace;
	    String emissionDate;
	    String ageRange;
	    Integer savingAccountsCount;
	    Integer financialIndustryDebtsCount;
	    Integer solidarityIndustryDebtsCount;
	    Integer serviceIndustryDebtsCount;
	    Integer commercialIndustryDebtsCount;
	    String ip;
	    String frontImgUrl;
	    String backImgUrl;
	    String selfiImageUrl;
	    String message;
	    Integer responseStatus;

Ejemplo de la respuesta:

	public void onSuccess(final ResponseIV responseIV) {  
	  String id = responseIV.getId ( );  
		String created_at = responseIV.getCreated_at ( );  
		String company = responseIV.getCompany ( );  
		String fullname = responseIV.getFullname ( );  
		String birth = responseIV.getBirth ( );  
		String document_type = responseIV.getDocument_type ( );  
		String document_number = responseIV.getDocument_number ( );  
		Boolean face_match = responseIV.getFace_match ( );  
		Boolean template = responseIV.getTemplate ( );  
		Boolean alteration = responseIV.getAlteration ( );  
		Boolean watch_list = responseIV.getWatch_list ( );  
		String comply_advantage_result = responseIV.getComply_advantage_result ( );  
		String comply_advantage_url = responseIV.getComply_advantage_url ( );  
		String verification_status = responseIV.getVerification_status ( );  
		String message = responseIV.getMessage ( );  
		Integer responseStatus = responseIV.getResponseStatus ( );  
		String textFinal = "id: " +  
             "\ncreated_at: " + created_at +  
             "\ncompany: " + company +  
             "\nfullname: " + fullname +  
             "\nbirth: " + birth +  
             "\ndocument_type: " + document_type +  
             "\ndocument_number: " + document_number +  
             "\nface_match: " + face_match +  
             "\ntemplate: " + template +  
             "\nalteration: " + alteration +  
             "\nwatch_list: " + watch_list +  
             "\ncomply_advantage_result: " + comply_advantage_result +  
             "\ncomply_advantage_url: " + comply_advantage_url +  
             "\nverification_status: " + verification_status +  
             "\nmessage: " + message +  
             "\nresponseStatus: " + responseStatus;  
  
	  textResponse.setText (textFinal);  
	  Log.d ("responseIV", textFinal);  
	}

## Implementación del proceso
Esta sección se encarga de proporcionar el fragmento de código para la implementación final del proceso.

	public class MainActivity extends AppCompatActivity {  
          	    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew ( );  
        	  
        	  @SuppressLint("WrongThread")  
        	  @Override  
        	  protected void onCreate(Bundle savedInstanceState) {  
        	        super.onCreate (savedInstanceState);  
        	        setContentView (R.layout.activity_main);  
        	  
        	  Bitmap decodeResource = BitmapFactory.decodeResource (getResources ( ), R.drawable.become_icon);  
        	  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ( );  
        	  
        	  decodeResource.compress (Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);  
        	  byte[] byteArray = byteArrayOutputStream.toByteArray ( );  
        	  
        	  TextView textResponse = findViewById (R.id.textReponse);  
        	  EditText textClientSecret = findViewById (R.id.cliensecretText);  
        	  EditText textClientId = findViewById (R.id.clienidText);  
        	  EditText textContractId = findViewById (R.id.ContractIdText);  
        	  EditText textVlidationType = findViewById (R.id.validationType);  
        	  Button btnAut = findViewById (R.id.btnAuth);  
        	  btnAut.setOnClickListener (view -> {  
        	  String validatiopnTypes = textVlidationType.getText ( ).toString ( ).isEmpty ( ) ? "VIDEO" : textVlidationType.getText ( ).toString ( );  
        	  String clientSecret = textClientSecret.getText ( ).toString ( ).isEmpty ( ) ? "FKLDM63GPH89TISBXNZ4YJUE57WRQA25" : textClientSecret.getText ( ).toString ( );  
        	  String clientId = textClientId.getText ( ).toString ( ).isEmpty ( ) ? "acc_demo" : textClientId.getText ( ).toString ( );  
        	  String contractId = textContractId.getText ( ).toString ( ).isEmpty ( ) ? "2" : textContractId.getText ( ).toString ( );  
        	  Date currentTime = Calendar.getInstance().getTime();
		  SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
		  String inActiveDate = format1.format(currentTime); // identificador de la transacción // identificador de la transacción
        	  
		  BecomeResponseManager.getInstance ( ).startAutentication (MainActivity.this,  
        	      new BDIVConfig (clientId,  
        	      clientSecret,  
        	      contractId,  
        	      validatiopnTypes,  
        	      true,  
        	      byteArray,
		      inActiveDate
        	  ));  
            
        	  BecomeResponseManager.getInstance ( ).registerCallback (mCallbackManager, new BecomeInterfaseCallback ( ) {  
        	  
		  @Override  	  
              public void onSuccess(final ResponseIV responseIV) {  
        	  // Respuesta SDK
	      }  
        	  
              @Override  
              public void onCancel() {  
                  // Respuesta cancelado por usuario
              }  
        	  
              @Override  
              public void onError(LoginError pLoginError) {  
                  // Respuesta error en el proceso
              }        
          });  	  
        }  
      }

## Requerimientos

* **Tecnologias**
	
	Android 5 en adelante
	
## Vídeo de integración del SDK Become para Android
[![Become](https://github.com/Becomedigital/become_ANDROID_SDK/blob/master/Video.png)](https://www.youtube.com/watch?v=ggpLGHeaFSg&ab_channel=BecomeDigital)

