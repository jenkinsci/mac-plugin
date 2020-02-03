package fr.edf.jenkins.plugins.mac.ssh.key

import com.trilead.ssh2.KnownHosts

class MacHostKey {

    String algorithm
    byte[] key

    MacHostKey(String algorithm, byte[] key) {
        this.algorithm = algorithm
        this.key = key
    }

    public String getFingerprint() {
        return KnownHosts.createHexFingerprint(algorithm, key.clone());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
        result = prime * result + Arrays.hashCode(key);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MacHostKey other = (MacHostKey) obj;
        if (algorithm == null) {
            if (other.algorithm != null)
                return false;
        } else if (!algorithm.equals(other.algorithm))
            return false;
        if (!Arrays.equals(key, other.key))
            return false;
        return true;
    }
}
