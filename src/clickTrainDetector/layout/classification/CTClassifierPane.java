package clickTrainDetector.layout.classification;


import clickTrainDetector.ClickTrainControl;
import clickTrainDetector.classification.CTClassifier;
import clickTrainDetector.classification.CTClassifierParams;
import clickTrainDetector.classification.CTClassifierType;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import pamViewFX.PamGuiManagerFX;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamHBox;
import pamViewFX.fxNodes.PamSpinner;
import pamViewFX.fxNodes.PamVBox;

/**
 * The basic classifier pane. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class CTClassifierPane extends PamBorderPane {

	/**
	 * The classifier type combo box. 
	 */
	private ComboBox<String> classifierListBox;

	/**
	 * Reference to the click train control. 
	 */
	private ClickTrainControl clickTrainControl;

	/**
	 * Settings holder. 
	 */
	private PamBorderPane settingsHolder;

	/**
	 * The current classifier. 
	 */
	private CTClassifier currentClassifier;

	private CTClassifierType[] ctClassifierTypes;

	private TextField nameField;

	private PamSpinner<Integer> speciesIDSpinner;
	
	/**
	 * The default species to set 
	 */
	private int defaultSpeciesID = 1; 


	/**
	 * Constructor for the classifier pane. 
	 * @param i - the default species ID
	 */
	public CTClassifierPane(ClickTrainControl clickTrainControl) {
		this.clickTrainControl=clickTrainControl; 
		this.setCenter(createClassifierPane()); 
	}


	/**
	 * Create the classifier pane. 
	 * @return the classifier pane
	 */
	private Pane createClassifierPane() {

		PamVBox mainHolder = new PamVBox(); 
		mainHolder.setSpacing(5);

		//Name and type
		Label label1 = new Label("Classifier Info"); 
//		label1.setFont(PamGuiManagerFX.titleFontSize2);
		PamGuiManagerFX.titleFont2style(label1);


		nameField = new TextField(); 
		nameField.setPrefColumnCount(20);

		speciesIDSpinner = new PamSpinner<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE,1, 1);
		speciesIDSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		speciesIDSpinner.setPrefWidth(75);

		PamHBox holder = new PamHBox(); 
		holder.setSpacing(5);
		holder.getChildren().addAll(new Label("Name"), nameField, new Label("ID"), speciesIDSpinner);
		holder.setAlignment(Pos.CENTER_LEFT);

		//classifier types
		Label label2 = new Label("Classifier Type"); 
//		label2.setFont(PamGuiManagerFX.titleFontSize2);
		PamGuiManagerFX.titleFont2style(label2);


		ctClassifierTypes = CTClassifierType.values();

		classifierListBox = new ComboBox<String>(); 
		for (int i=0; i<ctClassifierTypes.length; i++) {
			classifierListBox.getItems().add(clickTrainControl.getClassifierManager().getClassifierName(ctClassifierTypes[i])); 
		}
		classifierListBox.setOnAction((action)->{
			setClassifierPane(classifierListBox.getSelectionModel().getSelectedIndex()); 
		});
		classifierListBox.getSelectionModel().select(0);

		classifierListBox.setPrefWidth(Double.MAX_VALUE);

		settingsHolder = new PamBorderPane(); 

		//set the current classifier pane. 
		setClassifierPane(0);

		mainHolder.getChildren().addAll(label1, holder, label2, classifierListBox,  settingsHolder);

		return mainHolder; 
	}

	/**
	 * Get parameters for the current classifier
	 * @return the current classifier. 
	 */
	public CTClassifierParams getParams() {
		
		CTClassifierParams paramsOut =  currentClassifier.getCTClassifierGraphics().getParams(); 
		
		paramsOut.classifierName=this.nameField.getText(); 
				
		paramsOut.speciesFlag= this.speciesIDSpinner.getValue(); 
		
		return paramsOut; 
	}

	/**
	 * Set classifier parameters for the pane
	 * @param params - the parameters to set.
	 */
	public void setParams(CTClassifierParams params) {		
		//probably a more elegent way to do this.
		int index =-1; 
		for (int i=0; i<ctClassifierTypes.length; i++) {
			if (ctClassifierTypes[i]==params.type) {
				index=i;
				break; 
			}
		}
		classifierListBox.getSelectionModel().select(index);
		setClassifierPane(index);
		
		//species Id
		setBasicParams(params);
		
		//		//when setting parameters have to figure out what pane to create first!
		//		currentClassifier = clickTrainControl.getClassifierManager().createClassifier(params.type); 
		//		//Classifier pane
		//		settingsHolder.setCenter(currentClassifier.getCTClassifierGraphics().getCTClassifierPane());
		currentClassifier.getCTClassifierGraphics().setParams(params); 

	}
	
	
	private void setBasicParams(CTClassifierParams params) {
		//species Id
		speciesIDSpinner.getValueFactory().setValue(params.speciesFlag);
		nameField.setText(params.classifierName);
	}

	/**
	 * Set the classifier pane. 
	 * @param clssfrIndex - set the index. 
	 */
	private void setClassifierPane(int clssfrIndex) {
		currentClassifier = clickTrainControl.getClassifierManager().createClassifier(clssfrIndex); 
		//Classifier pane- this also sets parameters for the classifier specific pane
		if (currentClassifier.getCTClassifierGraphics()!=null) {
			settingsHolder.setCenter(currentClassifier.getCTClassifierGraphics().getCTClassifierPane());		
		}
		else {
			settingsHolder.setCenter(null);
		}
		
//		int defaultSpeciesID = 1;
//		if (this.clickTrainControl.getClickTrainParams().ctClassifierParams!=null) {
//			defaultSpeciesID=this.clickTrainControl.getClickTrainParams().ctClassifierParams.length; 
//			//System.out.println("Hello: defaultSpeciesID: " + defaultSpeciesID); 
//		}
		//setBasicParams(currentClassifier.getCTClassifierGraphics().getParams()); //bit messy but sets the paramters properly
	}
	
	/**
	 * Get the default species ID
	 * @return the default species ID
	 */
	public int getDefaultSpeciesID() {
		return defaultSpeciesID;
	}

	/**
	 * Set the default species ID. Changes the spinner to this value
	 * @param defaultSpeciesID
	 */
	public void setDefaultSpeciesID(int defaultSpeciesID) {
		this.defaultSpeciesID = defaultSpeciesID;
		speciesIDSpinner.getValueFactory().setValue(defaultSpeciesID);
	}


	/**
	 * The name field for the classifier. 
	 * @return the name field for the classifier. 
	 */
	public TextField getNameField() {
		return this.nameField; 
	}


}
