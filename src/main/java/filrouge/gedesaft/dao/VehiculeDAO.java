package filrouge.gedesaft.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import filrouge.gedesaft.model.AffichageModel;
import filrouge.gedesaft.model.VehiculeModel;

//	L'annotation @Repository sert à donner à la classe decoree la configuration de la base de donnees.
//	Cette configuration est donnees dans le fichier application.properties
//	qu'on trouve dans le dossier src/main/resources dans le projet
//	La classe implemente l'interface DataInterfaceDAO qui donne le modele des classe DAO
//	Que l'on va faire dans l'application.
@Repository
public class VehiculeDAO implements DataInterfaceDAO {
	
//	Cette ligne va mettre dans un fichier .log tous les log qui seront genérés par cette classe
//	La configuration de la classe Logger log se trouve egalement dans le fichier application.properties
	private final Logger log = LoggerFactory.getLogger(this.getClass());
//	Attribut de type inteface Datasource, qui représente matérialise la connection avec la base de donnees
	private DataSource datasource;

//	Constructeur de la classe VehiculeDAO avec la configuration de base de donnée définie via
//	l'annotation @Repository.
//	Tout classe decoree par @Repository se construit avec en paramètre
//	une instance de la classe JdbcTemplate.
//	JdbcTemplate est la classe Springboot qui gère toute la liaison entre l'application et la base de donnees
//	On instancie datasource (la connection avec la base de donnees)
//	avec la methode getDataSource() de la classe JdbcTemplate.
	@Autowired
	public VehiculeDAO(JdbcTemplate jdbcTemplate) {
		this.datasource = jdbcTemplate.getDataSource();
	}

//	Cette methode sert a construire une liste de couple id_vehicule/type/vehicule (classe AffichageModel)
//	concernant tous les vehicules qui sont lies a une affaire a laquelle est liee l'utilisateur.
//	la methode a besoin de l'id de l'utilisateur comme parametre.
	/**
	 * @param id_utilisateur
	 * @return
	 * @throws Exception
	 */
	public List<AffichageModel> listAffichageVehicule(Long id_utilisateur) throws Exception {
//	On declare la variable listAffichageVehicule, a laquelle la methode va donner une valeur
		List <AffichageModel> listAffichageVehicule;
//	On initialise les variables qui seront les parametres de la methode listAffichage():
//	idRecherche : qui est l'id par rapport auquel on fait la recherche (ici, l'id_utilisateur),
//	c'est la veleur que prendra le ? de la requete.
//	id_colonne et name_colonne sont les noms des colonnes dont les valeurs permettront
//	de construire les instances de la classe AffichageModel (les couples id/noms affiches dans les boutons)
//	request est la requete qui permet de faire la recherche.
		Long idRecherche = id_utilisateur;
		String id_colonne = "id_vehicule";
		String name_colonne = "typeVehicule";
		String request = "SELECT DISTINCT vehicule.id_vehicule, vehicule.typeVehicule FROM vehicule"
				+ " JOIN affaire_has_vehicule ON vehicule.id_vehicule = affaire_has_vehicule.id_vehicule"
				+ " JOIN affaire ON affaire_has_vehicule.id_affaire = affaire.id_affaire"
				+ " JOIN affaire_has_protagonniste ON affaire.id_affaire = affaire_has_protagonniste.id_affaire"
				+ " JOIN protagonniste ON affaire_has_protagonniste.id_protagonniste = protagonniste.id_protagonniste"
				+ " WHERE protagonniste.id_Protagonniste = ? ";
//	On donne a la variable listAfichageVehicule la valeur produite par la methode listAffichage()
//	listAffichage() est la liste qui construit de façon generique une liste d'instances de AffichageModel
//	elle a besoin de 4 parametres, qui ont ete initialises ci-dessus.
		listAffichageVehicule = listAffichage(idRecherche, id_colonne, name_colonne, request);

//	On retourne la liste que l'on a construite.
		return listAffichageVehicule;
	}	
	
//	Methode qui va permettre de creer une liste d'instances de la classe AffichageModel
//	c'est à dire qui va renvoyer le couple id_:donnee affichee dans le bouton des objets
//	dont on veut afficher une liste.
//	Cette methode a besoin de 4 paramteres:
//	idRecherche, qui est l'id par rapport auquel on veut faire une recherche
//	(si on veut par exemple, la liste des empreintes liees a un vehicule donne, on met l'id_vehicule);
//	name_colonne, c'est le nom de la colonne dont on veut afficher le contenu dans la liste de boutons;
//	id_colonne, c'est le nom d ela coloone ou se trouvent les id qui seront associes aux boutons de la liste;
//	request, c'est la requete sql qui permettra e trouver toutes les ojets qui devrnt etres listes.
	/**
	 * @param id_utilisateur
	 * @return
	 * @throws Exception
	 */
	public List<AffichageModel> listAffichage(Long idRecherche, String id_colonne, String name_colonne, String request) throws Exception {
//	variable de type AffichageModel qui va stocker les instanciations de la classe AfichageModel
		AffichageModel affichage;
//	Instanciation vide de la classe PreparedStatement qui est la classe qui materialiese
//	la requete SQL envoyee a la base de donnees
//	Cette instance est vide au depart (null)
		PreparedStatement pstmt = null;
//	variable de tyoe ResultSet qui stockera les donnees obtenues
//	suite a la requete SQL faite a la base de donnees
		ResultSet rs;

//	Instanciation nomme aListOfAffichage d'une liste d'instances de la classe AffichageModel
		List<AffichageModel> aListOfAffichage = new ArrayList<AffichageModel>();

//	On tente la requete (la requete sera expliquee de vive vois avec le schema de la base de donnees).
		try {

//	On prepare la requete en remplissant l'instance pstmt de la classe PrepareStatement:			
//	- on etablie la connexion avec la base de donnees (datasource.getConnection());
//	- on transmet la requete a base de donnees (datasource.prepareStatement(request)):
//	la variable request a ete donnee en parametre a la methode.
			pstmt = datasource.getConnection().prepareStatement(request);
//	On donne une valeur a la variable (materialisee par un ?)
//	qu'il y a dans la requete SQL donnee en parametre a la methode.
//	Le 1 donne la position du ? dans la requete,
//	et on remplace la ? par la valeur de la variable qui suite le 1 (ici id_utilisateur).
//	Chaque ? dans la requete sera resolu par un couple cle(position du ?)/valeur(valeur qu'on donne au ?)
//	S'il y a par exemple trois ? dans la requete, on aura la commande:
//	pstmt.setLong(1,X ; 2,Y ; 3,Z); a condition que les trois valeurs soient du type Long
//	on pourrait avoir:
//	pstmt.setLong(1,X ; 3,Z).setString(2,Y);
			pstmt.setLong(1, idRecherche);
			
//	On envoie dans le fichier log les infos sur le prepareStatement.
			logSQL(pstmt);

//	On execute la requete stokee dans l'instance pstmt de la classe PremareStatement
//	et on en stocke le resultat dans la variable rs de type ResultSet
			rs = pstmt.executeQuery();

//	Notre requete va renvoyer plusieurs resultats (on cherche a obtenir une liste!)
//	Donc on fait une boucle while qui va fonctionner tant que la requete renvoye un resultat:
//	la boucle tournen tant que rs.next() est vrai (tant qu'il reste un resultat a renvoyer)
			while (rs.next()) {
//	On stocke dans la variable affichage l'instanciation de la classe AffichageModel
//	obtenu grace a la methode getAffchageFromResultSet
//	avec les resultats de la requete (variable rs).
//	On donne egalement en parametre les colonnes dont les valeurs vont permettre
//	d'instancier les objets de la liste de type AffichageModel.
				affichage = getAffichageFromResultSet(rs, id_colonne, name_colonne);
//	On ajoute l'instance de la classe AffichageModel obtenue dans la liste aListOfAffichage.				
				aListOfAffichage.add(affichage);
			}

//	Si la connection avec la base de donnees ne se fait pas
//	On effectue le code ecrit dans le catch { } sans faire cracher l'application
		} catch (Exception e) {
//	Ecrit l'erreur dans un log
			e.printStackTrace();
//	Rajoute dans le log d'erreur que c'est une erreur SQL, la requete, et le message d'erreur
			log.error("SQL Error !:" + pstmt.toString(), e);
//	Renvoie une instance e de la classe Exception ???
			throw e;
//	Que la requete ait ete effectuee ou non avec succes, on execute le code qui est dans le finally { }
//	Ici, on ferme la connection avec la base de donnees
		} finally {
			pstmt.close();
		}

//	On renvoie la liste d'instances de la classe AfichageModel obtenue
		return aListOfAffichage;
	}
	
//	Methode pour instancier la classe AffichageModel en utilisant le resultat dune requete SQL
//	(instance rs de l'objet ResultSet) donne en paramètre de la methode
//	On cree une instance affichagfe de la classe AffichageModel
//	On la remplie avec les setters de la classe AffichageModel (setId et setName)
//	qui renvoie les valeurs donnee par les "getters" de la classe ResultSet
//	getLong() et getString() - en fonction du type de variable renvoyee -.
//	Ces "getters" ont en paramètre le nom de la colonne de la table de la base de donnees consulte
//	dont on veut la valeur stockee dans le ResultSet.
	/**
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	private AffichageModel getAffichageFromResultSet (ResultSet rs, String id_colonne, String name_colonne) throws Exception {
		AffichageModel affichage = new AffichageModel();
		affichage.setId(rs.getLong(id_colonne));
		affichage.setName(rs.getString(name_colonne));
		return affichage;
	}

//	Cette methode permet d'obtenir une instance de la classe VehiculeModel
//	qui correspond au vehicule dont on donne l'id_vehicule en parametre de la fonction
//	la mecanique generale reste la meme que precedemment.
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public VehiculeModel getData(Long id) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs;
		String sql;
		VehiculeModel vehicule = null;
		

		try {
			// Prepare the SQL query
			sql = "SELECT * FROM vehicule WHERE id_vehicule = ?";
			pstmt = datasource.getConnection().prepareStatement(sql);
			pstmt.setLong(1, id);
			
			// Log info
			logSQL(pstmt);

			// Run the query
			rs = pstmt.executeQuery();

//	Si la requete obtient un resultat (si l'id_vehicule fournit correspont a un vehicule dans la table)
//	on cnstruit l'instance de la classe VehiculeModel avec l'instance rs de la classe resultSet obtenue.
//	Mais, cette fois-ci, pour contruire vehicule, on a besoin de l'id du vehicule:
//	on le donne donc en parametre a la methode getVehculeFromResultSet().
			if (rs.next())
				vehicule = getVehiculeFromResultSet(rs, id);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("SQL Error !:" + pstmt.toString(), e);
			throw e;
		} finally {
			pstmt.close();
		}

		return vehicule;
	}

//	methode servant fabriquer une instance de la classe VehiculeModel a partir
//	d'un id-vehicule (Long id) et du resultat d'une requete SQl (ResultSet rs)
	/**
	 * @param rs
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private VehiculeModel getVehiculeFromResultSet(ResultSet rs, Long id) throws Exception {
//	On instancie un objet de type VehiculeModel.
		VehiculeModel vehicule = new VehiculeModel();
//	On initialise des variables qui correspondent aux requetes necessaires pour construire
//	pour contruire ls listes liees au vehicule: liste d'empreintes, d'adn, d'affaires.
		String requestAffaire = "SELECT DISTINCT affaire.id_affaire, nomAffaire FROM affaire"
				+ " JOIN affaire_has_vehicule ON affaire.id_affaire = affaire_has_vehicule.id_affaire"
				+ " JOIN vehicule ON affaire_has_vehicule.id_vehicule = vehicule.id_vehicule"
				+ " WHERE vehicule.id_vehicule = ?";
		String requestADN = "SELECT DISTINCT adn.id_ADN, nomADN FROM adn"
				+ " JOIN vehicule_has_adn ON adn.id_ADN = vehicule_has_adn.id_adn"
				+ " JOIN vehicule ON vehicule_has_adn.id_vehicule = vehicule.id_vehicule"
				+ " WHERE vehicule.id_vehicule = ?";
		String requestEmpreinte = "SELECT DISTINCT empreintes.id_empreintes, nomEmpreintes FROM empreintes"
				+ " JOIN vehicule_has_empreintes ON empreintes.id_empreintes = vehicule_has_empreintes.id_empreintes"
				+ " JOIN vehicule ON vehicule_has_empreintes.id_vehicule = vehicule.id_vehicule"
				+ " WHERE vehicule.id_vehicule = ?";
//	Requete pour trouver les propretaires du vehicule.
		String requestProtagonniste = "SELECT DISTINCT protagonniste.id_protagonniste, nomProtagonniste FROM protagonniste"
				+ " JOIN vehicule ON protagonniste.id_protagonniste = vehicule.id_protagonniste"
				+ " WHERE vehicule.id_vehicule = ?";
//	On construit l'instance du vehicule.
//	C'est le meme principe que dnas la methode getAffichageFrom ResultSet();
		vehicule.setId_vehicule(rs.getLong("id_vehicule"));
		vehicule.setTypeVehicule(rs.getString("typevehicule"));
		vehicule.setMarqueVehicule(rs.getString("marquevehicule"));
		vehicule.setModeleVehicule(rs.getString("modeleVehicule"));
		vehicule.setCouleurVehicule(rs.getString("couleurVehicule"));
		vehicule.setImmatriculationVehicule(rs.getString("immatriculationVehicule"));
//	Ici, aux setters, on donne comme valeur des listes d'instances de la classe AfichageModel,
//	que l'on construit avec la methode listAffichage().
		vehicule.setProtagonnisteVehicule(listAffichage(id, "id_protagonniste", "nomProtagonniste", requestProtagonniste));
		vehicule.setListAffaireVehicule(listAffichage(id, "id_affaire", "nomAffaire", requestAffaire));
		vehicule.setListAdnVehicule(listAffichage(id, "id_adn", "nomADN", requestADN));
		vehicule.setListEmpreinteVehicule(listAffichage(id, "id_empreintes", "nomEmpreintes", requestEmpreinte));
		return vehicule;
	}
	
//	Cette fonction sert a ecrire dans le fichier .log des infos sur les requetes
//	effectuees via les instance de la classe PreparedStatemlent
//	utile pour debuguer et comprendre pourquoi des requetes ne fonctionnent pas
	/**
	 * @param pstmt
	 */
	private void logSQL(PreparedStatement pstmt) {
		String sql;
		
		if (pstmt == null)
			return;
		
		sql = pstmt.toString().substring(pstmt.toString().indexOf(":") + 2);
		log.debug(sql);
	}

}
