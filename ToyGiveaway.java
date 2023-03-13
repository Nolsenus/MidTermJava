import java.util.*;

public class ToyGiveaway {

    private final HashMap<Toy, Double> toysWeights;
    private final Random random;

    public ToyGiveaway(Map<Toy, Double> toysWeights, Random random) {
        this.random = random;
        if (toysWeights.isEmpty()) {
            this.toysWeights = new HashMap<>();
            return;
        }
        double sum = 0;
        for (double weight : toysWeights.values()) {
            sum += weight;
        }
        if (sum != 100) {
            throw new IllegalArgumentException("Total weight is not 100.");
        }
        this.toysWeights = new HashMap<>(toysWeights);
    }

    public ToyGiveaway(Map<Toy, Double> toysWeights) {
        this(toysWeights, new Random());
    }

    public ToyGiveaway(Random random) {
        this.random = random;
        this.toysWeights = new HashMap<>();
    }

    public ToyGiveaway() {
        this(new Random());
    }

    public HashMap<Toy, Double> getToysWeights() {
        HashMap<Toy, Double> copy = new HashMap<>();
        for (Toy toy : toysWeights.keySet()) {
            copy.put(toy, toysWeights.get(toy));
        }
        return copy;
    }

    public double[] getWeights() {
        double[] weights = new double[toysWeights.size()];
        int i = 0;
        for (double weight : toysWeights.values()) {
            weights[i] = weight;
            i++;
        }
        return weights;
    }

    private double[] getRatios() {
        int size = toysWeights.size();
        double[] ratios = new double[size];
        double min = 100;
        for (double weight : toysWeights.values()) {
            if (min > weight) {
                min = weight;
            }
        }
        int i = 0;
        for (double weight : toysWeights.values()) {
            ratios[i] = weight / min;
            i++;
        }
        return ratios;
    }

    private void adjustWeights(double value) {
        double[] ratios = getRatios();
        double sum = 0;
        for (double ratio : ratios) {
            sum += ratio;
        }
        double onePart = value / sum;
        int i = 0;
        for (Toy oldToy : toysWeights.keySet()) {
            toysWeights.replace(oldToy, toysWeights.get(oldToy) - ratios[i] * onePart);
            i++;
        }
    }

    private void adjustWeightsIgnore(double value, Toy ignored) {
        if (!toysWeights.containsKey(ignored)) {
            adjustWeights(value);
        } else {
            double weight = toysWeights.get(ignored);
            toysWeights.remove(ignored);
            adjustWeights(value);
            toysWeights.put(ignored, weight);
        }
    }

    private void checkToyCount(Toy toy) {
        if (toy.getCount() == 0) {
            throw new IllegalArgumentException("Can not add toys with count 0 to giveaway.");
        }
    }

    public void addToy(Toy toy) {
        checkToyCount(toy);
        if (toysWeights.size() == 0) {
            toysWeights.put(toy, 100d);
        } else {
            double newToyWeight = 100d / (toysWeights.size() + 1);
            adjustWeights(newToyWeight);
            toysWeights.put(toy, newToyWeight);
        }
    }

    public void addToy(Toy toy, double weight) {
        checkToyCount(toy);
        if (toysWeights.size() == 0) {
            if (weight == 100) {
                toysWeights.put(toy, weight);
            } else {
                throw new IllegalArgumentException("The only toy in giveaway has to have a 100% chance ot be won.");
            }
        } else {
            adjustWeights(weight);
            toysWeights.put(toy, weight);
        }
    }

    public void addToyFromString(String string) {
        try {
            Toy toy = Toy.parseToy(string.substring(0, string.lastIndexOf(' ')));
            double weight = Double.parseDouble(string.substring(string.lastIndexOf(' ') + 1));
            addToy(toy, weight);
        } catch (IllegalArgumentException e) {
            try {
                Toy toy = Toy.parseToy(string);
                addToy(toy);
            } catch (IllegalArgumentException ex) {
                System.out.println("Не удалось обработать строку: " + string);
            }
        }
    }

    public void changeWeight(Toy toy, double newWeight) {
        if (!toysWeights.containsKey(toy)) {
            throw new IllegalArgumentException("Can not change weight of toy: not in giveaway.");
        }
        double oldWeight = toysWeights.get(toy);
        double diff = oldWeight - newWeight;
        if (diff == 0) {
            return;
        }
        if (diff < 0) {
            adjustWeightsIgnore(-diff, toy);
        } else {
            double diffPerToy = diff / (toysWeights.size() - 1);
            for (Toy t : toysWeights.keySet()) {
                if (!t.equals(toy)) {
                    toysWeights.replace(t, toysWeights.get(t) + diffPerToy);
                }
            }
        }
        toysWeights.replace(toy, newWeight);
    }

    public double getWeight(Toy toy) {
        if (toysWeights.containsKey(toy)) {
            return toysWeights.get(toy);
        }
        return -1;
    }

    public Set<Toy> getToySet() {
        return new HashSet<>(toysWeights.keySet());
    }

    public void removeToy(Toy toy) {
        if (!toysWeights.containsKey(toy)) {
            throw new IllegalArgumentException("Can not remove toy: not in giveaway.");
        }
        double weight = toysWeights.get(toy);
        toysWeights.remove(toy);
        if (!toysWeights.isEmpty()) {
            double weightPerToy = weight / (toysWeights.size());
            for (Toy t : toysWeights.keySet()) {
                toysWeights.replace(t, toysWeights.get(t) + weightPerToy);
            }
        }
    }

    public Toy giveaway() {
        double rand = 100 * random.nextDouble();
        double sum = 0;
        Toy result = null;
        for (Toy toy : toysWeights.keySet()) {
            sum += toysWeights.get(toy);
            if (rand <= sum) {
                result = toy;
                break;
            }
        }
        result.changeCount(-1);
        if (result.getCount() == 0) {
            removeToy(result);
        }
        return result;
    }

    public boolean isEmpty() {
        return toysWeights.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Список игрушек:\n");
        for (Toy toy : toysWeights.keySet()) {
            sb.append(String.format("%s, Шанс выпадения: %f\n", toy.toString(), toysWeights.get(toy)));
        }
        return sb.toString();
    }
}
