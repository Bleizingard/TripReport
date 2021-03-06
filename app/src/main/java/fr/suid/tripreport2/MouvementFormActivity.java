package fr.suid.tripreport2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import DAO.AeroportDAO;
import DAO.AvionDAO;
import DAO.MouvementDAO;
import aeroplan.Aeroport;
import aeroplan.Avion;
import aeroplan.Mouvement;

public class MouvementFormActivity extends AppCompatActivity
{
	private static final String TAG = "Sample";

	private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

	private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";

	//editText Date & Heure Départ/Arrivée
	private TextView textViewHD;
	private TextView textViewHA;

	//editText
	private EditText editTextNVol;
	private EditText editTextDistance;
	private EditText editTextNbPassagers;
	private EditText editTextDureeVol;

	//Widget
	private Spinner spinnerAvion;
	private Spinner spinnerAeroportDepart;
	private Spinner spinnerAeroportArrivee;
	private Switch switchEstIntracom;

	//Buton de Sauvegarde
	private Button saveButton;

	private SwitchDateTimeDialogFragment dateTimeFragmentHD;
	private SwitchDateTimeDialogFragment dateTimeFragmentHA;

	private ArrayList<Avion> avionArrayList;
	private ArrayList<Aeroport> aeroportArrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouvement_form);

		textViewHD = (TextView) findViewById(R.id.editTextDateHeureDepart);
		textViewHA = (TextView) findViewById(R.id.editTextDateHeureArrivee);
		editTextDistance = findViewById(R.id.editTextDistance);
		editTextDureeVol = findViewById(R.id.editTextDureeVol);
		editTextNbPassagers = findViewById(R.id.editTextNbPassager);
		editTextNVol = findViewById(R.id.editTextNumeroVol);
		spinnerAvion = findViewById(R.id.spinnerAvion);
		spinnerAeroportDepart = findViewById(R.id.spinnerAeroportDepart);
		spinnerAeroportArrivee = findViewById(R.id.spinnerAeroportArrivee);
		switchEstIntracom = findViewById(R.id.switchIntracom);

		saveButton = findViewById(R.id.button);

		//Convertissement des listes
		try
		{
			avionArrayList = new AvionDAO(this).getAll();
			ArrayList<String> list = new ArrayList<String>();
			for (Avion avion: avionArrayList)
			{
				list.add(avion.getCodeInterne());
			}
			spinnerAvion.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list));

			 aeroportArrayList = new AeroportDAO(this).getAll();
			list = new ArrayList<String>();
			for (Aeroport aeroport: aeroportArrayList)
			{
				list.add(aeroport.getOaci());
			}
			spinnerAeroportDepart.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list));
			spinnerAeroportArrivee.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list));

		} catch (Exception e)
		{
			e.printStackTrace();
		}


		if (savedInstanceState != null)
		{
			// Restore value from saved state
			textViewHD.setText(savedInstanceState.getCharSequence(STATE_TEXTVIEW));
			textViewHA.setText(savedInstanceState.getCharSequence(STATE_TEXTVIEW));
		}

		// Construct SwitchDateTimePicker
		dateTimeFragmentHD = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
		dateTimeFragmentHA = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);

		if (dateTimeFragmentHD == null)
		{
			dateTimeFragmentHD = SwitchDateTimeDialogFragment.newInstance(
					getString(R.string.label_datetime_dialog),
					getString(android.R.string.ok),
					getString(android.R.string.cancel)
					//getString(R.string.clean) // Optional
			);

		}

		if (dateTimeFragmentHA == null)
		{
			dateTimeFragmentHA = SwitchDateTimeDialogFragment.newInstance(
					getString(R.string.label_datetime_dialog),
					getString(android.R.string.ok),
					getString(android.R.string.cancel)
					//getString(R.string.clean) // Optional
			);

		}

		// Init format
		final SimpleDateFormat myDateFormat = new SimpleDateFormat("d/MM/yyyy HH:mm", java.util.Locale.getDefault());
		// Assign unmodifiable values
		dateTimeFragmentHA.set24HoursMode(true);
		dateTimeFragmentHA.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
		dateTimeFragmentHA.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

		dateTimeFragmentHD.set24HoursMode(true);
		dateTimeFragmentHD.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
		dateTimeFragmentHD.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

		// Define new day and month format
		try
		{
			dateTimeFragmentHA.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
			dateTimeFragmentHD.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
		} catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e)
		{
			Log.e(TAG, e.getMessage());
		}

		// Set listener for date
		// Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
		dateTimeFragmentHA.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener()
		{
			@Override
			public void onPositiveButtonClick(Date date)
			{
				textViewHA.setText(myDateFormat.format(date));
			}

			@Override
			public void onNegativeButtonClick(Date date)
			{
				// Do nothing
			}

			@Override
			public void onNeutralButtonClick(Date date)
			{
				// Optional if neutral button does'nt exists
				textViewHA.setText("");
			}
		});

		dateTimeFragmentHD.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener()
		{
			@Override
			public void onPositiveButtonClick(Date date)
			{
				textViewHD.setText(myDateFormat.format(date));
			}

			@Override
			public void onNegativeButtonClick(Date date)
			{
				// Do nothing
			}

			@Override
			public void onNeutralButtonClick(Date date)
			{
				// Optional if neutral button does'nt exists
				textViewHD.setText("");
			}
		});

		textViewHD.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Calendar now = Calendar.getInstance();

				// Re-init each time
				dateTimeFragmentHD.startAtCalendarView();
				dateTimeFragmentHD.setDefaultDateTime(new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)).getTime());
				dateTimeFragmentHD.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
			}
		});

		textViewHA.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Calendar now = Calendar.getInstance();

				// Re-init each time
				dateTimeFragmentHA.startAtCalendarView();
				dateTimeFragmentHA.setDefaultDateTime(new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)).getTime());
				dateTimeFragmentHA.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String toastMessage = null;

				//Vérification que les champs soient remplies
				if(editTextNVol.getText().length() == 0)
				{
					editTextNVol.requestFocusFromTouch();

					toastMessage = getString(R.string.numero_vol);
				}
				else if(editTextDistance.getText().length() == 0)
				{
					editTextDistance.requestFocusFromTouch();

					toastMessage = getString(R.string.distance);
				}
				else if(editTextNbPassagers.getText().length() == 0)
				{
					editTextNbPassagers.requestFocusFromTouch();

					toastMessage = getString(R.string.nb_passager);
				}
				else if(textViewHD.length() == 0)
				{
					textViewHD.requestFocusFromTouch();

					toastMessage = getString(R.string.date_heure_depart);
				}
				else if(textViewHA.length() == 0)
				{
					textViewHA.requestFocusFromTouch();

					toastMessage = getString(R.string.date_heure_arrivee);
				}
				else if(editTextDureeVol.getText().length() == 0)
				{
					editTextDureeVol.requestFocusFromTouch();

					toastMessage = getString(R.string.duree_vol);

				}


				//Envoie du Toast
				if(toastMessage != null)
				{
					Toast t = Toast.makeText(getApplicationContext(), getString(R.string.toast_champs) + " " + toastMessage + " " + getString(R.string.toast_not_empty), Toast.LENGTH_LONG);
					t.show();
				}
				else
				{
					//Préparation des données
					String NVol = editTextNVol.getText().toString();
					int dureeVol = Integer.parseInt(editTextDureeVol.getText().toString());
					int distance = Integer.parseInt(editTextDistance.getText().toString());
					int nbPassager = Integer.parseInt(editTextNbPassagers.getText().toString());
					boolean estIntracom = false;
					Calendar cDepart = Calendar.getInstance();
					Calendar cArrivee = Calendar.getInstance();

					Avion avion = avionArrayList.get((int) spinnerAvion.getSelectedItemId());
					Aeroport aeroportDepart = aeroportArrayList.get((int) spinnerAeroportDepart.getSelectedItemId());
					Aeroport aeroportArrivee = aeroportArrayList.get((int) spinnerAeroportArrivee.getSelectedItemId());

					cDepart.set(dateTimeFragmentHD.getYear(), dateTimeFragmentHD.getMonth(), dateTimeFragmentHD.getDay(), dateTimeFragmentHD.getHourOfDay(), dateTimeFragmentHD.getMinute());
					cArrivee.set(dateTimeFragmentHA.getYear(), dateTimeFragmentHA.getMonth(), dateTimeFragmentHA.getDay(), dateTimeFragmentHA.getHourOfDay(), dateTimeFragmentHA.getMinute());

					//Enregistrement du mouvement
					Mouvement mouvement = new Mouvement(0, NVol, distance, cDepart, cArrivee, dureeVol, estIntracom, nbPassager, avion, aeroportDepart, aeroportArrivee);

					try
					{
						new MouvementDAO(getApplicationContext()).add(mouvement);

						setResult(1, null);
						//finish must be declared here to send the result to parent activity
						finish();

					} catch (Exception e)
					{
						Toast t = Toast.makeText(getApplicationContext(), getString(R.string.erreur_maj_SQLite), Toast.LENGTH_LONG);
						t.show();
					}

				}

			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		// Save the current textView
		savedInstanceState.putCharSequence(STATE_TEXTVIEW, textViewHA.getText());
		super.onSaveInstanceState(savedInstanceState);
	}
}
