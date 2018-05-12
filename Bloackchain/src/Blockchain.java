import java.util.ArrayList;
import java.util.List;

public class Blockchain {

	private List<Bloc> blocs;
	
	public Blockchain() {
		blocs = new ArrayList<>();
	}
	
	public Bloc dernierBloc() {
		if(blocs.isEmpty()) {
			return null;
		}
		else {
			return blocs.get(blocs.size() - 1);
		}
	}
	
	//Le premier bloc est valide
	public boolean isFirstBlockValid() {
		Bloc firstBlock = blocs.get(0);

		  if (firstBlock.getIdB() != 0) {
		    return false;
		  }

		  if (firstBlock.getPreviousHash() != null) {
		    return false;
		  }

		  if (firstBlock.getHash() == null || 
		      !Bloc.calculerHash(firstBlock).equals(firstBlock.getHash())) {
		    return false;
		  }

		  return true;
	}
	
	//Un nouveau bloc est valide avec le bloc précédent de la Blockchain
	public boolean isValidNewBlock(Bloc newBlock, Bloc previousBlock) {
	    if (newBlock != null  &&  previousBlock != null) {
	    	// Si les Id ne se suivent pas
	      if (previousBlock.getIdB() + 1 != newBlock.getIdB()) {
	        return false;
	      }
	      
	      // Si
	      if (newBlock.getPreviousHash() == null  ||  
		    !newBlock.getPreviousHash().equals(previousBlock.getHash())) {
	        return false;
	      }
	      // Si le hash du nouveau bloc est null ou si les hash ne correspondent pas
	      if (newBlock.getHash() == null  ||  
		    new String(Bloc.calculerHash(newBlock)).compareTo(new String(newBlock.getHash()))!=0 ) {
	        return false;
	      }

	      return true;
	    }
	    
	    // Si c'est le premier bloc de la chaine (donc le précédent est null)
	    if(newBlock != null  &&  previousBlock == null){
	    	 // Si le hash du nouveau bloc est null ou si les hash ne correspondent pas
		      if (newBlock.getHash() == null  ||  
			    new String(Bloc.calculerHash(newBlock)).compareTo(new String(newBlock.getHash()))!=0 ) {
		        return false;
		      }
		      return true;
	    }

	    return false;
	  }

	//La Blockchain est valide
	  public boolean isBlockChainValid() {
	    if (!isFirstBlockValid()) {
	      return false;
	    }

	    for (int i = 1; i < blocs.size(); i++) {
	      Bloc currentBlock = blocs.get(i);
	      Bloc previousBlock = blocs.get(i - 1);

	      if (!isValidNewBlock(currentBlock, previousBlock)) {
	        return false;
	      }
	    }

	    return true;
	  }

	
	public Bloc nouveauBloc(Operation[] listeOps, int idCreateur) {
		Bloc latestBloc = dernierBloc();
		Bloc add;
		//Si le bloc a ajouté est le premier du blockchain
		if(latestBloc == null) {
			add = new Bloc(0, listeOps, null, idCreateur);
		}
		else {
			add = new Bloc(latestBloc.getIdB() + 1, listeOps, latestBloc.getHash(), idCreateur);
		}
		return add; 
	}
	
	public void ajoutBloc(Bloc b) {
		blocs.add(b);
	}
	
	public Bloc[] getArrayBlocs(){
		return (Bloc[])blocs.toArray();
	}
	
	public List<Bloc> getBlocs() {
		return blocs;
	}

	public void afficheBlockchain() {
		for(int i = 0; i < blocs.size(); i++) {
			System.out.println("Bloc " + blocs.get(i).getIdB() + " :");
			System.out.println("	Hash : " + blocs.get(i).getHash());
			System.out.println("	Previous Hash : " + blocs.get(i).getPreviousHash());
			Operation[] listeOp = blocs.get(i).getOp();
			for(int j = 0; j < listeOp.length; j++) {
				System.out.println("		Opération " + j + " :" + listeOp[j].getOp());

			}
		}
	}
}
