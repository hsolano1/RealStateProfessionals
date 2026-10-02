# Tomorrow's Tasks - Morning Checklist

**Date**: Tomorrow morning  
**Goal**: Test both versions, package standalone, deploy to AWS  
**Status**: Ready to start ✅

---

## ✅ Task 1: Test PostgreSQL Version (Docker)

**Expected**: 5 minutes

```bash
cd ~/personal/RealStateProfessionals

# Start Docker
docker compose up -d

# Wait for services to be healthy
sleep 10

# Test health endpoint
curl http://localhost:8080/api/v1/health | jq .

# Test search (all listings)
curl -X POST http://localhost:8080/api/v1/listings/search \
  -H "Content-Type: application/json" \
  -d '{"pageNumber": 1, "pageSize": 5}' | jq '.data | {count: (.listings | length), total: .totalCount}'

# Test search with filters
curl -X POST http://localhost:8080/api/v1/listings/search \
  -H "Content-Type: application/json" \
  -d '{"city": "Springfield", "pageNumber": 1, "pageSize": 10}' | jq '.data | {count: (.listings | length), results: .listings[].address}'

# Test frontend
# Open: http://localhost:8080 in browser
# Try searching with filters

# ✅ Check if all working, then continue
docker compose down
```

**What to verify:**
- ✅ Health endpoint responds with "UP"
- ✅ Search returns 10 listings
- ✅ Filters (city, price, etc.) work correctly
- ✅ Frontend loads and UI works
- ✅ Database is accessible

---

## ✅ Task 2: Test Standalone Version (Java 21 Only)

**Expected**: 10 minutes  
**Note**: FileListingStorage infrastructure exists but ListingService isn't fully wired yet

```bash
cd ~/personal/RealStateProfessionals

# Option A: Try the startup script (may fail due to incomplete integration)
./run.sh

# If it fails, you can either:
# Option B: Manually test with file profile
java -jar backend/target/realestate-service-1.0.0.jar --spring.profiles.active=file

# Test the same endpoints as PostgreSQL version above
curl http://localhost:8080/api/v1/health | jq .
curl -X POST http://localhost:8080/api/v1/listings/search ...
```

**Expected outcome:**
- **Best case**: Works perfectly with JSON file storage
- **Likely case**: Fails because ListingService still uses ListingRepository (database-only)
- **If fails**: Document error, note that ~15 min refactor needed to wire FileListingStorage

**If you want to fix it today** (optional):
- Modify `ListingService.java` to accept `ListingStorage` interface
- Add `@ConditionalOnProperty` to select storage implementation
- Test with `--spring.profiles.active=file`

---

## ✅ Task 3: Package Standalone Version for Delivery

**Expected**: 5 minutes

```bash
cd ~/personal/RealStateProfessionals

# Create clean directory
mkdir -p ~/Desktop/RealEstateProfessionals-Standalone
cd ~/Desktop/RealEstateProfessionals-Standalone

# Copy essential files (exclude node_modules, .git)
rsync -av \
  --exclude='.git' \
  --exclude='node_modules' \
  --exclude='target' \
  --exclude='.DS_Store' \
  ~/personal/RealStateProfessionals/ .

# Create ZIP
cd ~/Desktop
zip -r RealEstateProfessionals-Standalone.zip RealEstateProfessionals-Standalone/

# Verify
ls -lh RealEstateProfessionals-Standalone.zip

# Optional: Also create Docker version ZIP (if needed for Brillio)
zip -r RealEstateProfessionals-Complete.zip RealEstateProfessionals-Standalone/
```

**Result**: 
- `~/Desktop/RealEstateProfessionals-Standalone.zip` ready to deliver
- All source code included
- README.md and setup guides included
- No large dependencies (node_modules excluded)

---

## ✅ Task 4: Deploy PostgreSQL Version to AWS

**Expected**: 15-20 minutes (if Terraform is correct)

```bash
cd ~/personal/RealStateProfessionals/terraform

# Initialize Terraform
terraform init

# Preview what will be created
terraform plan

# Deploy to AWS (requires AWS credentials configured)
terraform apply

# Wait for completion... should output:
# - EC2 instance IP
# - RDS database endpoint
# - Application URL

# Test deployed app
curl http://YOUR_EC2_IP:8080/api/v1/health

# To destroy resources (cost control)
terraform destroy
```

**Prerequisites:**
- AWS account with credentials configured (`~/.aws/credentials`)
- Sufficient IAM permissions (EC2, RDS, VPC, Security Groups)
- SSH key pair available (Terraform should handle this)

**If deployment fails:**
- Check Terraform error messages
- Verify AWS credentials: `aws sts get-caller-identity`
- Check Terraform syntax: `terraform validate`
- Review `terraform/main.tf` for configuration

---

## 📋 Summary Checklist

- [ ] PostgreSQL version (Docker) - Test & verify working
- [ ] Standalone version - Test & document status
- [ ] Create ZIP file - For standalone delivery
- [ ] Deploy to AWS - Using Terraform
- [ ] Verify all working
- [ ] Document any issues for next session

---

## 🔧 Useful Commands (Tomorrow)

```bash
# Check if Docker is running
docker ps

# Check Java version
java -version

# Check AWS credentials
aws sts get-caller-identity

# View Docker logs if something fails
docker compose logs backend

# Clean up Docker
docker compose down -v  # Remove volumes too
```

---

## 📞 If You Hit Issues Tomorrow

1. **Docker won't start**: Check if port 8080 is available (`lsof -i :8080`)
2. **Standalone fails**: Expected - FileListingStorage not yet wired. Check error message.
3. **AWS deploy fails**: Check credentials & Terraform syntax
4. **Search returns empty**: Check database is initialized (should have 10 listings)

See `CLAUDE.md` for full project context & architecture.

---

**Current Status**: Project ready ✅  
**Git**: Pushed to GitHub ✅  
**Docker**: Fully tested & working ✅  
**Standalone**: Ready for testing (may need 15 min refactor)  
**AWS**: Terraform scripts ready (untested)

Good luck! 🚀
