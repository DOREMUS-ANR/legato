package legato.cluster;

import java.util.Arrays;

public class Vecteur {
	
	public final double[] elements; // un tableau constitu� des �l�ments de type "double"

	public Vecteur(int elts) { // Construire un vecteur avec les �l�ments "elts"
		elements = new double[elts];
	}

	public Vecteur(Vecteur vector) { // Construire un vecteur en copiant les �l�ments du vecteur fourni "vector"
		elements = Arrays.copyOf(vector.elements, vector.elements.length);
	}

	public Vecteur add(Vecteur operand) { // Ajouter le vecteur fourni "operand" � ce vecteur
		Vecteur result = new Vecteur(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, get(i) + operand.get(i));
		}
		return result;
	}
	
	public Vecteur divide(double divisor) { // Diviser ce vecteur par le vecteur fourni "divisor"
		Vecteur result = new Vecteur(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, get(i) / divisor);
		}
		return result;
	}

	public double get(int i) { // obtenir un �l�ment du vecteur en sp�cifiant l'index o� se trouve l'�l�ment
		return elements[i];
	}

	public void increment(int i) { // Incr�menter �l�ment par �l�ment � un �l�ment sp�cifi� de ce vecteur
		set(i, get(i) + 1);
	}

	public double innerProduct(Vecteur vector) { // Calculer le produit scalaire de ce vecteur avec le vecteur fourni "vector"
		double innerProduct = 0;
		for (int i = 0; i < elements.length; i++) {
			innerProduct += get(i) * vector.get(i);
		}
		return innerProduct;
	}

	public Vecteur invert() { // inverser �l�ment par �l�ment de ce vecteur
		Vecteur result = new Vecteur(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, 1 / get(i));
		}
		return result;
	}

	public Vecteur log() { // le log des �l�ments de ce vecteur
		Vecteur result = new Vecteur(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, Math.log(get(i)));
		}
		return result;
	}

	public double max() { // obtenir l'�lement qui a la valeur maximale
		double maxValue = Double.MIN_VALUE;
		for (int i = 0; i < elements.length; i++) {
			maxValue = Math.max(maxValue, get(i));
		}
		return maxValue;
	}

	public Vecteur multiply(double multiplier) { // multiplier les �l�ments du vecteur par la valeur "multiplier" 
		Vecteur result = new Vecteur(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, get(i) * multiplier);
		}
		return result;
	}

	public Vecteur multiply(Vecteur multiplier) { // multiplier le vecteur avec un autre vecteur "multiplier"
		Vecteur result = new Vecteur(size());
		for (int i = 0; i < elements.length; i++) {
			if (get(i) == 0 || multiplier.get(i) == 0) {
				result.set(i, 0);
			} else {
				result.set(i, multiplier.get(i) * get(i));
			}
		}
		return result;
	}

	public double norm() { //Calculer la norme L2 de ce vecteur
		double normSquared = 0.0;
		for (int i = 0; i < elements.length; i++) {
			normSquared += get(i) * get(i);
		}
		return Math.sqrt(normSquared);
	}

	public void set(int i, double value) { // mettre un �l�ment � ce vecteur
		elements[i] = value;
	}

	public int size() { // nombre d'�l�ments de ce vecteur
		return elements.length;
	}
}